package com.platform.auto.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ConfigInfoEntity {

    public String template;

    public String remark;

    public String type;

    /**
     * 实体类是否使用 public 修饰
     **/
    @JsonProperty("field_is_public")
    public Boolean fieldIsPublic;

    @JsonProperty("add_one_row_for_id")
    public String addOneRowForId;

    /**
     * 文件名后缀
     **/
    @JsonProperty("file_name_suffix")
    public String fileNameSuffix;

    /**
     * 是否需要展示, @JsonProperty
     * **/
    @JsonProperty("show_json_property")
    public Boolean showJsonProperty;

    /**
     * 实体类是否使用 生成 static 方法
     **/
    @JsonProperty("generate_static_method")
    public Boolean generateStaticMethod;

    /**
     * 生成的文件是否按照 table 存储
     **/
    @JsonProperty("store_by_table")
    public Boolean storeByTable;

    @JsonProperty("is_useful")
    public Boolean isUseful;

    @JsonProperty("is_front")
    public Boolean isFront;

    public PathEntity path;

}
