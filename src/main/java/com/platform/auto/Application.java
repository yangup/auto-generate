package com.platform.auto;

import com.fasterxml.jackson.databind.JsonNode;
import com.platform.auto.jdbc.ConnectionAuto;
import com.platform.auto.jdbc.Constant;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import com.platform.auto.util.CharUtil;

public class Application {

    private static final Logger logger = AutoLogger.getLogger(Application.class);

    /**
     * 应用的初始化
     **/
    public static void init() throws Exception {
        Constant.project_base_path = Constant.project.getBasePath();
        Constant.initConfig();
    }

    public static void initStart() throws Exception {
        Constant.initStart();
        JsonNode jdbc = Constant.getConfig().get("jdbc");
        ConnectionAuto.prepare(jdbc.get("clazz").asText(),
                jdbc.get("url").asText(),
                jdbc.get("username").asText(),
                jdbc.get("password").asText(),
                jdbc.get("database").asText());
    }

    public static void start() throws Exception {
        initStart();
        // TODO: 通用代码生成
        // TODO: 通用代码生成
        // TODO: 通用代码生成
        // 通用代码生成
        ConnectionAuto.start(
                CharUtil.convertJsonNodeArrayToStringList(Constant.getConfig().get("tableNames"))
        );

        logger.info("end");

    }

}
