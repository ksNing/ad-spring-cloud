package com.example.ad.mysql.listener;

import com.example.ad.mysql.constant.Constant;
import com.example.ad.mysql.constant.OpType;
import com.example.ad.mysql.dto.BinlogRowData;
import com.example.ad.mysql.dto.MySqlRowData;
import com.example.ad.mysql.dto.TableTemplate;
import com.example.ad.sender.ISender;
import com.github.shyiko.mysql.binlog.event.EventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class IncrementListener implements Ilistener {
    @Resource
    private ISender sender;

    private final AggregationListener aggregationListener;

    @Autowired
    public IncrementListener(AggregationListener aggregationListener) {
        this.aggregationListener = aggregationListener;
    }


    @Override
    public void register() {
        log.info("IncrementListener register db and table info");
        Constant.table2Db.forEach((k,v)
                -> aggregationListener.register(v,k,this));

    }

    @Override
    public void onEvent(BinlogRowData eventData) {
        TableTemplate table = eventData.getTable();
        EventType eventType = eventData.getEventType();

        //包装成最后需要投递的数据
        MySqlRowData mySqlRowData = new MySqlRowData();
        mySqlRowData.setTableName(table.getTableName());
        mySqlRowData.setLevel(eventData.getTable().getLevel());
        OpType opType = OpType.to(eventType);
        mySqlRowData.setOpType(opType);

        //取出模版中该操作对应的字段列表
        List<String> filedList = table.getOpTypeFiledSetMap().get(opType);
        if(null == filedList) {
            log.warn("{} not support for {}", opType,table.getTableName());
            return;
        }

        for (Map<String, String> afterMap : eventData.getAfter()) {
            Map<String,String> _afterMap = new HashMap<>();
            for (Map.Entry<String, String> entry : afterMap.entrySet()) {
                String colName = entry.getKey();
                String colValue = entry.getValue();
                _afterMap.put(colName,colValue);
            }
            mySqlRowData.getFiledValueMap().add(_afterMap);

        }
        sender.sender(mySqlRowData);
    }
}
