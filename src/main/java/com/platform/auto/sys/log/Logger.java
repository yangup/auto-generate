package com.platform.auto.sys.log;


public interface Logger {

    String ROOT_LOGGER_NAME = "ROOT";

    String getName();

    void info(String var1, Object... var2);

}
