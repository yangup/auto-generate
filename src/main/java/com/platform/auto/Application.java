package com.platform.auto;

import com.platform.auto.config.Config;
import com.platform.auto.jdbc.ConnectionAuto;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

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
        // 处理一下,过滤掉空格
        List<String> list = tableNameList.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());

        logger.info("start: {}", String.join(",", list));
        if (ObjectUtils.isEmpty(list)) {
            return;
        }
        ConnectionAuto.start(list);

        logger.info("end");

    }

}
