package com.platform.auto.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

@Data
public class LocalEntity {

    public Long time;

    public String filterDbNameText;
    public String selectedDbName;

    public String filterTableNameText;

    public List<TableEntity> tableList;

    public String selectedTableName;

    // 数据处理好了
    public List<DbEntity> dbInfoList;

    @Data
    public static class TableEntity {

        public String tableSchema;
        public String tableName;
    }


}
