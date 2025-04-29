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

    /**
     *
     **/
    // 在多选框中, 选中的配置文件的 key
    public String selectedJsonName;
    // 配置文件
    public List<String> configList;

    // 数据处理好了
    public List<DbEntity> dbInfoList;

    @Data
    public static class TableEntity {

        public String tableSchema;
        public String tableName;
    }


}
