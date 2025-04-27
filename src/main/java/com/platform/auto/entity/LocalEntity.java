package com.platform.auto.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 *
 */

@Data
public class LocalEntity {

    public String time;

    public String filterTableNameText;
    public String configJsonName;
    public String selectedJsonName;

    public String selectedDbName;
    public Map<String, String> configMap;

    // 数据处理好了
    public List<DbEntity> dbInfoList;

    @Data
    public static class TableEntity {

        public String tableSchema;
        public String tableName;
    }


}
