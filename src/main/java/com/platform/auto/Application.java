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

//    public static void main(String[] args) throws Exception {
//        start();
//    }

    public static void start() throws Exception {
        Constant.basePath = Constant.project.getBasePath();
        JsonNode jsonNode = new ObjectMapper().readTree(String.join(" ", AutoUtil.readTemplate("auto/setting.json")));

        /**
         * 目录结构为同级目录
         * api
         * common
         * 详情查看 demo.png
         * */
        Constant.path_base = jsonNode.get("path_base").asText();

        // todo : 常量类的位置, 有些是类型的这种常量, 需要写入到常量类中f
        Constant.package_constant = jsonNode.get("package_constant").asText();
        // todo : 关于 db 操作的类, 需要写入到 db 包中
        Constant.package_db = jsonNode.get("package_db").asText();
        // todo : controller 的类, 需要写入到 controller 的包中
        Constant.package_controller = jsonNode.get("package_controller").asText();

        Constant.db_project_name = jsonNode.get("db_project_name").asText();
        Constant.controller_project_name = jsonNode.get("controller_project_name").asText();

        JsonNode jdbc = jsonNode.get("jdbc");
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
                CharUtil.convertJsonNodeArrayToStringList(jsonNode.get("tableNames"))
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
