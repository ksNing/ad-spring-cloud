package com.example.ad.mysql.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class JsonTable {
    private String tableName;
    private Integer level;
    private List<Column> insert;
    private List<Column> update;
    private List<Column> delete;


    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Column {
        private String column;
    }
}

