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

    /**
     *
     **/
    // 运行插件的时候, 选中的配置文件
    public String configJsonName;
    // 在多选框中, 选中的配置文件的 key
    public String selectedJsonName;
    // 配置文件
    public Map<String, String> configMap;
    /**
     *
     **/

    /**
     *
     **/
    // 运行插件的时候, 选中的 db name
    public String selectedDbName;
    // 在多选框中, 选中的配置文件的 key
    public String selectedDbKey;
    // 配置文件
    public Map<String, String> dbMap;
    /**
     *
     **/

    // 数据处理好了
    public List<DbEntity> dbInfoList;

    @Data
    public static class TableEntity {

        public String tableSchema;
        public String tableName;
    }


}
