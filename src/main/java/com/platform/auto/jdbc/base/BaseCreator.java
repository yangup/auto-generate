package com.platform.auto.jdbc.base;

import com.platform.auto.config.Config;
import com.platform.auto.entity.ConfigInfoEntity;
import com.platform.auto.jdbc.model.*;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import com.platform.auto.sys.order.Order;
import com.platform.auto.util.AutoUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.platform.auto.util.CharUtil.*;

/**
 * 基础的生成类
 **/
public class BaseCreator {

    private static final Logger logger = AutoLogger.getLogger(BaseCreator.class);

    public List<String> codeList;
    public Table table;
    public String template;
    public ConfigInfoEntity info;

    public BaseCreator() {

    }

    public BaseCreator(BaseCreator baseCreator) {
        this.info = baseCreator.info;
        this.table = baseCreator.table;
        this.template = baseCreator.info.template;
        this.codeList = baseCreator.codeList;
    }

    public BaseCreator(String template) {
        Table table = new Table();
        table.tableNameJava = "tableNameJava";
        table.tableNameJavaParam = "tableNameJavaParam";
        table.columnInfos = new ArrayList<>();
        this.template = template;
        this.table = table;
        this.codeList = new ArrayList<>(128);
    }

    /**
     * 根据模板来生成指定的代码
     * 将通用模板下的字段, 替换成 指定 @param table 下的字段
     **/
    public BaseCreator(ConfigInfoEntity info, Table table) {
        this.info = info;
        this.table = table;
        this.template = info.template;
        this.codeList = new ArrayList<>(128);
    }

