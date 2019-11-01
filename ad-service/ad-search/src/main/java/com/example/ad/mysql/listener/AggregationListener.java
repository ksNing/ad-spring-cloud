package com.example.ad.mysql.listener;

import com.example.ad.mysql.TemplateHolder;
import com.example.ad.mysql.dto.BinlogRowData;
import com.example.ad.mysql.dto.TableTemplate;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 触发监听，监听binlog，产生的event转换成binLogData类型
 */
@Slf4j
@Component
public class AggregationListener implements BinaryLogClient.EventListener {
    private String dbName;
    private String tableName;

    private Map<String, Ilistener> listenerMap = new HashMap<>();


    private final TemplateHolder templateHolder;

    @Autowired
    public AggregationListener(TemplateHolder templateHolder) {
       this.templateHolder = templateHolder;
    }

    public String genKey(String dbName, String tableName) {
        return dbName + ":" + tableName;
    }

    public void register(String _dbName, String _tableName, Ilistener ilistener) {
        log.info("register : {} - {}", _dbName, _tableName);
        this.listenerMap.put(genKey(_dbName,_tableName),ilistener);
    }

    private List<Serializable[]> getAfterValues(EventData eventData) {
        if(eventData instanceof WriteRowsEventData) {
            return ((WriteRowsEventData) eventData).getRows();
        }
        if(eventData instanceof DeleteRowsEventData) {
            return ((DeleteRowsEventData) eventData).getRows();
        }
        if(eventData instanceof UpdateRowsEventData) {
            return ((UpdateRowsEventData) eventData).getRows().stream()
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private BinlogRowData buildRowData(EventData eventData) {
        TableTemplate table = templateHolder.getTable(tableName);
        if(null == table) {
            log.warn("table {} not found", tableName);
            return null;
        }
        List<Map<String, String>> afterMapList = new ArrayList<>();
        for (Serializable[] after : getAfterValues(eventData)) {
            Map<String,String> afterMap = new HashMap<>();
            int len = after.length;
            for(int i = 0 ; i < len ; i ++) {
                String colName = table.getPosMap().get(i);
                if(null == colName) {
                    log.debug("ignore position: {}", i);
                    continue;
                }
                String colValue = after[i].toString();
                afterMap.put(colName,colValue);
            }
            afterMapList.add(afterMap);
        }
        BinlogRowData data = new BinlogRowData();
        data.setAfter(afterMapList);
        data.setTable(table);

        return data;
    }


    @Override
    public void onEvent(Event event) {
        EventType type = event.getHeader().getEventType();
        log.info("event type: {}", type);

        if(type == EventType.TABLE_MAP) {
            TableMapEventData data = event.getData();
            this.dbName = data.getDatabase();
            this.tableName = data.getTable();
            return;
        }

        if(type != EventType.EXT_DELETE_ROWS
                && type != EventType.EXT_UPDATE_ROWS
                && type != EventType.EXT_WRITE_ROWS) {
            return;
        }

        //检验表名和库名是否已经完成填充
        if(StringUtils.isEmpty(dbName) || StringUtils.isEmpty(tableName)) {
            log.error("no meta data event");
            return;
        }

        //找出对应表有兴趣的监听器
        String key = genKey(this.dbName, this.tableName);
        Ilistener ilistener = listenerMap.get(key);
        if(null == ilistener) {
            log.debug("skip {}", key);
            return;
        }

        log.info("trigger event: {}", type.name());

        //将event转换为自己定义的binlogData类型
        try {
            BinlogRowData binlogRowData = buildRowData(event.getData());
            if(binlogRowData == null) {
                return;
            }
            binlogRowData.setEventType(type);
            ilistener.onEvent(binlogRowData);

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        } finally {
            this.dbName = "";
            this.tableName = "";
        }
    }
}
