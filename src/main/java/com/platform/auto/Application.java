package com.platform.auto;

import com.platform.auto.config.Config;
import com.platform.auto.jdbc.ConnectionAuto;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import com.platform.auto.util.CharUtil;

public class Application {

    private static final Logger logger = AutoLogger.getLogger(Application.class);

    public static void start() throws Exception {
        ConnectionAuto.prepare(Config.getJdbc("clazz"),
                Config.getJdbc("url"),
                Config.getJdbc("username"),
                Config.getJdbc("password"),
                Config.getJdbc("database"));
        // TODO: 通用代码生成
        // TODO: 通用代码生成
        // TODO: 通用代码生成
        // 通用代码生成
        ConnectionAuto.start(
                CharUtil.convertJsonNodeArrayToStringList(Config.getConfig().get("tableNames"))
        );

        logger.info("end");

    }

}
