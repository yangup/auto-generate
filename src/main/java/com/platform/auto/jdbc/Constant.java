package com.platform.auto.jdbc;

import com.intellij.openapi.project.Project;
import com.platform.auto.jdbc.model.TypeToJavaData;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import com.platform.auto.util.AutoUtil;
import com.platform.auto.util.FileUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

public class Constant {

    private static final Logger logger = AutoLogger.getLogger(Constant.class);

    public static Project project;
    public static String project_base_path;
    public static String log_path;
    public static String db_project_name = "";
    public static String constant_project_name = "";
    public static String controller_project_name = "";
    public static String base_java_path = File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator;
    public static String package_constant = "";
    public static String package_db = "";
    public static String package_controller = "";
    public static String projectName = "admin";
    public static String path = "";
    public static String constant = "";
    public static String path_base = "";
    public static String path_front = "";
    public static String path_controller = "";
    public static String path_no_java = "";
    public static String author = "yangpu";
    public static String name = "for_app_api";
    public static String base_url = "";
    public static String token = "9UHsisMpaa1GBhwTrJOXsiaSI6IjIwMjAxMjE2MjAxMjQ5MDAwMDAwMTAiLCJqdXN0Rm9yVGVzdCI6InllcyIsImwiOiJ5YW5ncHUiLCJ1IjoiMTExMTExMTExMTExIn0=";
    public static Integer number = null;
    public static String template = "config/template/";

    /**
     * 使用 templateMapper_simple
     * 使用 templateService_simple
     * 使用 templateSqlProvider_simple
     * 作为模板
     **/
//    public static boolean simple = false;
    public static boolean simple = true;
    /**
     *
     **/
    public static String TEMPLATE_USEFUL = "templateUseful.up";
    public static String TEMPLATE_ENTITY = "templateEntity.up";
    public static String TEMPLATE_DATA = "templateData.up";
    public static String TEMPLATE_PAGE = "templatePage.up";
    public static String TEMPLATE_DTO = "templateDto.up";
    public static String TEMPLATE_MAPPER = "templateMapper.up";
    public static String TEMPLATE_MAPPER_SIMPLE = "templateMapper_simple.up";
    public static String TEMPLATE_SERVICE = "templateService.up";
    public static String TEMPLATE_SERVICE_SIMPLE = "templateService_simple.up";
    public static String TEMPLATE_DOC_TABLE = "templateDocTable.up";
    public static String TEMPLATE_DOC_URL = "templateDocUrl.up";
    public static String TEMPLATE_POST_MAN = "templatePostMan.up";
    public static String TEMPLATE_POST_MAN_TABLE = "templatePostManTable.up";
    public static String TEMPLATE_CONTROLLER = "templateController.up";
    public static String TEMPLATE_SQL_PROVIDER = "templateSqlProvider.up";
    public static String TEMPLATE_SQL_PROVIDER_SIMPLE = "templateSqlProvider_simple.up";
    public static List<String> TEMPLATE_LLIST = null;
    public static String useful = "";
    public static String entity = "";
    public static String data = "";
    public static String page = "";
    public static String dto = "";
    public static String mapper = "";
    public static String mapperSimple = "";
    public static String service = "";
    public static String serviceSimple = "";
    public static String docTable = "";
    public static String docUrl = "";
    public static String docPostMan = "";
    public static String docPostManTable = "";
    public static String controller = "";
    public static String sqlProvider = "";
    public static String sqlProviderSimple = "";
    public static String file = "file";
    public static final String auto_config_bash_path = ".auto";
    /**
     * 主要的生成的文件,基础文件的包名,路径
     * com.platform.db.admin.
     * 注意: 末尾包含了点
     **/
    public static String package_base = null;
    /**
     * controller 的包名,路径
     * com.platform.auto.admin.controller
     * 注意: 末尾没有包含点
     **/
    public static String package_base_controller = null;
    public static String front_file = null;
    public static String java_file_path = null;
    // todo : 生成构造方法
    public static Boolean isConstructor = Boolean.TRUE;

    public static String obtainTemplate(String templateName) {
        return template + templateName;
    }

