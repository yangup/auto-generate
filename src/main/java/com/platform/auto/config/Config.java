package com.platform.auto.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.platform.auto.entity.*;
import com.platform.auto.jdbc.Connection;
import com.platform.auto.jdbc.ControllerCreator;
import com.platform.auto.jdbc.HttpCreator;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import com.platform.auto.util.FileUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.platform.auto.util.AutoUtil.*;

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
    public static final String auto_local_name = ".local";

    // todo : D:\ksm\code\playlet\playlet-app-api\.auto
    public static String project_auto_path;

    // todo : .config\config.json
    public static String config_path_file_name = auto_config_name + "/config.json";
    public static String config_path_file_name_1 = auto_config_name + "/config1.json";

    // todo : .config\typeToJavaData.json
    public static String config_path_type_to_java_data_file_name = auto_config_name + "/typeToJavaData.json";

    // todo : .local\log.txt
    public static String log_path_file_name = auto_local_name + "/log.txt";

    // todo : .local\local.json
    public static String local_path_file_name = auto_local_name + "/local.json";
    public static final String base_java_path = "/src/main/java/";
    public static final String config_template_prefix = ".config/template/";

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ConfigEntity getConfig(String... configJsonName) {
        try {
            String configJsonNameUse = "config.json";
            if (configJsonName != null && configJsonName.length > 0 && StringUtils.isNotBlank(configJsonName[configJsonName.length - 1])) {
                configJsonNameUse = configJsonName[configJsonName.length - 1];
            }
            config = config == null ? objectMapper.readValue(readFromLocalJson(auto_config_name + "/" + configJsonNameUse), ConfigEntity.class) : config;
        } catch (Exception e) {
            logger.info(e);
        }
        return config;
    }

    public static ConfigEntity getConfigFromResources() {
        try {
            return objectMapper.readValue(String.join(" ", readFromResources(config_path_file_name)), ConfigEntity.class);
        } catch (Exception e) {
            logger.info(e);
        }
        return new ConfigEntity();
    }

    public static LocalEntity getLocal() {
        try {
            local = local == null ? objectMapper.readValue(readFromLocalJson(local_path_file_name), LocalEntity.class) : local;
        } catch (Exception e) {
            logger.info(e);
            return new LocalEntity();
        }
        return local;
    }

    public static boolean existLocal(String path) {
        try {
            if (FileUtil.exists(project_auto_path + "/" + path)) {
                logger.info("existLocal, true, {}", path);
                return true;
            }
        } catch (Exception e) {
            logger.info("existLocal, false, {}", path);
            return false;
        }
        logger.info("existLocal, false, {}", path);
        return false;
    }

    public static void refreshLocal() {
        try {
            local.time = getNowTime();
            objectToLocalFile(local_path_file_name, local);
            local = null;
            getLocal();
        } catch (Exception e) {
            logger.info(e);
        }
    }

    public static PathEntity getPathByType(String type) {
        return getConfig().info.stream().filter(info -> equalsAnyIgnoreCase(info.type, type)).findFirst().orElse(new ConfigInfoEntity()).getPath();
    }

    /**
     * 从 config.json 中解析出,生成的代码的存放路径
     ***/
    public static String getJavaFilePath(PathEntity path) {
        return getJavaFilePath(path.projectName, path.packageName);
    }

    public static String getJavaFilePath(String projectName, String packageName) {
        return project_base_path + "/" + projectName.replace(".", "/") + Config.base_java_path + packageName.replace(".", "/") + "/";
    }

    public static String getTemplatePath(String templateFileName) {
        return config_template_prefix + templateFileName;
    }

    /**
     * 本地数据的初始化
     **/
    public static void initLocalData(boolean init) throws Exception {
        if (isNotEmpty(getLocal().dbInfoList) && init) {
            return;
        }
        config = null;
        Connection.prepare(getConfig().jdbc.clazz, getConfig().jdbc.url, getConfig().jdbc.username, getConfig().jdbc.password, getConfig().jdbc.database);
        List<LocalEntity.TableEntity> tableList = Connection.getAllTableInfo();
        logger.info("initLocalData");

        getLocal().dbInfoList = new ArrayList<>();
        DbEntity dbEntity = null; // 初始设置为 null

        for (LocalEntity.TableEntity table : tableList) {
            // 当 dbEntity 为空或 tableSchema 改变时，创建一个新的 dbEntity
            if (dbEntity == null || !StringUtils.equals(dbEntity.dbName, table.tableSchema)) {
                // 如果不是第一次循环，将前一个 dbEntity 添加到 dbInfoList 中
                if (dbEntity != null) {
                    getLocal().dbInfoList.add(dbEntity);
                }
                dbEntity = new DbEntity();
                dbEntity.dbName = table.tableSchema;
                dbEntity.tableNameList = new ArrayList<>();
            }
            dbEntity.tableNameList.add(table.tableName);
        }

        // 循环结束后，将最后一个 dbEntity 添加到 dbInfoList 中
        if (dbEntity != null) {
            getLocal().dbInfoList.add(dbEntity);
        }

        if (StringUtils.isEmpty(getLocal().selectedDbName)) {
            getLocal().selectedDbName = getConfig().jdbc.database;
        }

        refreshLocal();
        logger.info("refreshLocal");
    }

    public static void initProject(Project projectParam) {
        project = ProjectUtil.currentOrDefaultProject(projectParam);
        project_base_path = project.getBasePath();
        project_auto_path = project_base_path + "/" + auto_name;
    }

    public static String getNowTime() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
    }

    /**
     * 将 config 信息, 拷贝到 .auto 项目目录下面
     **/
    public static void initFile() throws Exception {
        config = null;
        local = null;
        if (!existLocal(log_path_file_name)) {
            FileUtil.createLocalFile(log_path_file_name);
        }
        if (StringUtils.isBlank(readFromLocalJson(local_path_file_name))) {
            LocalEntity localEntity = new LocalEntity();
            localEntity.time = getNowTime();
            // 默认 config 中的 db name
            localEntity.selectedDbName = getConfigFromResources().jdbc.database;
            objectToLocalFile(local_path_file_name, localEntity);
            logger.info("init_local.json");
        }

        if (StringUtils.isBlank(readFromLocalJson(config_path_type_to_java_data_file_name))) {
            listToLocalFile(config_path_type_to_java_data_file_name, readFromResources(config_path_type_to_java_data_file_name));
        }

        // 当 config 存在的时候,就不需要
        if (existLocal(config_path_file_name)) {
            return;
        }

        HttpCreator.createRequestInit();

        // todo : 拷贝系统的 config 配置
        logger.info("initConfig: {}", project_auto_path);
        listToLocalFile(config_path_file_name, readFromResources(config_path_file_name));
        listToLocalFile(config_path_file_name_1, readFromResources(config_path_file_name));
        List<ConfigInfoEntity> infoList = getConfigFromResources().info;
        for (ConfigInfoEntity info : infoList) {
            if (isBlank(info.template)) {
                continue;
            }
            String templateFilePath = getTemplatePath(info.template);
            listToLocalFile(templateFilePath, readFromResources(templateFilePath));
        }
    }


}
