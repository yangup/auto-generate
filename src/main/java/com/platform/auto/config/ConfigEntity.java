package com.platform.auto.config;

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
    public String storeByTable;

    /**
     * 表明称前缀, 需要去掉
     **/
    @JsonProperty("remove_prefix")
    public List<String> removePrefix;

    public Jdbc jdbc;

    public List<Info> info;


    @Data
    public static class Info {

        public String template;

        public String remark;

        public String type;

        @JsonProperty("entity_field_is_public")
        public String entityFieldIsPublic;

        @JsonProperty("entity_generate_static_method")
        public String entityGenerateStaticMethod;

        public Path path;

    }

    @Data
    public static class Path {

        @JsonProperty("project_name")
        public String projectName;

        @JsonProperty("package_name")
        public String packageName;
    }

    @Data
    public static class Jdbc {

        public String clazz;
        public String url;
        public String username;
        public String password;
        public String database;
    }


}
