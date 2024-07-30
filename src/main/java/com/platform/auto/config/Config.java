package com.platform.auto.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intellij.openapi.project.Project;
import com.platform.auto.jdbc.Connection;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import com.platform.auto.util.AutoUtil;
import com.platform.auto.util.FileUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Config {

    private static final Logger logger = AutoLogger.getLogger(Config.class);

    /***
     * 系统 的常量
     * **/
    public static Project project;
    public static JsonNode config;
    public static JsonNode local;
    public static final ObjectMapper objectMapper = new ObjectMapper();

    // todo : D:\ksm\code\playlet\playlet-app-api
    public static String project_base_path;
    public static final String auto_name = ".auto";

    // todo : D:\ksm\code\playlet\playlet-app-api\.auto
    public static String project_auto_path;

    // todo : D:\ksm\code\playlet\playlet-app-api\.auto\config
    public static String project_config_path;

    // todo : D:\ksm\code\playlet\playlet-app-api\.auto\config\template
    public static String project_template_path;

    // todo : D:\ksm\code\playlet\playlet-app-api\.auto\config\log.txt
    public static String log_path;

    // todo : D:\ksm\code\playlet\playlet-app-api\.auto\config\local.json
    public static String local_path;
    public static String base_java_path = "/src/main/java/";

    public static JsonNode getConfig() throws Exception {
        config = config == null ? objectMapper.readTree(String.join(" ", AutoUtil.readFromLocal("config/config.json"))) : config;
        return config;
    }

    public static JsonNode getConfigFromResources() throws Exception {
        return objectMapper.readTree(String.join(" ", AutoUtil.readFromResources("config/config.json")));
    }

    public static JsonNode getLocal() throws Exception {
        local = local == null ? objectMapper.readTree(String.join(" ", AutoUtil.readFromLocal("config/local.json"))) : local;
        return local;
    }

    public static String getByKeyFromLocal(String key) {
        try {
            return getLocal().get(key) == null ? null : getLocal().get(key).asText();
        } catch (Exception e) {
        }
        return "";
    }

    public static void setLocal(String key, Object value) throws Exception {
        if (value == null) return;
        if (value instanceof List<?>) {
            ((ObjectNode) getLocal()).putArray(key)
                    .addAll((ArrayNode) objectMapper.convertValue(value, JsonNode.class));
        } else {
            ((ObjectNode) getLocal()).put(key, value.toString());
        }
        AutoUtil.listToFile(project_config_path + "/local.json", List.of(getLocal().toString()));
        local = null;
        getLocal();
    }

    public static String getControllerFilePath() throws Exception {
        return getJavaFilePath("controller_project_name", "controller_package");
    }

    public static String getDbFilePath() throws Exception {
        return getJavaFilePath("db_project_name", "db_package");
    }

    public static String getConstantFilePath() throws Exception {
        return getJavaFilePath("constant_project_name", "constant_package");
    }

    /**
     * 从 config.json 中解析出,生成的代码的存放路径
     ***/
    public static String getJavaFilePath(String key1, String key2) throws Exception {
        return project_base_path + "/"
                + getByKey(key1).replace(".", "/")
                + Config.base_java_path
                + getByKey(key2).replace(".", "/")
                + "/";
    }

    public static String getByKey(String key) {
        try {
            return getConfig().get(key).asText();
        } catch (Exception e) {
        }
        return "";
    }

    public static String getTemplate(String key) throws Exception {
        return getConfig().get("template").get(key).asText();
    }

    public static String getJdbc(String key) throws Exception {
        return getConfig().get("jdbc").get(key).asText();
    }

    /**
     * 本地数据的初始化
     **/
    public static void initLocalData() throws Exception {
        Connection.prepare(
                Config.getJdbc("clazz"),
                Config.getJdbc("url"),
                Config.getJdbc("username"),
                Config.getJdbc("password"),
                Config.getJdbc("database")
        );
        String sql = "SELECT distinct col.TABLE_SCHEMA\n" +
                "FROM `information_schema`.`tables` col\n" +
                "WHERE col.table_schema NOT IN ('information_schema', 'performance_schema', 'mysql', 'sys')\n" +
                "order by 1 asc";
        setLocal("db", Connection.getData("TABLE_SCHEMA", sql));
        sql = "SELECT distinct col.TABLE_NAME\n" +
                "FROM `information_schema`.`tables` col\n" +
                "WHERE col.table_schema NOT IN ('information_schema', 'performance_schema', 'mysql', 'sys')\n" +
                "and col.table_schema IN ('" + Config.getConfig().get("jdbc").get("database").asText() + "')\n" +
                "order by 1 asc";
        setLocal("table", Connection.getData("TABLE_NAME", sql));
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
        project_config_path = project_auto_path + "/config";
        project_template_path = project_config_path + "/template";

        // for log
        log_path = project_config_path + "/log.txt";
        FileUtil.createFile(log_path);
        // for local.json
        local_path = project_config_path + "/local.json";
        String localString = String.join(" ", AutoUtil.readFromLocal("config/local.json"));
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
        JsonNode templateJsonNode = getConfigFromResources().get("template");
        Iterator<String> fieldNames = templateJsonNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            String templateFilePath = templateJsonNode.get(fieldName).asText();
            String templateLocalFilePath = project_auto_path + "/" + templateFilePath;
            FileUtil.createFile(templateLocalFilePath);
            AutoUtil.listToFile(templateLocalFilePath, AutoUtil.readFromResources(templateFilePath));
        }
    }


}
