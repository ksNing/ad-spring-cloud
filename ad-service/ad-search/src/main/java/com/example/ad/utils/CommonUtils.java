package com.example.ad.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public class CommonUtils {
    /**
     * 此方法的含义：入参为一个key，一个map，一个工厂构造器，
     * 当map中不包含key的时候，就会利用factory创建一个V
     * @param key
     * @param map
     * @param factory
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V>V getOrCreate(K key, Map<K,V> map,
                                Supplier<V> factory) {
        return map.computeIfAbsent(key,k -> factory.get());
    }

    public static String stringContact(String...args) {
        StringBuilder sb = new StringBuilder();
        for(String arg: args) {
            sb.append(arg);
            sb.append("-");
        }
        return sb.toString().substring(0,sb.length() - 1);
    }

    // Tue Jan 01 08:00:00 CST 2019
    public static Date parseStringDate(String dateString) {
        try {
            DateFormat dateFormat = new SimpleDateFormat(
                    "EEE MMM dd HH:mm:ss zzz yyyy",
                    Locale.US
            );
            return DateUtils.addHours(dateFormat.parse(dateString), -8);
        } catch (ParseException e) {
            log.error("parseStringDate error: {}", dateString);
            return null;
        }
    }
}
