package com.platform.auto.sys.order;

import com.platform.auto.util.CharUtil;
import org.apache.commons.lang3.StringUtils;

public enum Order {

    // vue table column
    startElTableColumn,
    startElFormItem,
    startTemp,
    startRules,

    // id
    author,
    method,
    url,
    uuid,
    name,
    item,
    base_url,
    queryMapParam,
    token,
    // house_side_info -> HouseSideInfo
    // 表名称-> 将下划线+小写转化为大写,首字母大写
    tableNameJava,
    // house_side_info -> houseSideInfo
    // 将下划线+小写转化为大写
    tableNameJavaParam,
    // 表的注释
    tableComment,
    tableCommentRaw,
    // house_side_info -> housesideinfo
    // model产生的时候生成的field
    startField,
    // 当前用户
    user,
    tableNameJavaLower,
    projectName,
    // 当前日期
    // yyyy-mm-dd HH:mi:ss
    dateYMDHMSS,
    // 当前日期
    // yyyy-mm-dd HH:mi
    dateYMDHM,
    dateYMDHMS,
    // ctr中的包名
    packageController,
    // ser中的包名
    packageService,
    // model中的包名
    packageTable,
    // mapper中的包名
    packageMapper,
    packageSqlProvider,
    // 开始产生json数据
    jsonStart,
    jsonKeyValueComment,
    tableJson,
    columnNoteWithLength,

    // 在生成实体类的时候, 需要导入的包, 有些类型用的是其他的包
    javaEntityPackage,

    jsonDemo,

    //
    sqlFieldRaw,
    sqlFieldValue,
    sqlFieldNameValue,
    sqlHelpful,
    sqlHelpfulFormat,
    sqlFieldNameValueParam,
    tableName,

    serviceTypeName,
    serFindTypeName,
    serviceName,
    serviceFindName,
    controllerFindParam,

    mapperSelectKeyValue,
    mapperSelectKeyValueParam,

    //
    queryParam,
    filterAll,
    filterName,
    filterComment,
    // vue 中的 详情展示
    elDescriptionsItem,
    elFormItemDetail,
    // comment use
    commentUse,
    commentList,
    columnNameJavaList,
    sqlColumnNameUse,
    javaColumnNameUse,
    javaParamNameUse,
    frontFilePath,
    frontTop,
    frontSelectConstant,

    // todo : 第一个不是 id 的数据, 变量名, 例如 : name
    firstNoIdParam,

    queryFindParam,
    queryFindParamDoc,

    // todo : 需要引入的一些 static 常量
    importStaticEntity,

    importPage,
    importPageList,
    importService,
    importServiceCollectors,
    autowiredService,
    findMethod,
    ;

    public static boolean checkNeed(String columnName) {
        if (StringUtils.equalsIgnoreCase("id", columnName.toLowerCase())) {
            return false;
        }
        if (StringUtils.equalsIgnoreCase("create_time", columnName.toLowerCase())) {
            return false;
        }
        if (StringUtils.equalsIgnoreCase("update_time", columnName.toLowerCase())) {
            return false;
        }
        return true;
    }

    public static boolean checkNeedUpdate(String columnName) {
        if (StringUtils.equalsIgnoreCase("id", columnName.toLowerCase())) {
            return false;
        }
        if (StringUtils.equalsIgnoreCase("create_time", columnName.toLowerCase())) {
            return false;
        }
        return true;
    }

    /**
     * 获得命令
     **/
    public static String getOrder(Order o) {
        if (o == null) {
            return null;
        }
        return CharUtil.y + o + CharUtil.y;
    }

    public static boolean check(StringBuilder str, Order o) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        return str.indexOf(getOrder(o)) != -1;
    }

    /**
     * 判断str中,是否含有命令
     **/
    public static boolean check(String str, Order o) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        return str.contains(getOrder(o));
    }

    /**
     * 判断str中,o命令转化成os,并返回str
     **/
    public static String change(String str, Order o, String os) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        if (StringUtils.isEmpty(os)) {
            os = " ";
        }
        if (check(str, o)) {
            str = str.replaceAll(getOrder(o), os);
        }
        return str;
    }

}
