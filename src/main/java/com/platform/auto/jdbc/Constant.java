package com.platform.auto.jdbc;

import com.platform.auto.jdbc.model.TypeToJavaData;
import org.apache.commons.lang3.StringUtils;

public class Constant {

    // TODO: 2021/12/28 自动将文档给写入到 showdoc 服务器中
    public static String doc_url = "";
    public static String projectName = "";
    public static String doc_api_key = "";
    public static String doc_api_token = "";
    public static String path = "";
    public static String constant = "";
    public static String path_base = "";
    public static String path_front = "";
    public static String path_controller = "";
    public static String path_to_doc = "";
    public static String path_no_java = "";
    public static String author = "admin";
    public static String name = "for_app_api";
    public static String base_url = "";
    public static String token = "9UHsisMpaa1GBhwTrJOXsiaSI6IjIwMjAxMjE2MjAxMjQ5MDAwMDAwMTAiLCJqdXN0Rm9yVGVzdCI6InllcyIsImwiOiJ5YW5ncHUiLCJ1IjoiMTExMTExMTExMTExIn0=";
    public static Integer number = null;
    public static String template = "/template/";

    /**
     * 使用 templateMapper_simple
     * 使用 templateService_simple
     * 使用 templateSqlProvider_simple
     * 作为模板
     **/
    public static boolean simple = false;
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
    public static String DESC = "desc.up";
    public static String TRANSLATE = "translate.up";
    public static String TEMPLATE_SERVICE = "templateService.up";
    public static String TEMPLATE_SERVICE_SIMPLE = "templateService_simple.up";
    public static String TEMPLATE_DOC_TABLE = "templateDocTable.up";
    public static String TEMPLATE_DOC_URL = "templateDocUrl.up";
    public static String TEMPLATE_POST_MAN = "templatePostMan.up";
    public static String TEMPLATE_POST_MAN_TABLE = "templatePostManTable.up";
    public static String TEMPLATE_CONTROLLER = "templateController.up";
    public static String TEMPLATE_SQL_PROVIDER = "templateSqlProvider.up";
    public static String TEMPLATE_SQL_PROVIDER_SIMPLE = "templateSqlProvider_simple.up";
    public static String TEMPLATE_FRONT = "templateFront.up";
    public static String useful = "";
    public static String entity = "";
    public static String data = "";
    public static String page = "";
    public static String dto = "";
    public static String mapper = "";
    public static String mapperSimple = "";
    public static String desc = "";
    public static String translate = "";
    public static String service = "";
    public static String serviceSimple = "";
    public static String docTable = "";
    public static String docUrl = "";
    public static String docPostMan = "";
    public static String docPostManTable = "";
    public static String controller = "";
    public static String sqlProvider = "";
    public static String sqlProviderSimple = "";
    public static String front = "";
    public static String file = "file";
    /**
     * 主要的生成的文件,基础文件的包名,路径
     * com.platform.db.admin.
     * 注意: 末尾包含了点
     **/
    public static String package_base = null;
    /**
     * controller 的包名,路径
     * com.platform.web.admin.controller
     * 注意: 末尾没有包含点
     **/
    public static String package_base_controller = null;
    public static String ROLE_ID = null;
    public static boolean front_generate = false;
    public static String front_file = null;
    public static String java_file_path = null;
    // todo : 生成构造方法
    public static Boolean isConstructor = Boolean.TRUE;

    public static String obtain(String pathName) {
//        System.out.println("pathName=" + pathName);
        return Constant.class.getResource(pathName).getPath();
    }

    public static String obtainTemplate(String templateName) {
        return obtain(template + templateName);
    }

    public static void init() {
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
        desc = obtainDocPath(DESC);
        translate = obtainDocPath(TRANSLATE);
        service = obtainTemplate(TEMPLATE_SERVICE);
        serviceSimple = obtainTemplate(TEMPLATE_SERVICE_SIMPLE);
        docTable = obtainTemplate(TEMPLATE_DOC_TABLE);
        docUrl = obtainTemplate(TEMPLATE_DOC_URL);
        docPostMan = obtainTemplate(TEMPLATE_POST_MAN);
        docPostManTable = obtainTemplate(TEMPLATE_POST_MAN_TABLE);
        controller = obtainTemplate(TEMPLATE_CONTROLLER);
        sqlProvider = obtainTemplate(TEMPLATE_SQL_PROVIDER);
        sqlProviderSimple = obtainTemplate(TEMPLATE_SQL_PROVIDER_SIMPLE);
        front = obtainTemplate(TEMPLATE_FRONT);
//        Constant.TEMPLATE_DOC = "templateDoc.up";
//        Constant.TEMPLATE_DOC_ALL = "templateDocAll.up";
//
//        Constant.TEMPLATE_USEFUL = "templateUseful.up";
//
//        Constant.DESC = "desc.up";
//        Constant.TRANSLATE = "translate.up";
//
//        Constant.TEMPLATE_POST_MAN = "templatePostMan.up";
//        Constant.TEMPLATE_POST_MAN_TABLE = "templatePostManTable.up";
    }

    /**
     * 根据路径,获得包名
     **/
    public static String obtainPackageJava(String p) {
        if (StringUtils.isEmpty(p)) {
            return "";
        }
        String indexPath = "src\\main\\java";
        String packagePathNew = StringUtils.substring(p, StringUtils.indexOf(p, indexPath) + indexPath.length() + 1);
        packagePathNew = StringUtils.replaceChars(packagePathNew, "\\", ".");
        if (StringUtils.endsWith(packagePathNew, ".")) {
            packagePathNew = packagePathNew.substring(0, packagePathNew.length() - 1);
        }
        return packagePathNew;
    }

    /***
     * 不在代码中的 path
     * **/
    public static String obtainDocPath(String name) {
        String p = Constant.class.getResource(template).getPath();
        String base = p.substring(0, p.indexOf("/platform/web-admin/"));
        return base + "\\doc\\" + name;
    }


}
