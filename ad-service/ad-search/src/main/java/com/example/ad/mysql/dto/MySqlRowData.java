package com.example.ad.mysql.dto;

import com.example.ad.mysql.constant.OpType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 索引层面的实体类
 */
public class MySqlRowData {
    private String tableName;
    private String level;
    private OpType opType;
    /**
     * 首先是个list，每个值是一个map，key为colname，value为colValue
     */
    private List<Map<String, String>> filedValueMap = new ArrayList<>();
}
