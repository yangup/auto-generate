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
    @JsonProperty("entity_field_is_public")
    public boolean entityFieldIsPublic;

    @JsonProperty("add_one_row_for_id")
    public String addOneRowForId;

    /**
     * 文件名后缀
     **/
    @JsonProperty("file_name_suffix")
    public String fileNameSuffix;

    @JsonProperty("need_json_property")
    public boolean needJsonProperty;

    /**
     * 实体类是否使用 生成 static 方法
     **/
    @JsonProperty("generate_static_method")
    public String generateStaticMethod;

    /**
     * 生成的文件是否按照 table 存储
     **/
    @JsonProperty("store_by_table")
    public boolean storeByTable;

    @JsonProperty("is_useful")
    public boolean isUseful;

    @JsonProperty("is_front")
    public boolean isFront;

    public PathEntity path;

}
