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

    @JsonProperty("controller_package")
    public String controllerPackage;

    @JsonProperty("controller_project_name")
    public String controllerProjectName;

    @JsonProperty("db_package")
    public String dbPackage;

    @JsonProperty("db_project_name")
    public String dbProjectName;

    @JsonProperty("constant_package")
    public String constantPackage;

    @JsonProperty("constant_project_name")
    public String constantProjectName;

    public Jdbc jdbc;

    @JsonProperty("table_names")
    public List<String> tableNames;

    public Template template;


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
