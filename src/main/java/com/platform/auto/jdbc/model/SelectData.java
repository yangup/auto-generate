package com.platform.auto.jdbc.model;

/**
 *
 */
public class SelectData {

    public String key;
    public String value;

    public static SelectData of(String key, String value) {
        SelectData f = new SelectData();
        f.key = key;
        f.value = value;
        return f;
    }


}
