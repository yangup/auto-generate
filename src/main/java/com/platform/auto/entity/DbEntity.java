package com.platform.auto.entity;

import lombok.Data;

import java.util.List;

/**
 *
 */

@Data
public class DbEntity {

    public String dbName;
    public List<String> tableNameList;

}
