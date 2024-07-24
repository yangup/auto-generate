package com.platform.auto.jdbc;

import com.platform.auto.jdbc.base.BaseCreate;
import com.platform.auto.jdbc.model.ColumnInfo;
import com.platform.auto.jdbc.model.SelectData;
import com.platform.auto.jdbc.model.Table;
import com.platform.auto.sys.order.Order;
import com.platform.auto.util.FileUtil;
import com.platform.auto.util.AutoUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.platform.auto.util.CharUtil.*;

/**
 * yangpu.jdbc.mysql.ModelCreate.java<br>
 * Description : <br>
 *
 * @author YangPu
 * @createTime 2016年7月21日 下午3:50:33
 */
public class UsefulCreate extends BaseCreate {

    /**
     * 加载模板
     *
     * @param table
     */
    public UsefulCreate(Table table) throws Exception {
        new UsefulCreate(table, false);
    }

    public UsefulCreate(Table table, boolean isList) throws Exception {
        super(Constant.useful, table);
        List<String> templateList = this.copyCodeListAndClear();
        for (String line : templateList) {
            if (Order.check(line, Order.jsonStart)) {
                createJson();
                continue;
            } else if (Order.check(line, Order.elDescriptionsItem)) {
                // todo : 查询出这个命令距离左边的位置
                int leftNum = line.substring(0, line.indexOf(Order.getOrder(Order.elDescriptionsItem))).length();
                StringBuffer sb = new StringBuffer();
                for (ColumnInfo c : table.columnInfos) {
                    if (sb.length() > 0) {
                        sb.append(n + getWhitespace(leftNum));
                    }
                    String prefix = "";
                    String suffix = "";
                    if (isIdCreateTimeUpdateTime(c.columnNameJava)
                            || c.columnName.endsWith("_id")) {
                        prefix = "<!--";
                        suffix = "-->";
                    }
                    // todo : <el-descriptions-item label="最小版本">{{temp.minVersion}}</el-descriptions-item>
                    sb.append(prefix + "<el-descriptions-item label=\"" + c.columnComment + "\">{{ temp." + c.columnNameJava + " }}</el-descriptions-item>" + suffix);
                }
                line = line.replace(Order.getOrder(Order.elDescriptionsItem), sb.toString());
            } else if (Order.check(line, Order.elFormItemDetail)) {
                // todo : 查询出这个命令距离左边的位置
                int leftNum = line.substring(0, line.indexOf(Order.getOrder(Order.elFormItemDetail))).length();
                StringBuffer sb = new StringBuffer();
                for (ColumnInfo c : table.columnInfos) {
                    if (sb.length() > 0) {
                        sb.append(n + getWhitespace(leftNum));
                    }
                    String prefix = "";
                    String suffix = "";
                    if (isIdCreateTimeUpdateTime(c.columnNameJava)
                            || c.columnName.endsWith("_id")) {
                        prefix = "<!--";
                        suffix = "-->";
                    }
                    // todo : <el-form-item label="商品名称"><span>121212</span></el-form-item>
                    sb.append(prefix + "<el-form-item label=\"" + c.columnComment + "\"><span>{{temp." + c.columnNameJava + "}}</span></el-form-item>" + suffix);
                }
                line = line.replace(Order.getOrder(Order.elFormItemDetail), sb.toString());
            } else if (Order.check(line, Order.commentUse)) {
                // todo : 查询出这个命令距离左边的位置
                int leftNum = line.substring(0, line.indexOf(Order.getOrder(Order.commentUse))).length();
                StringBuffer sb = new StringBuffer();
                for (ColumnInfo c : table.columnInfos) {
                    if (sb.length() > 0) {
                        sb.append(n + getWhitespace(leftNum));
                    }
                    String columnComment = c.columnComment;
                    if (StringUtils.isEmpty(columnComment)) {
                        columnComment = c.columnNameJava;
                    }
                    sb.append(columnComment);
                }
                line = line.replace(Order.getOrder(Order.commentUse), sb.toString());
            } else if (Order.check(line, Order.sqlColumnNameUse)) {
                // todo : 查询出这个命令距离左边的位置
                int leftNum = line.substring(0, line.indexOf(Order.getOrder(Order.sqlColumnNameUse))).length();
                StringBuffer sb = new StringBuffer();
                for (ColumnInfo c : table.columnInfos) {
                    if (sb.length() > 0) {
                        sb.append(n + getWhitespace(leftNum));
                    }
                    sb.append(c.columnName);
                }
                line = line.replace(Order.getOrder(Order.sqlColumnNameUse), sb.toString());
            } else if (Order.check(line, Order.javaColumnNameUse)) {
                // todo : 查询出这个命令距离左边的位置
                int leftNum = line.substring(0, line.indexOf(Order.getOrder(Order.javaColumnNameUse))).length();
                StringBuffer sb = new StringBuffer();
                for (ColumnInfo c : table.columnInfos) {
                    if (sb.length() > 0) {
                        sb.append(n + getWhitespace(leftNum));
                    }
                    sb.append(c.columnNameJava);
                }
                line = line.replace(Order.getOrder(Order.javaColumnNameUse), sb.toString());
            } else if (Order.check(line, Order.javaParamNameUse)) {
                StringBuffer sb = new StringBuffer();
                for (ColumnInfo c : table.columnInfos) {
                    if (sb.length() > 0) {
                        sb.append(",\n");
                    }
                    sb.append("@RequestParam(name = \"" + c.columnNameJava + "\", required = false) " + c.dataTypeJava + " " + c.columnNameJava + "");
                }
                sb.append(n3);
                for (ColumnInfo c : table.columnInfos) {
                    sb.append(String.format("{\n" +
                            "    title: '%s',\n" +
                            "    key: '%s',\n" +
                            "}", c.columnComment, c.columnNameJava));
                    if (c != table.columnInfos.get(table.columnInfos.size() - 1)) {
                        sb.append(",");
                    }
                }
                sb.append(n3);
                for (ColumnInfo c : table.columnInfos) {
                    sb.append(String.format("{\n" +
                            "    title: '%s',\n" +
                            "    width: 100,\n" +
                            "    key: '%s',\n" +
                            "}", c.columnComment, c.columnNameJava
                    ));
                    if (c != table.columnInfos.get(table.columnInfos.size() - 1)) {
                        sb.append(",");
                    }
                }
                line = line.replace(Order.getOrder(Order.javaParamNameUse), sb.toString());
            }

            // TODO: 2022/5/13 自己的逻辑
            if (Order.check(line, Order.frontSelectConstant)) {
                for (ColumnInfo c : table.columnInfos) {
                    if (isEmpty(c.select)) {
                        continue;
                    }
                    // TODO: 这个变量的名称
                    String constantName = c.constantName;
                    codeList.add("export const " + constantName + " = {");
                    for (int i = 0; i < c.select.size(); i++) {
                        SelectData selectData = c.select.get(i);
                        String last = ",";
                        if (i == c.select.size() - 1) {
                            last = ",";
                        }
                        codeList.add("  " + selectData.key + ": '" + selectData.value + "'" + last + "");
                    }
                    codeList.add("}");
                }
            } else {
                codeList.add(line);
            }
        }

        if (!isList) {
            AutoUtil.newCodeToFile(codeList, FileUtil.createFileOther(table.tableNameJavaParam + "_useful.txt", table.javaFilePath));
        }
    }

    private void createJson() {
        for (int i = 0; i < this.table.columnInfos.size(); i++) {
            ColumnInfo columninfo = this.table.columnInfos.get(i);
            String field = columninfo.columnNameJava;
            String dd = "";
            if (i != this.table.columnInfos.size() - 1) {
                dd = d;
            }
            codeList.add(t + s + field + s + m + w + s + field + s + dd);
        }

    }

}