    public void create() {
        if (isEmpty(table) || isEmpty(this.template)) {
            logger.info("table name or template is empty");
            return;
        }
        // 表名称--将下划线+小写转化为大写,首字母大写
        String tableNameJava = table.tableNameJava;
        String tableNameJavaParam = table.tableNameJavaParam;
        // 表的注释
        String tableComment = table.tableComment;
        // 读取 template
        List<String> templateList = AutoUtil.readFromLocal(Config.getTemplatePath(this.template));
        for (String lineTemp : templateList) {
            StringBuilder line = new StringBuilder(lineTemp);
            lineReplaceOrder(line, Order.uuid, UUID.randomUUID());
            lineReplaceOrder(line, Order.tableComment, tableComment);
            lineReplaceOrder(line, Order.tableNameSimple, table.tableNameSimple);
            lineReplaceOrder(line, Order.tableNameSimpleUpperCase, table.tableNameSimple.toUpperCase());
            lineReplaceOrder(line, Order.tableCommentRaw, table.tableCommentRaw);
            lineReplaceOrder(line, Order.author, Config.getConfig().author);
            lineReplaceOrder(line, Order.tableNameJava, tableNameJava);
            lineReplaceOrder(line, Order.tableNameJavaParam, tableNameJavaParam);
            lineReplaceOrder(line, Order.tableNameJavaLower, tableNameJava.toLowerCase());
            lineReplaceOrder(line, Order.dateYMDHMSS, DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            lineReplaceOrder(line, Order.dateYMDHM, DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm"));
            lineReplaceOrder(line, Order.dateYMDHMS, DateFormatUtils.format(new Date(), "yyyyMMdd_HHmmss"));
            String path = isTrue(Config.getConfig().getStoreByTable()) ? "." + table.tableNameJava.toLowerCase() : "";
            if (isNotBlank(info.storeByTable)) {
                if (isTrue(info.storeByTable)) {
                    path = "." + table.tableNameJava.toLowerCase();
                } else {
                    path = "";
                }
            }
            lineReplaceOrder(line, Order.packagePackage, info.path.packageName + path);
            lineReplaceOrder(line, Order.tableName, table.tableName);

            // todo : 在文档中, find 接口
            if (Order.check(line, Order.queryFindParamDoc)) {
                String wp = getWhitespace(line.substring(0, line.indexOf(Order.getOrder(Order.queryFindParamDoc))).length());
                lineReplaceOrder(line, Order.queryFindParamDoc, queryFindParamDoc(wp, table));
            }

            // todo : 在文档中, update 类的接口, 带有字段长度
            if (Order.check(line, Order.columnNoteWithLength)) {
                columnNoteWithLength(table.columnInfos);
                continue;
            }

            // todo : 在生成实体类的时候, 需要导入的包, 有些类型用的是其他的包
            lineReplaceOrder(line, Order.javaEntityPackage,
                    table.columnInfos
                            .stream()
                            .filter(c -> c.typeToJavaData != null && isNotEmpty(c.typeToJavaData.javaPackage))
                            .map(c -> String.format("import %s;", c.typeToJavaData.javaPackage))
                            .distinct()
                            .sorted()
                            .collect(Collectors.joining("\n")));
            lineReplaceOrder(line, Order.firstNoIdParam,
                    table.firstNoId != null ? table.firstNoId.columnNameJava : table.columnInfos.get(0).columnNameJava
            );
            if (Order.check(line, Order.frontTop)) {
                int top = 15;
                top = top - table.columnInfos.size();
                top = Math.max(top, 0);
                // todo : 设置 dialog 距离 页面顶部的 top 值; columnInfos 越多, top 越小, 反之亦然
                lineReplaceOrder(line, Order.frontTop, top);
            }

            lineReplaceOrder(line, Order.sqlHelpful, table.columnInfos
                    .stream()
                    .map(c -> String.format("a.%s,", c.columnName))
                    .collect(Collectors.joining(" ")));

            lineReplaceOrder(line, Order.sqlHelpfulFormat, table.columnInfos
                    .stream()
                    .map(c -> String.format("a.%s,", c.columnName))
                    .collect(Collectors.joining("\n")));

            lineReplaceOrder(line, Order.controllerFindParam, table.columnInfos
                    .stream()
                    .filter(c -> isNotIdCreateTimeUpdateTime(c.columnNameJava))
                    .map(c -> String.format("@RequestParam(name = \"%s\", required = false) %s %s", c.columnNameJava, c.dataTypeJava, c.columnNameJava))
                    .collect(Collectors.joining(d + n + getLeftWhitespace(line, Order.controllerFindParam))));

            lineReplaceOrder(line, Order.serviceFindName, table.columnInfos
                    .stream()
                    .filter(c -> isNotIdCreateTimeUpdateTime(c.columnNameJava))
                    .map(c -> c.columnNameJava)
                    .collect(Collectors.joining(d + n + getLeftWhitespace(line, Order.serviceFindName))));

            lineReplaceOrder(line, Order.serviceTypeName, table.columnInfos
                    .stream()
                    .filter(c -> isNotCreateTimeUpdateTime(c.columnNameJava))
                    .map(c -> c.dataTypeJava + w + c.columnNameJava)
                    .collect(Collectors.joining(d + n + getLeftWhitespace(line, Order.serviceTypeName))));

            lineReplaceOrder(line, Order.serviceName, table.columnInfos
                    .stream()
                    .filter(c -> isNotCreateTimeUpdateTime(c.columnNameJava))
                    .map(c -> c.columnNameJava)
                    .collect(Collectors.joining(d + n + getLeftWhitespace(line, Order.serviceName))));
            // @Param("id") Integer id
            lineReplaceOrder(line, Order.sqlFieldNameValueParam, table.columnInfos
                    .stream()
                    .filter(c -> isNotCreateTimeUpdateTime(c.columnNameJava))
                    .map(c -> String.format("@Param(\"%s\") %s %s", c.columnNameJava, c.dataTypeJava, c.columnNameJava))
                    .collect(Collectors.joining(d + n + getLeftWhitespace(line, Order.sqlFieldNameValueParam))));

            if (Order.check(line, Order.sqlFieldNameValue)) {
                StringBuffer sb = new StringBuffer();
                int temp_length = 0;
                String wp = getWhitespace(calcLeftNum(line, Order.sqlFieldNameValue) - 1);
                for (ColumnInfo c : table.columnInfos) {
                    if (Order.checkNeedUpdate(c.columnName)) {
                        String sqlTemp = String.format(" %s = %s,", c.columnName,
                                equalsIgnoreCase("update_time", c.columnName) ? "now()" : String.format("#{%s}", c.columnNameJava));
                        temp_length += sqlTemp.length();
                        if (temp_length > 110) {
                            sb.append("\" +\n" + wp + "\"");
                            temp_length = 0;
                        }
                        sb.append(sqlTemp);
                    }
                }
                lineReplaceOrder(line, Order.sqlFieldNameValue, sb.substring(0, sb.length() - 1));
            }

            lineReplaceOrder(line, Order.mapperSelectKeyValue,
                    "\" WHERE "
                            + table.columnInfos
                            .stream()
                            .filter(c -> isNotIdCreateTimeUpdateTime(c.columnNameJava))
                            .map(c -> String.format("a.%s = #{%s}", c.columnName, c.columnNameJava))
                            .collect(Collectors.joining("\" +" + n + getLeftWhitespace(line, Order.mapperSelectKeyValue) + "\" AND "))
                            + "\" +");

            lineReplaceOrder(line, Order.mapperSelectKeyValueParam, table.columnInfos
                    .stream()
                    .filter(c -> isNotIdCreateTimeUpdateTime(c.columnNameJava))
                    .map(c -> String.format("@Param(\"%s\") %s %s", c.columnNameJava, c.dataTypeJava, c.columnNameJava))
                    .collect(Collectors.joining(d + n + getLeftWhitespace(line, Order.mapperSelectKeyValueParam))));

            lineReplaceOrder(line, Order.serFindTypeName, table.columnInfos
                    .stream()
                    .filter(c -> isNotIdCreateTimeUpdateTime(c.columnNameJava))
                    .map(c -> String.format("%s %s", c.dataTypeJava, c.columnNameJava))
                    .collect(Collectors.joining(d + n + getLeftWhitespace(line, Order.serFindTypeName))));

            lineReplaceOrder(line, Order.serviceFindName, table.columnInfos
                    .stream()
                    .filter(c -> isNotIdCreateTimeUpdateTime(c.columnNameJava))
                    .map(c -> c.columnNameJava)
                    .collect(Collectors.joining(d + n + getLeftWhitespace(line, Order.serviceFindName))));

            lineReplaceOrder(line, Order.commentList, "'" + table.columnInfos
                    .stream()
                    .filter(c -> isNotIdCreateTimeUpdateTime(c.columnNameJava)
                            && !c.columnName.toUpperCase().endsWith("_ID")
                            && !c.isText)
                    .map(c -> c.columnComment)
                    .collect(Collectors.joining("', '")) + "'");

            if (Order.check(line, Order.columnNameJavaList)) {
                StringBuilder sb = new StringBuilder();
                for (ColumnInfo c : table.columnInfos) {
                    if (isIdCreateTimeUpdateTime(c.columnNameJava)
                            || c.columnName.toUpperCase().endsWith("_ID")
                            || c.isText) {
                        continue;
                    }
                    if (!sb.isEmpty()) {
                        sb.append(", ");
                    }
                    // todo : 'amount'
                    // todo : ['status', DEMO4_STATUS]
                    if (isNotEmpty(c.select)) {
                        sb.append(String.format("['%s', %s]", c.columnNameJava, c.constantName));
                    } else {
                        sb.append(String.format("'%s'", c.columnNameJava));
                    }
                }
                lineReplaceOrder(line, Order.columnNameJavaList, sb);
            }
            this.codeList.add(line.toString());
        }
    }

    /**
     * 将占位符，替换成 具体的内容
     **/
    public void lineReplaceOrder(StringBuilder sb, Order order, Object value) {
        String key = Order.getOrder(order);
        while (sb.indexOf(key) != -1) {
            sb.replace(sb.indexOf(key), sb.indexOf(key) + key.length(), ObjectUtils.isEmpty(value) ? " " : value.toString());

        }
    }

    public int calcLeftNum(StringBuilder line, Order order) {
        if (Order.check(line, order)) {
            return line.substring(0, line.indexOf(Order.getOrder(order))).length();
        }
        return -1;
    }

    public String getLeftWhitespace(String line, Order order) {
        return getWhitespace(calcLeftNum(new StringBuilder(line), order));
    }

    public String getLeftWhitespace(StringBuilder line, Order order) {
        return getWhitespace(calcLeftNum(line, order));
    }

    public List<String> copyCodeListAndClear() {
        List<String> codeTempList = new ArrayList<>(this.codeList);
        this.codeList.clear();
        return codeTempList;
    }

    public String getClazzNameSuffix() {
        return info.fileNameSuffix.replace(".java", "");
    }


    /**
     * 创建列的说明数据
     * | 参数 | 必填 | 类型 | 说明 |
     * |:
     * | page                | 否 | 整数     | 页码,第几页,编号从 1 开始, 默认第1页
     * | size                | 否 | 整数     | 每页多少条数据,默认值:1 , 最大1000
     * // todo : 一般用于修改接口
     **/
    public void columnNoteWithLength(List<ColumnInfo> list) {
        for (int i = 0; i < list.size(); i++) {
            ColumnInfo c = list.get(i);
            boolean required = false;
            String max = "";
            // |参数名 |参数描述 |是否必填 |类型 |最大长度(整数,小数) |说明
            // |id |权限id |是 |Integer | |1 | | |权限id
            if ("NO".equals(c.isNullable)) {
                required = true;
                max = "";
            }
            String name = c.columnNameJava;
            String des = c.columnComment;
            if (isNotEmpty(c.select)) {
                StringBuilder sb = new StringBuilder();
                c.select.stream().forEach(one -> {
                    sb.append(String.format("%s : %s</br>", one.key, one.value));
                });
                des += "</br>" + sb.substring(0, sb.length() - "</br>".length());
            } else if (c.isConstant) {
                des += "</br>" + "常量";
            }
            if (isNotEmpty(c.typeDocNote)) {
                des += "</br>" + c.typeDocNote;
            }
            String type = c.typeDoc;
            // 最大长度(整数.小数)
            if (TypeToJavaData.isString(c.dataTypeJava)) {
                max = c.characterMaximumLength;
            }
            // Integer
            if (TypeToJavaData.isInt(c.dataTypeJava)) {
                max = c.numericPrecision + ",0";
            }
            // BigDecimal
            if (TypeToJavaData.isBigDecimal(c.dataTypeJava)) {
                // 小数部分为空
                if (StringUtils.isEmpty(c.numericScale)) {
                    max = c.numericPrecision;
                } else {
                    // 有小数部分
                    max = (c.numericPrecisionInt - c.numericScaleInt) + "," + c.numericScale;
                }
            }
            // 其他情况
            if (StringUtils.isEmpty(max)) {
                if (StringUtils.isNotEmpty(c.characterMaximumLength)) {
                    max = c.characterMaximumLength;
                } else if (StringUtils.isEmpty(c.numericScale)) {
                    max = c.numericPrecision;
                } else {
                    // 有小数部分
                    max = c(c.numericPrecision) + d + c.numericScale;
                }
            }
            StringBuffer sb = new StringBuffer();
            queryFindParamDocOneLine(sb, "", name, des, type, max, required);
            this.codeList.add(sb.toString());
        }
    }

    public static String queryFindParamDoc(String wp, Table table) {
        StringBuffer sb = new StringBuffer();
        for (QueryMapFindParam f : Table.pageBoundsFindParamStatic) {
            queryFindParamDocOneLine(sb, wp, f.key, f.des, f.type);
        }
        for (QueryMapFindParam f : Table.idFindParamStatic) {
            queryFindParamDocOneLine(sb, wp, f.key, f.des, f.type);
        }
        for (int i = 0; i < table.columnInfos.size(); i++) {
            ColumnInfo c = table.columnInfos.get(i);
            for (FindData f : c.findData) {
                if (Table.noneMatchParamStatic(f.name)) {
                    String des = f.comment;
                    if (isNotEmpty(c.select)) {
                        StringBuilder sbTemp = new StringBuilder();
                        c.select.forEach(one -> {
                            sbTemp.append(String.format("%s : %s</br>", one.key, one.value));
                        });
                        des += "</br>" + sbTemp.toString();
                    } else if (c.isConstant) {
                        des += "</br>" + "常量";
                    }
                    if (isNotEmpty(c.typeDocNote)) {
                        des += "</br>" + c.typeDocNote;
                    }
                    queryFindParamDocOneLine(sb, wp, f.name, des, c.typeDoc);
                }
            }
        }
        for (QueryMapFindParam f : Table.queryMapFindParamStatic) {
            queryFindParamDocOneLine(sb, wp, f.key, f.des, f.type);
        }
        return sb.toString();
    }

    public static void queryFindParamDocOneLine(StringBuffer sb, String wp, String name, String des, String type) {
        queryFindParamDocOneLine(sb, wp, name, des, type, null, false);
    }

    /**
     * 生成一行的数据, 在查询中
     **/
    public static void queryFindParamDocOneLine(StringBuffer sb,
                                                String wp,
                                                String name,
                                                String des,
                                                String type,
                                                String maxLength,
                                                boolean required) {
        String[] templateLine = "| page                | 否 | 整数     | 编号从 1 开始, 默认第1页".split("\\|");
        int templateNameLength = strLength(templateLine[1]);
        int templateRequiredLength = strLength(templateLine[2]);
        int templateTypeLength = strLength(templateLine[3]);
        // TODO | all          | 否     | 字符串   | 分页状态下,查询出全部数据,最大20000,相当于page=1,size=20000
        List<String> fl = new ArrayList<String>();
        fl.add(w + getWhitespaceRight(templateNameLength - 1, name));
        fl.add(w + getWhitespaceRight(templateRequiredLength - 1, required ? "是" : "否"));
        fl.add(w + getWhitespaceRight(templateTypeLength - 1, type));
        if (isNotEmpty(maxLength)) {
            if (maxLength.contains(",")) {
                String[] rs = maxLength.split(",");
                des += String.format("</br>***整数最大长度:%s***", rs[0]);
                if (isNotEmpty(rs[1])) {
                    des += String.format("</br>***小数最大长度:%s***", rs[1]);
                }
            } else {
                des += String.format("</br>***最大长度:%s***", maxLength);
            }
        }
        fl.add(w + des);
        sb.append(sb.isEmpty() ? "" : "\n");
        sb.append(wp + "|" + String.join("|", fl));
    }

    public void removeLastCountString(int removeLastCount) {
        removeLastCountString(1, removeLastCount);
    }

    /**
     * 去掉最后几行的字符串中末尾的若干字符
     *
     * @param removeLastRowCount 要处理的最后几行的数量
     * @param removeLastCount    每行要去掉的字符数
     */
    public void removeLastCountString(int removeLastRowCount, int removeLastCount) {
        int size = codeList.size();
        int start = size - removeLastRowCount;
        if (start < 0) return; // 安全检查，防止越界
        for (int i = start; i < size; i++) {
            String line = codeList.get(i);
            if (line.length() >= removeLastCount) {
                codeList.set(i, line.substring(0, line.length() - removeLastCount));
            } else {
                codeList.set(i, ""); // 如果要移除的字符数大于行长度，则置空
            }
        }
    }


}
