package com.example.ad.index.district;

import com.example.ad.index.IndexAware;
import com.example.ad.search.vo.feature.DistrictFeature;
import com.example.ad.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UnitDistrictIndex implements IndexAware<String, Set<Long>> {
    private static Map<String, Set<Long>> districtUnitMap;
    private static Map<Long, Set<String>> unitDistrictMap;
    static {
        districtUnitMap = new ConcurrentHashMap<>();
        unitDistrictMap = new ConcurrentHashMap<>();
    }
    @Override
    public Set<Long> get(String key) {
        if (StringUtils.isEmpty(key)) {
            return Collections.emptySet();
        }

        Set<Long> result = districtUnitMap.get(key);
        if (result == null) {
            return Collections.emptySet();
        }

        return result;
    }

    @Override
    public void add(String key, Set<Long> value) {
        log.info("before add: {}",districtUnitMap);
        Set<Long> unitIdSet = CommonUtils.getOrCreate(key,districtUnitMap,
                ConcurrentSkipListSet::new);
        unitIdSet.addAll(value);
        for(Long unitId: unitIdSet) {
            Set<String> districtSet = CommonUtils.getOrCreate(unitId,unitDistrictMap,
                    ConcurrentSkipListSet::new);
            districtSet.add(key);
        }
        log.info("after add: {}",districtUnitMap);
    }

    @Override
    public void update(String key, Set<Long> value) {
        log.error("district index can not support update");

    }

    @Override
    public void delete(String key, Set<Long> value) {
        log.info("UnitDistrictIndex, before delete: {}", unitDistrictMap);

        Set<Long> unitIdSet = CommonUtils.getOrCreate(key,districtUnitMap,
                ConcurrentSkipListSet::new);
        unitIdSet.removeAll(value);
        for (Long unitId : value) {

            Set<String> districts = CommonUtils.getOrCreate(
                    unitId, unitDistrictMap,
                    ConcurrentSkipListSet::new
            );
            districts.remove(key);
        }
        log.info("UnitDistrictIndex, after delete: {}", unitDistrictMap);

    }

    public boolean match(Long unitId, List<DistrictFeature.ProvinceAndCity> districts) {
        if (unitDistrictMap.containsKey(unitId)
                && CollectionUtils.isNotEmpty(unitDistrictMap.get(unitId))) {

            Set<String> unitDistricts = unitDistrictMap.get(unitId);

            List<String> targetDistricts = districts.stream().map(
                    d -> CommonUtils.stringContact(d.getProvince(), d.getCity()))
                    .collect(Collectors.toList());
            return CollectionUtils.isSubCollection(targetDistricts, unitDistricts);
        }
        return false;
    }


}
