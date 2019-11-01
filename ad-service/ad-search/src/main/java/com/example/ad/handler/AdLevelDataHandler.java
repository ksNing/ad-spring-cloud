package com.example.ad.handler;

import com.alibaba.fastjson.JSON;
import com.example.ad.index.DataTable;
import com.example.ad.index.IndexAware;
import com.example.ad.index.adplan.AdPlanIndex;
import com.example.ad.index.adplan.AdPlanObject;
import com.example.ad.index.adunit.AdUnitIndex;
import com.example.ad.index.adunit.AdUnitObject;
import com.example.ad.index.creative.CreativeIndex;
import com.example.ad.index.creative.CreativeObject;
import com.example.ad.index.creativeunit.CreativeUnitIndex;
import com.example.ad.index.creativeunit.CreativeUnitObject;
import com.example.ad.index.district.UnitDistrictIndex;
import com.example.ad.index.interest.UnitItIndex;
import com.example.ad.index.keyword.UnitKeywordIndex;
import com.example.ad.mysql.constant.OpType;
import com.example.ad.utils.CommonUtils;
import com.example.ad.vo.dump.table.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.example.ad.utils.CommonUtils.stringContact;

/**
 * 对二级索引，三级索引，四级索引的一些操作
 */
@Slf4j
public class AdLevelDataHandler {
    public static void handleLevel2(AdPlanTable planTable, OpType type) {
        AdPlanObject planObject = new AdPlanObject(
                planTable.getId(),
                planTable.getUserId(),
                planTable.getPlanStatus(),
                planTable.getStartDate(),
                planTable.getEndDate()
        );
        handleBinlogEvent(DataTable.of(AdPlanIndex.class),
                planObject.getPlanId(),
                planObject,
                type);
    }

    public static void handleLevel2(AdCreativeTable adCreativeTable, OpType type) {
        CreativeObject creativeObject = new CreativeObject(
                adCreativeTable.getAdId(),
                adCreativeTable.getName(),
                adCreativeTable.getType(),
                adCreativeTable.getMaterialType(),
                adCreativeTable.getHeight(),
                adCreativeTable.getWidth(),
                adCreativeTable.getAuditStatus(),
                adCreativeTable.getAdUrl()
        );
        handleBinlogEvent(DataTable.of(CreativeIndex.class),
                creativeObject.getAdId(),
                creativeObject,
                type);
    }

    public static void handleLevel3(AdUnitTable adUnitTable, OpType type) {
        AdPlanObject adPlanObject = DataTable.of(AdPlanIndex.class).get(adUnitTable.getPlanId());
        if(null == adPlanObject) {
            log.info("handleLevel3 found adPlanObject error: {}",
                    adUnitTable.getPlanId());
        }
        AdUnitObject adUnitObject = new AdUnitObject(
                adUnitTable.getUnitId(),
                adUnitTable.getUnitStatus(),
                adUnitTable.getPositionType(),
                adUnitTable.getPlanId(),
                adPlanObject
        );
        handleBinlogEvent(DataTable.of(AdUnitIndex.class),
                adUnitObject.getUnitId(),
                adUnitObject,
                type);
    }

    public static void handleLevel3(AdCreativeUnitTable creativeUnitTable, OpType type) {
        if (type == OpType.UPDATE) {
            log.error("CreativeUnitIndex not support update");
            return;
        }

        AdUnitObject adUnitObject = DataTable.of(AdUnitIndex.class).
                get(creativeUnitTable.getUnitId());
        CreativeObject creativeObject = DataTable.of(CreativeIndex.class).
                get(creativeUnitTable.getAdId());
        if(null == adUnitObject || null == creativeObject) {
            log.info("AdCreativeUnitTable index error: {}",
                    JSON.toJSONString(creativeUnitTable));
        }
        CreativeUnitObject creativeUnitObject = new CreativeUnitObject(
                adUnitObject.getUnitId(),
                creativeObject.getAdId()
        );
        handleBinlogEvent(DataTable.of(CreativeUnitIndex.class),
                stringContact(creativeUnitObject.getAdId().toString(),
                        creativeUnitObject.getUnitId().toString()),
                creativeUnitObject,
                type);

    }

    public static void handleLevel4(AdUnitDistrictTable unitDistrictTable, OpType type) {
        if (type == OpType.UPDATE) {
            log.error("AdUnitDistrictIndex not support update");
            return;
        }
        AdUnitObject adUnitObject = DataTable.of(AdUnitIndex.class).get(unitDistrictTable.getUnitId());
        if(null == adUnitObject) {
            log.info("AdUnitDistrictTable index error: {}",
                    JSON.toJSONString(unitDistrictTable));
            return;
        }
        String key = CommonUtils.stringContact(unitDistrictTable.getProvince(),unitDistrictTable.getCity());
        Set<Long> value = new HashSet<>(Collections.singleton(unitDistrictTable.getUnitId()));
        handleBinlogEvent(DataTable.of(UnitDistrictIndex.class),
                key,
                value,
                type);
    }

    public static void handleLevel4(AdUnitItTable unitItTable, OpType type) {
        if(type == OpType.UPDATE) {
            log.error("AdUnitItIndex not support update");
            return;
        }
        AdUnitObject adUnitObject = DataTable.of(AdUnitIndex.class).get(unitItTable.getUnitId());
        if(null == adUnitObject) {
            log.info("AdUnitItTable index error: {}",
                    unitItTable);
            return;
        }
        String key = unitItTable.getItTag();
        Set<Long> value = new HashSet<>(Collections.singleton(unitItTable.getUnitId()));
        handleBinlogEvent(DataTable.of(UnitItIndex.class),
                key,
                value,
                type);
    }

    public static void handleLevel4(AdUnitKeywordTable keywordTable,
                                    OpType type) {

        if (type == OpType.UPDATE) {
            log.error("keyword index can not support update");
            return;
        }

        AdUnitObject unitObject = DataTable.of(
                AdUnitIndex.class
        ).get(keywordTable.getUnitId());
        if (unitObject == null) {
            log.error("AdUnitKeywordTable index error: {}",
                    keywordTable.getUnitId());
            return;
        }

        Set<Long> value = new HashSet<>(
                Collections.singleton(keywordTable.getUnitId())
        );
        handleBinlogEvent(
                DataTable.of(UnitKeywordIndex.class),
                keywordTable.getKeyword(),
                value,
                type
        );
    }




    private static <K,V> void handleBinlogEvent(IndexAware<K,V> index,
                                                K key,
                                                V value,
                                                OpType type) {
        switch(type) {
            case ADD:
                index.add(key,value);
                break;
            case UPDATE:
                index.update(key,value);
                break;
            case DELETE:
                index.delete(key,value);
                break;
            default:
                break;
        }

    }
}
