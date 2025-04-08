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
                codeList.add(t + "private @Autowired -a-Service -b-Service;\n"
                        .replaceAll("-a-", param.otherTable.tableNameJava)
                        .replaceAll("-b-", param.otherTable.tableNameJavaParam));
            }
        }
    }

    private void serviceFindMethod(String line) {
        String wp = getLeftWhitespace(new StringBuilder(line), Order.serviceFindMethod);
        for (ColumnInfo c : table.columnInfos) {
            String note = "//";
            if (!c.isId && isNotEmpty(c.findData)) {
                note = "";
                codeList.add(note + wp +
                        "wrapper.in(isNotEmpty(queryMap.get(\"-a-s\")), -b-Entity::get-c-, queryMap.get(\"-a-s\"));"
                                .replaceAll("-a-", c.columnNameJava)
                                .replaceAll("-b-", table.tableNameJava)
                                .replaceAll("-c-", firstToUppercase(c.columnNameJava))
                );
            }
            codeList.add(note + wp +
                    "wrapper.eq(isNotEmpty(queryMap.get(\"-a-\")), -b-Entity::get-c-, queryMap.get(\"-a-\"));"
                            .replaceAll("-a-", c.columnNameJava)
                            .replaceAll("-b-", table.tableNameJava)
                            .replaceAll("-c-", firstToUppercase(c.columnNameJava))
            );
        }
    }

    private void serviceFindMethodMore(String line) {
        if (isNotEmpty(table.relateTable)) {
            for (ColumnInfo columnInfo : table.columnInfos) {
                if (columnInfo.otherTable == null) {
                    continue;
                }
                codeList.add(t2 + "Set<String> -a-Ids = page.stream().map(a -> a.-b-).filter(StringUtils::isNotEmpty).collect(Collectors.toSet());"
                        .replaceAll("-a-", columnInfo.otherTable.tableNameJavaParam)
                        .replaceAll("-b-", columnInfo.columnNameJava)
                );
                codeList.add(t2 + "PageList<-b-Data> -a-PageList = isNotEmpty(-a-Ids) ? -a-Service.find(QueryMap.ofIDS(-a-Ids).rawTrue()) : null;"
                        .replaceAll("-a-", columnInfo.otherTable.tableNameJavaParam)
                        .replaceAll("-b-", columnInfo.otherTable.tableNameJava)
                );
            }
            for (PageListParam param : table.relateTable) {
                codeList.add(t2 + "Set<String> -a-s = page.stream().map(a -> a.-a-).filter(StringUtils::isNotEmpty).collect(Collectors.toSet());"
                        .replaceAll("-a-", param.thisTableColumn.columnNameJava)
                );
                codeList.add(t2 + "PageList<-a-Data> -b-PageList = isNotEmpty(-c-s) ? -b-Service.find(QueryMap.of(\"-d-\", -c-s).rawTrue()) : null;"
                        .replaceAll("-a-", param.otherTable.tableNameJava)
                        .replaceAll("-b-", param.otherTable.tableNameJavaParam)
                        .replaceAll("-c-", param.thisTableColumn.columnNameJava)
                        .replaceAll("-d-", param.otherTableColumn.columnNameJava)
                );
            }
            // 拼接查询
            codeList.add(t2 + String.format("for (%sData a : page) {", table.tableNameJava));
            for (ColumnInfo columnInfo : table.columnInfos) {
                if (columnInfo.otherTable == null) {
                    continue;
                }
                // a.customer = customerPageList == null ? null : customerPageList.stream().filter(b -> equals(a.customerId, b.id)).findFirst().orElse(null);
                codeList.add(t3 + "a.-a- = -a-PageList == null ? null ? -a-PageList.stream().filter(b -> equals(a.-a-, b.id)).findFirst().orElse(null);"
                        .replaceAll("-a-", columnInfo.otherTable.tableNameJavaParam)
                );
            }
            for (PageListParam param : table.relateTable) {
                if (param.more) {
                    codeList.add(t3 + "a.-a-List = -a-PageList == null ? null : -a-PageList.stream().filter(b -> equals(a.-b-, b.-c-)).collect(Collectors.toList());"
                            .replaceAll("-a-", param.otherTable.tableNameJavaParam)
                            .replaceAll("-b-", param.thisTableColumn.columnNameJava)
                            .replaceAll("-c-", param.otherTableColumn.columnNameJava)
                    );
                } else {
                    codeList.add(t3 + "a.-a- = -a-PageList == null ? null : -a-PageList.stream().filter(b -> equals(a.-b-, b.-c-)).findFirst().orElse(null);"
                            .replaceAll("-a-", param.otherTable.tableNameJavaParam)
                            .replaceAll("-b-", param.thisTableColumn.columnNameJava)
                            .replaceAll("-c-", param.otherTableColumn.columnNameJava)
                    );
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
