package com.platform.auto.jdbc;

import com.platform.auto.config.Config;
import com.platform.auto.jdbc.base.BaseCreator;
import com.platform.auto.jdbc.model.ColumnInfo;
import com.platform.auto.jdbc.model.RelateTableInfo;
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
        for (RelateTableInfo param : table.relateTable) {
            // import com.platform.db.admin.customer.*;
            codeList.add(String.format("import %s.*;", (Config.getPathByType(DB).packageName + "." + param.otherTable.tableNameJavaParam.toLowerCase())));
        }
    }

    //    private @Autowired CustomerService customerService;
    private void autowiredService() {
        if (table.relateTable != null) {
            for (RelateTableInfo param : table.relateTable) {
                codeList.add(t + "private @Autowired -a-Service -b-Service;\n"
                        .replaceAll("-a-", param.otherTable.tableNameJava)
                        .replaceAll("-b-", param.otherTable.tableNameJavaParam));
            }
        }
    }

    private void serviceFindMethod(String line) {
        String wp = getLeftWhitespace(new StringBuilder(line), Order.serviceFindMethod);
        for (ColumnInfo c : table.columnInfos) {
            if (isIdCreateTimeUpdateTime(c.columnNameJava)) {
                continue;
            }
            String note = "//";
            if (isNotEmpty(c.findData)) {
                note = "";
            }
            codeList.add(note + wp +
                    "wrapper.eq(queryMap.has(-b-Entity::get-c-), -b-Entity::get-c-, queryMap.get(-b-Entity::get-c-));"
                            .replaceAll("-b-", table.tableNameJava)
                            .replaceAll("-c-", c.columnNameJavaFirstToUppercase)
            );
            if (isNotEmpty(c.findData)) {
                codeList.add(note + wp +
                        "wrapper.in(queryMap.hasList(-b-Entity::get-c-), -b-Entity::get-c-, queryMap.getList(-b-Entity::get-c-));"
                                .replaceAll("-b-", table.tableNameJava)
                                .replaceAll("-c-", c.columnNameJavaFirstToUppercase)
                );
            }
        }
    }

    private void serviceFindMethodMore(String line) {
        if (isNotEmpty(table.relateTable)) {
            for (RelateTableInfo param : table.relateTable) {
                codeList.add(t2 + "List<String> -a-List = page.stream().map(a -> a.-a-).filter(StringUtils::isNotEmpty).distinct().collect(Collectors.toList());"
                        .replaceAll("-a-", param.thisTableColumn.columnNameJava)
                );
                // PageList<SystemUserSettingData> susPList = isNotEmpty(idList) ? systemUserSettingService.find(QueryMap.ofList(SystemUserSettingEntity::getUserId, idList).rawTrue()) : null;
                codeList.add(t2 + ("PageList<-tableNameJava-Data> -tableNameSimple-PList = isNotEmpty(-thisTableColumn-List) ?" +
                        " -tableNameJavaParam-Service.find(QueryMap.ofList(-tableNameJava-Entity::get-otherTableColumnUpper-, -thisTableColumn-List).rawTrue()) : null;")
                        .replaceAll("-tableNameJava-", param.otherTable.tableNameJava)
                        .replaceAll("-tableNameJavaParam-", param.otherTable.tableNameJavaParam)
                        .replaceAll("-tableNameSimple-", param.otherTable.tableNameSimple)
                        .replaceAll("-thisTableColumn-", param.thisTableColumn.columnNameJava)
                        .replaceAll("-otherTableColumn-", param.otherTableColumn.columnNameJava)
                        .replaceAll("-otherTableColumnUpper-", param.otherTableColumn.columnNameJavaFirstToUppercase)
                );
            }
            // 拼接查询
            codeList.add(t2 + String.format("for (%sData a : page) {", table.tableNameJava));
            for (RelateTableInfo param : table.relateTable) {
                if (param.more) {
                    codeList.add(t3 + "a.-a-List = -a1-PList != null ? -a1-PList.stream().filter(b -> equals(a.-b-, b.-c-)).collect(Collectors.toList()) : null;"
                            .replaceAll("-a-", param.otherTable.tableNameJavaParam)
                            .replaceAll("-a1-", param.otherTable.tableNameSimple)
                            .replaceAll("-b-", param.thisTableColumn.columnNameJava)
                            .replaceAll("-c-", param.otherTableColumn.columnNameJava)
                    );
                } else {
                    codeList.add(t3 + "a.-a- = -a1-PList != null ? -a1-PList.stream().filter(b -> equals(a.-b-, b.-c-)).findFirst().orElse(null) : null;"
                            .replaceAll("-a-", param.otherTable.tableNameJavaParam)
                            .replaceAll("-a1-", param.otherTable.tableNameSimple)
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
