package com.platform.auto.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.project.Project;
import com.platform.auto.jdbc.Connection;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import com.platform.auto.util.AutoUtil;
import com.platform.auto.util.FileUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

public class Config {

    private static final Logger logger = AutoLogger.getLogger(Config.class);

    /***
     * 系统 的常量
     * **/
    public static Project project;
    public static ConfigEntity config;
    public static LocalEntity local;
    public static final ObjectMapper objectMapper = new ObjectMapper();

    // todo : D:\ksm\code\playlet\playlet-app-api
    public static String project_base_path;
    public static final String auto_name = ".auto";
    public static final String auto_config_name = ".config";

    // todo : D:\ksm\code\playlet\playlet-app-api\.auto
    public static String project_auto_path;

    // todo : D:\ksm\code\playlet\playlet-app-api\.auto\.config
    public static String project_config_path;

    // todo : D:\ksm\code\playlet\playlet-app-api\.auto\.config\template
    public static String project_template_path;

    // todo : D:\ksm\code\playlet\playlet-app-api\.auto\.config\log.txt
    public static String log_path;

    // todo : D:\ksm\code\playlet\playlet-app-api\.auto\.config\local.json
    public static String local_path;
    public static String base_java_path = "/src/main/java/";

    public static ConfigEntity getConfig() {
        try {
            config = config == null ? objectMapper.readValue(String.join(" ", AutoUtil.readFromLocal(auto_config_name + "/config.json")), ConfigEntity.class) : config;
        } catch (Exception e) {
            logger.info(e);
        }
        return config;
    }

    public static ConfigEntity getConfigFromResources() {
        try {
            return objectMapper.readValue(String.join(" ", AutoUtil.readFromResources("config/config.json")), ConfigEntity.class);
        } catch (Exception e) {
            logger.info(e);
        }
        return null;
    }

    public static LocalEntity getLocal() {
        try {
            local = local == null ? objectMapper.readValue(String.join(" ", AutoUtil.readFromLocal(auto_config_name + "/local.json")), LocalEntity.class) : local;
        } catch (Exception e) {
            logger.info(e);
        }
        return local;
    }

    public static void refreshLocal() {
        try {
            AutoUtil.listToFile(project_config_path + "/local.json", List.of(objectMapper.writeValueAsString(local)));
            local = null;
            getLocal();
        } catch (Exception e) {
            logger.info(e);
        }
    }

    public static String getControllerFilePath() throws Exception {
        return getJavaFilePath(getConfig().controllerProjectName, getConfig().controllerPackage);
    }

    public static String getDbFilePath() throws Exception {
        return getJavaFilePath(getConfig().dbProjectName, getConfig().dbPackage);
    }

    public static String getConstantFilePath() throws Exception {
        return getJavaFilePath(getConfig().constantProjectName, getConfig().constantPackage);
    }

    /**
     * 从 config.json 中解析出,生成的代码的存放路径
     ***/
    public static String getJavaFilePath(String key1, String key2) throws Exception {
        return project_base_path + "/"
                + key1.replace(".", "/")
                + Config.base_java_path
                + key2.replace(".", "/")
                + "/";
    }

    /**
     * 本地数据的初始化
     **/
    public static void initLocalData() throws Exception {
        Connection.prepare(
                getConfig().jdbc.clazz,
                getConfig().jdbc.url,
                getConfig().jdbc.username,
                getConfig().jdbc.password,
                getConfig().jdbc.database
        );
        local.setTableList(Connection.getAllTableInfo());
    }

    /**
     * 将 config 信息, 拷贝到 .auto 项目目录下面
     **/
    public static void init(Project projectParam) throws Exception {
        config = null;
        local = null;
        project = projectParam;
        project_base_path = project.getBasePath();

        project_auto_path = project_base_path + "/" + auto_name;
        project_config_path = project_auto_path + "/" + auto_config_name;
        project_template_path = project_config_path + "/template";

        // for log
        log_path = project_config_path + "/log.txt";
        FileUtil.createFile(log_path);
        // for local.json
        local_path = project_config_path + "/local.json";
        String localString = String.join(" ", AutoUtil.readFromLocal(auto_config_name + "/local.json"));
        if (StringUtils.isBlank(localString)) {
            AutoUtil.listToFile(local_path, List.of("{\"_t\":\"" + System.currentTimeMillis() + "\"}"));
        }

        // 当 config 存在的时候,就不需要
        String configJson = project_config_path + "/config.json";
        File temp = new File(configJson);
        if (temp.exists()) {
            return;
        }
        // todo : 拷贝系统的 config 配置
        logger.info("initConfig: {}", configJson);
        AutoUtil.listToFile(configJson, AutoUtil.readFromResources("config/config.json"));
        ConfigEntity.Template template = getConfigFromResources().template;
        Class<?> clazz = template.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            logger.info("field: {}", field.getName());
            logger.info("field: {}", field.getType());
            logger.info("field: {}", field.get(clazz));
//            String templateFilePath = field.get(clazz).toString();
//            String templateLocalFilePath = project_auto_path + "/" + templateFilePath;
//            FileUtil.createFile(templateLocalFilePath);
//            AutoUtil.listToFile(templateLocalFilePath, AutoUtil.readFromResources(templateFilePath));
        }
    }


}
