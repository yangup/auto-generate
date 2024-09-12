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

    @JsonProperty("store_by_table")
    public String storeByTable;

    @JsonProperty("generate_location")
    public GenerateLocation generateLocation;

    public Jdbc jdbc;

    @JsonProperty("table_names")
    public List<String> tableNames;

    public Template template;


    @Data
    public static class ProjectPackage {

        @JsonProperty("project_name")
        public String projectName;

        @JsonProperty("package")
        public String packageName;

        @JsonProperty("entity_generate_static_method")
        public String entityGenerateStaticMethod;

        @JsonProperty("entity_field_is_public")
        public String entityFieldIsPublic;
    }


    @Data
    public static class GenerateLocation {

        @JsonProperty("db")
        public ProjectPackage db;

        @JsonProperty("constant")
        public ProjectPackage constant;

        @JsonProperty("entity")
        public ProjectPackage entity;

        @JsonProperty("mapper")
        public ProjectPackage mapper;

        @JsonProperty("service")
        public ProjectPackage service;

        @JsonProperty("sqlProvider")
        public ProjectPackage sqlProvider;

        @JsonProperty("controller")
        public ProjectPackage controller;
    }

    @Data
    public static class Jdbc {

        public String clazz;
        public String url;
        public String username;
        public String password;
        public String database;
    }

    @Data
    public static class Template {

        public String controller;
        public String service;
        public String mapper;
        public String sqlProvider;
        public String entity;
        public String data;
        public String dto;
        public String useful;
        public String docTable;
        public String docPostMan;
    }


}
