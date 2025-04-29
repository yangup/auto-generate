package com.platform.auto.entity;

import lombok.Data;

@Data
public class JdbcEntity {

    public String clazz;
    public String url;
    public String username;
    public String password;
    public String database;

}
