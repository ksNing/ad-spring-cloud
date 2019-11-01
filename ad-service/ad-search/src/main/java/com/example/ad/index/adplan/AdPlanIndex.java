package com.example.ad.index.adplan;

import com.example.ad.index.IndexAware;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class AdPlanIndex implements IndexAware<Long,AdPlanObject> {
    private static Map<Long,AdPlanObject> map;
    static {
        map = new ConcurrentHashMap<>();
    }
    @Override
    public AdPlanObject get(Long key) {
        return map.get(key);
    }

    @Override
    public void add(Long key, AdPlanObject value) {
        log.info("before add map: {}",map);
        map.put(key,value);
        log.info("after add map: {}", map);

    }

    @Override
    public void update(Long key, AdPlanObject value) {
        log.info("before update: {}", map);
        AdPlanObject oldObject = map.get(key);
        if(null == oldObject) {
            map.put(key,value);
        } else {
            oldObject.update(value);
        }
        log.info("after update: {}",map);
    }

    @Override
    public void delete(Long key, AdPlanObject value) {
        log.info("before delete: {}", map);
        map.remove(key);
        log.info("after delete: {}", map);
    }
}
