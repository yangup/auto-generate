package com.platform.auto.config;

import lombok.Data;

import java.util.List;

/**
 *
 */

@Data
public class LocalEntity {

    public Long time;

    public String filterDbNameText;

    public String filterTableNameText;

    public List<TableEntity> tableList;

    @Data
    public static class TableEntity {

        public String tableSchema;
        public String tableName;
    }


}
