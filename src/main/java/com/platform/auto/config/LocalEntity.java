package com.platform.auto.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 *
 */

@Data
public class LocalEntity {

    public Long time;
    public List<TableEntity> tableList;

    @Data
    public static class TableEntity {

        public String tableSchema;
        public String tableName;
    }


}
