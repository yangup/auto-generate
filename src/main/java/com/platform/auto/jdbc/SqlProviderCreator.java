package com.platform.auto.jdbc;

import com.platform.auto.config.Config;
import com.platform.auto.config.ConfigEntity;
import com.platform.auto.jdbc.base.BaseCreator;
import com.platform.auto.jdbc.model.ColumnInfo;
import com.platform.auto.jdbc.model.FindData;
import com.platform.auto.jdbc.model.SelectData;
import com.platform.auto.jdbc.model.Table;
import com.platform.auto.sys.order.Order;
import com.platform.auto.util.AutoUtil;
import com.platform.auto.util.FileUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.platform.auto.util.CharUtil.*;


public class SqlProviderCreator extends BaseCreator {

    /**
     * 加载模板
     *
     * @param table
     */
    public SqlProviderCreator(Table table, ConfigEntity.Info info) throws Exception {
        this(table, info, false);
    }

    /**
     * @param isList : 是否只把生成的数据, 放入到 list 中, 不做其他的处理
     **/
    public SqlProviderCreator(Table table, ConfigEntity.Info info, boolean isList) throws Exception {
        super(info.template, table);
        generateConstant(table);
        List<String> codeTempList = this.copyCodeListAndClear();
        for (String lineTemp : codeTempList) {
            StringBuilder line = new StringBuilder(lineTemp);
            if (Order.check(line, Order.queryMapParam)) {
                lineReplaceOrder(line, Order.queryMapParam, getQueryMapParam(table, line));
            }
            codeList.add(line.toString());
        }
        if (!isList) {
            AutoUtil.newCodeToFile(codeList, FileUtil.createFile(table, info, SQLPROVIDER_JAVA));
        }
    }

    /***
     * 在 Constant.java 中生成需要的常量
     * **/
    private void generateConstant(Table table) throws Exception {
        List<String> constantList = AutoUtil.fileToList(new File(Config.getJavaFilePath(Config.getPathByType(CONSTANT)) + "Constant.java"));
        List<String> newCoodeList = new ArrayList<>(constantList.size() * 2);
        List<String> addCoodeList = new ArrayList<>();

        for (ColumnInfo c : table.columnInfos) {
            if (isIdCreateTimeUpdateTime(c.columnNameJava)) {
                continue;
            }
            for (FindData findData : c.findData) {
                String line = String.format("    public static final String %s = \"%s\";", findData.staticName, findData.name);
                if (AutoUtil.listIndex(addCoodeList, line) == -1) {
                    addCoodeList.add(line);
                }
            }
            if (isEmpty(c.select)) {
                continue;
            }
            // TODO: 这个变量的名称
            for (SelectData selectData : c.select) {
//                String line = String.format("public static final String %s = \"%s\";", c.constantName + "__" + selectData.key, selectData.key);
                String line = String.format("    public static final String %s = \"%s\";", selectData.key, selectData.key);
                if (AutoUtil.listIndex(addCoodeList, line) == -1) {
                    addCoodeList.add(line);
                }
            }
        }

        String indexStr = "auto-generate";
        for (String constant : constantList) {
            newCoodeList.add(constant);
            // todo : 在这个位置开始写入数据
            if (constant.toUpperCase().contains(indexStr.toUpperCase())) {
                for (String addCode : addCoodeList) {
                    if (AutoUtil.listIndex(constantList, addCode) == -1) {
                        newCoodeList.add(addCode);
                    }
                }
            }
        }
        if (StringUtils.equals(String.join("", constantList), String.join("", newCoodeList))) {
            return;
        }
        AutoUtil.listToFile(new File(Config.getJavaFilePath(Config.getPathByType(CONSTANT)) + "Constant.java"), newCoodeList);
    }

    //sql.whereIsNotNULL("a.alarm_site_id", equal(queryMap.get(ALARM_SITE_ID)));
    private String getQueryMapParam(Table table, StringBuilder line) {
        List<String> stringList = new ArrayList<>();
        for (ColumnInfo c : table.columnInfos) {
            if (isIdCreateTimeUpdateTime(c.columnNameJava)) {
                continue;
            }
            for (FindData f : c.findData) {
                // sql.whereIsNotNULL("a.alarm_site_id", equal(queryMap.get(ALARM_SITE_ID)));
                String operator = f.operator;
                String sqlMethod = "str";
                if (f.isNumber) {
                    sqlMethod = "number";
                } else if (f.isConstant) {
                    // TODO : 是常量, 或者是 外键id
                    sqlMethod = "inComma";
                } else if (isEmpty(f.operator)) {
                    // TODO : 没有操作符,那就是普通的文本
                    if (c.characterMaximumLengthInt >= 256) {
//                    if (c.characterMaximumLengthInt >= 0) {
                        sqlMethod = "like";
                    } else {
                        sqlMethod = "equal";
                    }
                }
                if (c.dataTypeJava.contains("List")) {
                    sqlMethod = "in";
                }
                if (isEmpty(operator)) {
                    operator = "";
                } else {
                    operator = " " + operator;
                }
                stringList.add(String.format("sql.whereIsNotNULL(\"a.%s%s\", %s(queryMap.get(\"%s\")));", c.columnName, operator, sqlMethod, f.name));
            }
        }
        // todo : 查询出这个命令距离左边的位置
        String wp = getLeftWhitespace(line, Order.queryMapParam);
        return String.join("\n" + wp, stringList) + "\n" + wp;
    }


}
