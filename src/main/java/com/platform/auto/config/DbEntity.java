package com.platform.auto.config;

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
