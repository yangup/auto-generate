package com.platform.auto.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
public class ConfigEntity {

    public String author;

    /**
     * 是否按照 table 存储
     **/
    @JsonProperty("store_by_table")
    public Boolean storeByTable;

    @JsonProperty("type_to_java_data")
    public String typeToJavaData;

    /**
     * 表明称前缀, 需要去掉
     **/
    @JsonProperty("remove_prefix")
    public List<String> removePrefix;

    public JdbcEntity jdbc;

    public List<ConfigInfoEntity> info;

    // 数据处理好了
    public List<DbEntity> dbInfoList;


}
