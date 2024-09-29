package com.platform.auto.entity;

import lombok.Data;

import java.util.List;

/**
 *
 */

@Data
public class LocalEntity {

    public String time;

    public String filterTableNameText;

    public String selectedDbName;

    // 数据处理好了
    public List<DbEntity> dbInfoList;

    @Data
    public static class TableEntity {

        public String tableSchema;
        public String tableName;
    }


}
