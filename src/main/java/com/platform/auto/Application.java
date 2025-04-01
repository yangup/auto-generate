package com.platform.auto;

import com.platform.auto.config.Config;
import com.platform.auto.jdbc.ConnectionAuto;
import com.platform.auto.jdbc.model.TypeToJavaData;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class Application {

    private static final Logger logger = AutoLogger.getLogger(Application.class);

    public static void start(List<String> tableNameList) throws Exception {
        Config.config = null;
        Config.local = null;
        TypeToJavaData.init();
        ConnectionAuto.prepare(Config.getConfig().jdbc.clazz,
                Config.getConfig().jdbc.url,
                Config.getConfig().jdbc.username,
                Config.getConfig().jdbc.password,
                StringUtils.isEmpty(Config.getLocal().selectedDbName) ? Config.getConfig().jdbc.database : Config.getLocal().selectedDbName);
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
        File currentDir = new File(".");
        String absolutePath = currentDir.getAbsolutePath().replace("\\", "/");
        Config.project_base_path = absolutePath.substring(0, absolutePath.length() - 2);
        Config.project_auto_path = Config.project_base_path + "/" + Config.auto_name;
        Application.start(List.of("mns_fail_log", "t_system_privilege", "t_system_privilege_server", "t_system_role", "t_system_role_privilege", "t_system_user", "t_system_user_role", "t_system_user_setting", "tb_deposite_bank_card_setting", "tb_deposite_bank_statement", "tb_deposite_merchant", "tb_disburse_bank_card_setting", "tb_disburse_order", "tb_disburse_order_statement", "tb_merchant_info"));
    }

}