    /**
     * 常量的初始化
     **/
    public static void init() {

        Constant.constant_project_name = StringUtils.isEmpty(Constant.constant_project_name) ? Constant.db_project_name : Constant.constant_project_name;
        Constant.constant = Constant.path_base + File.separator
                + Constant.constant_project_name
                + Constant.base_java_path
                + Constant.package_constant.replace(".", File.separator)
                + "\\Constant.java";
        // todo : 代码生成的路径
        Constant.path = Constant.path_base + File.separator
                + Constant.db_project_name
                + Constant.base_java_path
                + Constant.package_db.replace(".", File.separator)
                + File.separator;
        // todo : controller 代码生成的路径
        Constant.path_controller = Constant.path_base + File.separator
                + Constant.controller_project_name
                + Constant.base_java_path
                + Constant.package_controller.replace(".", File.separator)
                + File.separator;
        // todo : 其他文件的路径
        Constant.path_no_java = Constant.path_base + File.separator + auto_config_bash_path + File.separator + "doc";

        package_base = obtainPackageJava(path) + ".";
        package_base_controller = obtainPackageJava(path_controller);
        TypeToJavaData.init();
        // todo : 需要重新赋值一下, 如果有新的, 就要更新一下
        useful = obtainTemplate(TEMPLATE_USEFUL);
        entity = obtainTemplate(TEMPLATE_ENTITY);
        data = obtainTemplate(TEMPLATE_DATA);
        page = obtainTemplate(TEMPLATE_PAGE);
        dto = obtainTemplate(TEMPLATE_DTO);
        mapper = obtainTemplate(TEMPLATE_MAPPER);
        mapperSimple = obtainTemplate(TEMPLATE_MAPPER_SIMPLE);
        service = obtainTemplate(TEMPLATE_SERVICE);
        serviceSimple = obtainTemplate(TEMPLATE_SERVICE_SIMPLE);
        docTable = obtainTemplate(TEMPLATE_DOC_TABLE);
        docUrl = obtainTemplate(TEMPLATE_DOC_URL);
        docPostMan = obtainTemplate(TEMPLATE_POST_MAN);
        docPostManTable = obtainTemplate(TEMPLATE_POST_MAN_TABLE);
        controller = obtainTemplate(TEMPLATE_CONTROLLER);
        sqlProvider = obtainTemplate(TEMPLATE_SQL_PROVIDER);
        sqlProviderSimple = obtainTemplate(TEMPLATE_SQL_PROVIDER_SIMPLE);

        TEMPLATE_LLIST = List.of(
                useful,
                entity,
                data,
                page,
                dto,
                mapper,
                mapperSimple,
                service,
                serviceSimple,
                docTable,
                docUrl,
                docPostMan,
                docPostManTable,
                controller,
                sqlProvider,
                sqlProviderSimple
        );
    }

    /**
     * 根据路径,获得包名
     **/
    public static String obtainPackageJava(String p) {
        if (StringUtils.isEmpty(p)) {
            return "";
        }
        String packagePathNew = StringUtils.substring(p, StringUtils.indexOf(p, base_java_path) + base_java_path.length() + 1);
        packagePathNew = StringUtils.replaceChars(packagePathNew, File.separator, ".");
        if (StringUtils.endsWith(packagePathNew, ".")) {
            packagePathNew = packagePathNew.substring(0, packagePathNew.length() - 1);
        }
        return packagePathNew;
    }

    /**
     * 将 config 信息, 拷贝到 .auto 项目目录下面
     **/
    public static void initConfig() throws Exception {
        if (StringUtils.isEmpty(project_base_path)) {
            return;
        }
        FileUtil.createIfNotExistsByPath(project_base_path + File.separator + auto_config_bash_path);
        FileUtil.createIfNotExistsByPath(project_base_path + File.separator + auto_config_bash_path + File.separator + "config");
        FileUtil.createIfNotExistsByPath(project_base_path + File.separator + auto_config_bash_path + File.separator + "config" + File.separator + "template");

        // for log
        String logTxt = project_base_path +
                File.separator + auto_config_bash_path +
                File.separator + "config" +
                File.separator + "log.txt";
        Constant.log_path = logTxt;
        File temp = new File(logTxt);
        if (!temp.exists()) {
            temp.createNewFile();
        }

        // 当 config 存在的时候,就不需要
        String configJson = project_base_path +
                File.separator + auto_config_bash_path +
                File.separator + "config" +
                File.separator + "setting.json";
        temp = new File(configJson);
        if (temp.exists()) {
            return;
        }
        logger.info("initConfig");
        if (!temp.exists()) {
            temp.createNewFile();
            logger.info("initConfig " + configJson);
            AutoUtil.listToFile(configJson, AutoUtil.readFromResources("config/setting.json"));
        }
        for (String filePath : TEMPLATE_LLIST) {
            logger.info("filePath " + filePath);
            String tempPath = project_base_path +
                    File.separator + auto_config_bash_path +
                    File.separator + filePath;
            logger.info("tempPath " + tempPath);
            logger.info("tempPath_filePath " + filePath);
            temp = new File(tempPath);
            if (!temp.exists()) {
                temp.createNewFile();
                AutoUtil.listToFile(tempPath, AutoUtil.readFromResources(filePath));
            }
        }
    }
}
