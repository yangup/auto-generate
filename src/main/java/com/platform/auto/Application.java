package com.platform.auto;

import com.platform.auto.jdbc.ConnectionAuto;
import com.platform.auto.jdbc.Constant;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import com.platform.auto.util.AutoUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Application {

    private static final Logger logger = AutoLogger.getLogger(Application.class);

    //    public static void main(String[] args) throws Exception {
////        start();
//    }

    public static void start() throws Exception {

        /**
         * 目录结构为同级目录
         * api
         * common
         * 详情查看 demo.png
         * */
        Constant.path_base = "D:\\ksm\\code\\playlet\\playlet-app-api";

        // todo : 常量类的位置, 有些是类型的这种常量, 需要写入到常量类中
        Constant.package_constant = "com.playlet.common.constant";
        // todo : 关于 db 操作的类, 需要写入到 db 包中
        Constant.package_db = "com.playlet.common.db";
        // todo : controller 的类, 需要写入到 controller 的包中
        Constant.package_controller = "com.playlet.api.controller";

        Constant.db_project_name = "common";
        Constant.controller_project_name = "api";

        ConnectionAuto.prepare("com.mysql.cj.jdbc.Driver",
                "jdbc:mysql://dev-db.ksmdev.top:3306/",
                "root",
                "123456",
                "yp3");
        // TODO: 通用代码生成
        // TODO: 通用代码生成
        // TODO: 通用代码生成
        // 通用代码生成
        ConnectionAuto.start(
                "tb_user_facebook"
//                "tb_video_episode"
//                "tb_video_like",
//                "tb_video_collect",
//                "tb_video_progress",
//                "tb_video_episode_progress",
//                "tb_event_tracking_log"
        );

    }

}
