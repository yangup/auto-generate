package com.platform.auto;

import com.platform.auto.config.Config;
import com.platform.auto.jdbc.ConnectionAuto;
import com.platform.auto.jdbc.model.TypeToJavaData;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class Application {

    private static final Logger logger = AutoLogger.getLogger(Application.class);

    public static void start(List<String> tableNameList) throws Exception {
        Config.refreshConfig();
        Config.refreshLocal();
        TypeToJavaData.init();
        ConnectionAuto.prepare(Config.getConfig().jdbc.clazz,
                Config.getConfig().jdbc.url,
                Config.getConfig().jdbc.username,
                Config.getConfig().jdbc.password,
                Config.getConfig().jdbc.database);
        // TODO: 通用代码生成
        // TODO: 通用代码生成
        // TODO: 通用代码生成
        // 通用代码生成
        // 处理一下,过滤掉空格
        List<String> list = tableNameList.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());

        logger.info("start: {}", String.join(",", list));
        if (ObjectUtils.isEmpty(list)) {
            return;
        }
        ConnectionAuto.start(list);

        logger.info("end");

    }

    // for test
    public static void main(String[] args) throws Exception {
//        File currentDir = new File(".");
//        String absolutePath = currentDir.getAbsolutePath().replace("\\", "/");
//        Config.project_base_path = absolutePath.substring(0, absolutePath.length() - 2);
//        Config.project_auto_path = Config.project_base_path + "/" + Config.auto_name;
        // 指定路径生成
//        Config.project_base_path = "D:/ksm/code/th/biz/th-pay-server";
        Config.project_base_path = "D:\\ksm\\code\\ph\\business\\ph-ovsloan-loan";
        Config.project_auto_path = Config.project_base_path + "/" + Config.auto_name;
        Application.start(List.of(


                "user_device_info_oss"


        ));
//        Application.start(List.of("t_system_user_setting"));
    }

}
