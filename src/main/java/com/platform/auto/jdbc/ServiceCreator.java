package com.platform.auto.jdbc;

import com.platform.auto.config.Config;
import com.platform.auto.jdbc.base.BaseCreator;
import com.platform.auto.jdbc.model.ColumnInfo;
import com.platform.auto.jdbc.model.PageListParam;
import com.platform.auto.sys.order.Order;

import java.util.List;

import static com.platform.auto.util.CharUtil.*;

/**
 * <p>
 * yangpu.jdbc.mysql.ModelCreate.java
 * </p>
 * <p>
 * description :
 * </p>
 *
 * @author YangPu
 * @createTime 2016年7月21日 下午3:50:33
 */
public class ServiceCreator extends BaseCreator {

    public ServiceCreator(BaseCreator baseCreator) {
        super(baseCreator);
    }

    @Override
    public void create() {
        List<String> codeTempList = this.copyCodeListAndClear();
        for (String line : codeTempList) {
            if (Order.check(line, Order.importService)) {
                importService();
            } else if (Order.check(line, Order.importServiceCollectors)) {
                importServiceCollectors();
            } else if (Order.check(line, Order.autowiredService)) {
                autowiredService();
            } else if (Order.check(line, Order.serviceFindMethod)) {
                serviceFindMethod(line);
            } else if (Order.check(line, Order.serviceFindMethodMore)) {
                serviceFindMethodMore(line);
            } else {
                this.codeList.add(line);
            }
        }
    }

    private void importServiceCollectors() {
        if (isNotEmpty(table.relateTable)) {
            codeList.add("import java.util.Set;");
            codeList.add("import java.util.stream.Collectors;");
        }
    }

    private void importService() {
        for (PageListParam param : table.relateTable) {
            // import com.platform.db.admin.customer.*;
            codeList.add(String.format("import %s.*;", (Config.getPathByType(DB).packageName + "." + param.otherTable.tableNameJavaParam.toLowerCase())));
        }
    }

    //    private @Autowired CustomerService customerService;
    private void autowiredService() {
        if (table.relateTable != null) {
            for (PageListParam param : table.relateTable) {
                codeList.add(t + String.format("private @Autowired %sService %sService;\n", param.otherTable.tableNameJava, param.otherTable.tableNameJavaParam));
            }
        }
    }

    private void serviceFindMethod(String line) {
        String wp = getLeftWhitespace(new StringBuilder(line), Order.serviceFindMethod);
        for (ColumnInfo c : table.columnInfos) {
            String note = "//";
            if (!c.isId && isNotEmpty(c.findData)) {
                note = "";
            }
            codeList.add(note + wp + String.format("wrapper.eq(isNotEmpty(queryMap.get(\"%s\")), %sEntity::get%s, queryMap.get(\"%s\"));",
                    c.columnNameJava, table.tableNameJava, firstToUppercase(c.columnNameJava), c.columnNameJava));
        }
    }

    private void serviceFindMethodMore(String line) {
        if (isNotEmpty(table.relateTable)) {
            for (ColumnInfo columnInfo : table.columnInfos) {
                if (columnInfo.otherTable == null) {
                    continue;
                }
                codeList.add(t2 + String.format("Set<String> %sIds = page.stream().map(d -> d.%s).filter(StringUtils::isNotEmpty).collect(Collectors.toSet());",
                        columnInfo.otherTable.tableNameJavaParam, columnInfo.columnNameJava));
                codeList.add(t2 + String.format("PageList<%sData> %sPageList = isNotEmpty(%sIds) ? %sService.find(QueryMap.ofIDS(%sIds).rawTrue()) : null;",
                        columnInfo.otherTable.tableNameJava, columnInfo.otherTable.tableNameJavaParam, columnInfo.otherTable.tableNameJavaParam,
                        columnInfo.otherTable.tableNameJavaParam, columnInfo.otherTable.tableNameJavaParam));
            }
            for (PageListParam param : table.relateTable) {
                codeList.add(t2 + String.format("Set<String> %ss = page.stream().map(d -> d.%s).filter(StringUtils::isNotEmpty).collect(Collectors.toSet());",
                        param.thisTableColumn.columnNameJava, param.thisTableColumn.columnNameJava));
                codeList.add(t2 + String.format("PageList<%sData> %sPageList = isNotEmpty(%ss) ? %sService.find(QueryMap.of(\"%s\", %ss).rawTrue()) : null;",
                        param.otherTable.tableNameJava, param.otherTable.tableNameJavaParam, param.thisTableColumn.columnNameJava,
                        param.otherTable.tableNameJavaParam,
                        param.otherTableColumn.columnNameJava, param.thisTableColumn.columnNameJava));
            }
            // 拼接查询
            codeList.add(t2 + String.format("for (%sData a : page) {", table.tableNameJava));
            for (ColumnInfo columnInfo : table.columnInfos) {
                if (columnInfo.otherTable == null) {
                    continue;
                }
                // a.customer = customerPageList == null ? null : customerPageList.stream().filter(b -> equals(a.customerId, b.id)).findFirst().orElse(null);
                codeList.add(t3 + String.format("a.%s = %sPageList == null ? null ? %sPageList.stream().filter(b -> equals(a.%s, b.id)).findFirst().orElse(null);",
                        columnInfo.otherTable.tableNameJavaParam, columnInfo.otherTable.tableNameJavaParam,
                        columnInfo.otherTable.tableNameJavaParam, columnInfo.otherTable.tableNameJavaParam));
            }
            for (PageListParam param : table.relateTable) {
                if (param.more) {
                    codeList.add(t3 + String.format("a.%sList = %sPageList == null ? null : %sPageList.stream().filter(b -> equals(a.%s, b.%s)).collect(Collectors.toList());",
                            param.otherTable.tableNameJavaParam, param.otherTable.tableNameJavaParam,
                            param.otherTable.tableNameJavaParam, param.otherTable.tableNameJavaParam, param.otherTableColumn.columnNameJava));
                } else {
                    codeList.add(t3 + String.format("a.%s = %sPageList == null ? null : %sPageList.stream().filter(b -> equals(a.%s, b.%s)).findFirst().orElse(null);",
                            param.otherTable.tableNameJavaParam, param.otherTable.tableNameJavaParam,
                            param.otherTable.tableNameJavaParam,
                            param.thisTableColumn.columnNameJava, param.otherTableColumn.columnNameJava));
                }
            }
            codeList.add(t2 + "}");
        } else {
            codeList.add("//        Set<String> ids = page.stream().map(one -> one.id).collect(Collectors.toSet());\n" +
                    "//        for (" + table.tableNameJava + "Data " + table.tableNameJavaParam + " : page) {\n" +
                    "//\n" +
                    "//        }");
        }
    }

}
