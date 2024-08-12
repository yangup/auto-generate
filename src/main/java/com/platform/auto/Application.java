package com.platform.auto;

import com.platform.auto.config.Config;
import com.platform.auto.jdbc.ConnectionAuto;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;

public class Application {

    private static final Logger logger = AutoLogger.getLogger(Application.class);

    public static void start() throws Exception {
        ConnectionAuto.prepare(Config.getConfig().jdbc.clazz,
                Config.getConfig().jdbc.url,
                Config.getConfig().jdbc.username,
                Config.getConfig().jdbc.password,
                Config.getConfig().jdbc.database);
        // TODO: 通用代码生成
        // TODO: 通用代码生成
        // TODO: 通用代码生成
        // 通用代码生成
        ConnectionAuto.start(Config.getConfig().tableNames);

        logger.info("end");

    }

}
