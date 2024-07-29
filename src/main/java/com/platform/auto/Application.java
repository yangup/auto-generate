package com.platform.auto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.auto.jdbc.ConnectionAuto;
import com.platform.auto.jdbc.Constant;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import com.platform.auto.util.AutoUtil;
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

    public static void start() throws Exception {
        Constant.initStart();
        JsonNode jdbc = Constant.getConfig().get("jdbc");
        ConnectionAuto.prepare(jdbc.get("clazz").asText(),
                jdbc.get("url").asText(),
                jdbc.get("username").asText(),
                jdbc.get("password").asText(),
                jdbc.get("database").asText());
        // TODO: 通用代码生成
        // TODO: 通用代码生成
        // TODO: 通用代码生成
        // 通用代码生成
        ConnectionAuto.start(
                CharUtil.convertJsonNodeArrayToStringList(Constant.getConfig().get("tableNames"))
//                "tb_video_episode"
//                "tb_video_like",
//                "tb_video_collect",
//                "tb_video_progress",
//                "tb_video_episode_progress",
//                "tb_event_tracking_log"
        );

        logger.info("end");

    }

}
