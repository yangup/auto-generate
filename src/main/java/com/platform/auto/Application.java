package com.platform.auto;

import com.platform.auto.config.Config;
import com.platform.auto.jdbc.ConnectionAuto;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class Application {

    private static final Logger logger = AutoLogger.getLogger(Application.class);

    public static void start(List<String> tableNameList) throws Exception {
        ConnectionAuto.prepare(Config.getConfig().jdbc.clazz,
                Config.getConfig().jdbc.url,
                Config.getConfig().jdbc.username,
                Config.getConfig().jdbc.password,
                Config.getConfig().jdbc.database);
        // TODO: 通用代码生成
        // TODO: 通用代码生成
        // TODO: 通用代码生成
        // 通用代码生成
//        ConnectionAuto.start(Config.getConfig().tableNames);
        logger.info("start: {}", tableNameList.stream().collect(Collectors.joining(", ")));
        ConnectionAuto.start(tableNameList);

        logger.info("end");

    }

}
