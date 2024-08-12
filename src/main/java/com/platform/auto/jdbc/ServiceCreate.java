package com.platform.auto.jdbc;

import com.platform.auto.config.Config;
import com.platform.auto.jdbc.base.BaseCreate;
import com.platform.auto.jdbc.model.ColumnInfo;
import com.platform.auto.jdbc.model.PageListParam;
import com.platform.auto.jdbc.model.Table;
import com.platform.auto.sys.order.Order;
import com.platform.auto.util.AutoUtil;
import com.platform.auto.util.FileUtil;

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
public class ServiceCreate extends BaseCreate {
    /**
     * 加载模板
     *
     * @param table
     */
    public ServiceCreate(Table table) throws Exception {
        new ServiceCreate(table, false);
    }

    /**
     * @param isList : 是否只把生成的数据, 放入到 list 中, 不做其他的处理
     **/
    public ServiceCreate(Table table, boolean isList) throws Exception {
        super(Config.getConfig().template.service, table);
        List<String> codeTempList = this.copyCodeListAndClear();
        for (String line : codeTempList) {
            if (Order.check(line, Order.importService)) {
                importService();
            } else if (Order.check(line, Order.importServiceCollectors)) {
                importServiceCollectors();
            } else if (Order.check(line, Order.autowiredService)) {
                autowiredService();
            } else if (Order.check(line, Order.findMethod)) {
                findMethod();
            } else {
                this.codeList.add(line);
            }
        }

        if (!isList) {
            AutoUtil.newCodeToFile(codeList, FileUtil.createFileDB(table.tableNameJava + "Service.java", table.javaFilePath));
        }
    }

    private void importServiceCollectors() {
        if (isNotEmpty(table.otherTable) || isNotEmpty(table.relateTable)) {
            codeList.add("import java.util.Set;");
            codeList.add("import java.util.stream.Collectors;");
        }
    }

    private void importService() {
        for (Table t : table.otherTable) {
            // import com.platform.db.admin.customer.*;
            codeList.add(String.format("import %s.*;", (Config.getConfig().dbPackage + "." + t.tableNameJavaParam.toLowerCase())));
        }
        for (PageListParam param : table.relateTable) {
            // import com.platform.db.admin.customer.*;
            codeList.add(String.format("import %s.*;", (Config.getConfig().dbPackage + "." + param.otherTable.tableNameJavaParam.toLowerCase())));
        }
    }

    //    @Autowired
//    private CustomerService customerService;
    private void autowiredService() {
        for (Table ta : table.otherTable) {
            codeList.add(t + "@Autowired");
            codeList.add(t + String.format("private %sService %sService;\n", ta.tableNameJava, ta.tableNameJavaParam));
        }
        for (PageListParam param : table.relateTable) {
            codeList.add(t + "@Autowired");
            codeList.add(t + String.format("private %sService %sService;\n", param.otherTable.tableNameJava, param.otherTable.tableNameJavaParam));
        }
    }

    private void findMethod() {
        if (isNotEmpty(table.otherTable) || isNotEmpty(table.relateTable)) {
            for (ColumnInfo columnInfo : table.columnInfos) {
                if (columnInfo.otherTable == null) {
                    continue;
                }
                codeList.add(t2 + String.format("Set<Integer> %sIds = page.stream().map(one -> one.%s).collect(Collectors.toSet());",
                        columnInfo.otherTable.tableNameJavaParam, columnInfo.columnNameJava));
                codeList.add(t2 + String.format("PageList<%sData> %sPageList = %sService.find(QueryMap.ofIDS(%sIds));",
                        columnInfo.otherTable.tableNameJava, columnInfo.otherTable.tableNameJavaParam,
                        columnInfo.otherTable.tableNameJavaParam, columnInfo.otherTable.tableNameJavaParam));
            }
            for (PageListParam param : table.relateTable) {
                codeList.add(t2 + String.format("Set<Integer> %ss = page.stream().map(one -> one.%s).collect(Collectors.toSet());",
                        param.thisTableColumn.columnNameJava, param.thisTableColumn.columnNameJava));
                codeList.add(t2 + String.format("PageList<%sData> %sPageList = %sService.find(QueryMap.of(\"%s\", %ss));",
                        param.otherTable.tableNameJava, param.otherTable.tableNameJavaParam,
                        param.otherTable.tableNameJavaParam,
                        param.otherTableColumn.columnNameJava, param.thisTableColumn.columnNameJava));
            }
            codeList.add(t2 + String.format("for (%sData %s : page) {", table.tableNameJava, table.tableNameJavaParam));
            for (ColumnInfo columnInfo : table.columnInfos) {
                if (columnInfo.otherTable == null) {
                    continue;
                }
                // demo3.customer = customerPageList.stream().filter(customerPage -> equals(demo3.customerId, customerPage.id)).findFirst().orElse(null);
                codeList.add(t3 + String.format("%s.%s = %sPageList.stream().filter(%sData -> equals(%s.%s, %sData.id)).findFirst().orElse(null);",
                        table.tableNameJavaParam, columnInfo.otherTable.tableNameJavaParam,
                        columnInfo.otherTable.tableNameJavaParam, columnInfo.otherTable.tableNameJavaParam, table.tableNameJavaParam,
                        columnInfo.columnNameJava, columnInfo.otherTable.tableNameJavaParam));
            }
            for (PageListParam param : table.relateTable) {
                // demo3.customer = customerPageList.stream().filter(customerPage -> equals(demo3.customerId, customerPage.id)).collect(Collectors.toList());
                if (param.more) {
                    codeList.add(t3 + String.format("%s.%sList = %sPageList.stream().filter(%sData -> equals(%s.%s, %sData.%s)).collect(Collectors.toList());",
                            table.tableNameJavaParam, param.otherTable.tableNameJavaParam,
                            param.otherTable.tableNameJavaParam, param.otherTable.tableNameJavaParam, table.tableNameJavaParam,
                            param.thisTableColumn.columnNameJava, param.otherTable.tableNameJavaParam, param.otherTableColumn.columnNameJava));
                } else {
                    codeList.add(t3 + String.format("%s.%s = %sPageList.stream().filter(%sPage -> equals(%s.%s, %sPage.%s)).findFirst().orElse(null);",
                            table.tableNameJavaParam, param.otherTable.tableNameJavaParam,
                            param.otherTable.tableNameJavaParam, param.otherTable.tableNameJavaParam, table.tableNameJavaParam,
                            param.thisTableColumn.columnNameJava, param.otherTable.tableNameJavaParam, param.otherTableColumn.columnNameJava));
                }
            }
            codeList.add(t2 + "}");
        } else {
            codeList.add("//        Set<Integer> ids = page.stream().map(one -> one.id).collect(Collectors.toSet());\n" +
                    "//        for (" + table.tableNameJava + "Data " + table.tableNameJavaParam + " : page) {\n" +
                    "//\n" +
                    "//        }");
        }
    }

}
