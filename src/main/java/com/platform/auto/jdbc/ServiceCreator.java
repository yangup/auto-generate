package com.platform.auto.jdbc;

import com.platform.auto.config.Config;
import com.platform.auto.jdbc.base.BaseCreator;
import com.platform.auto.jdbc.model.ColumnInfo;
import com.platform.auto.jdbc.model.RelateTableInfo;
import com.platform.auto.jdbc.model.TypeToJavaData;
import com.platform.auto.sys.order.Order;
import org.apache.commons.lang3.StringUtils;

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
        boolean useStartEndTime = false;
        ColumnInfo useStartEndTimeColumn = null;
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
                            .replace("-b-", table.tableNameJava)
                            .replace("-c-", c.columnNameJavaFirstToUppercase)
            );
            if (isNotEmpty(c.findData)) {
                if (c.findDataUseList) {
                    codeList.add(note + wp +
                            "wrapper.in(queryMap.hasList(-b-Entity::get-c-), -b-Entity::get-c-, queryMap.getList(-b-Entity::get-c-));"
                                    .replace("-b-", table.tableNameJava)
                                    .replace("-c-", c.columnNameJavaFirstToUppercase)
                    );
                }
                if (c.findDataUseMin) {
                    codeList.add(note + wp +
                            "wrapper.ge(queryMap.hasMin(-b-Entity::get-c-), -b-Entity::get-c-, queryMap.getMin(-b-Entity::get-c-));"
                                    .replace("-b-", table.tableNameJava)
                                    .replace("-c-", c.columnNameJavaFirstToUppercase)
                    );
                }
                if (c.findDataUseMax) {
                    codeList.add(note + wp +
                            "wrapper.lt(queryMap.hasMax(-b-Entity::get-c-), -b-Entity::get-c-, queryMap.getMax(-b-Entity::get-c-));"
                                    .replace("-b-", table.tableNameJava)
                                    .replace("-c-", c.columnNameJavaFirstToUppercase)
                    );
                }
                // 找到第一个 LocalDateTime 的字段, 然后使用 startEndTime
                if (TypeToJavaData.isDateTime(c.dataTypeJava) && !useStartEndTime) {
                    useStartEndTime = true;
                    useStartEndTimeColumn = c;
                }
            }
        }
        if (useStartEndTimeColumn == null) {
            useStartEndTimeColumn = table.columnInfos.stream()
                    .filter(t -> StringUtils.equalsIgnoreCase(t.columnNameJava, "create_time"))
                    .findFirst()
                    .orElse(null);
        }
        if (useStartEndTimeColumn != null) {
            codeList.add("//" + wp +
                    "wrapper.ge(queryMap.hasStartTime(), -b-Entity::get-c-, queryMap.startTime());"
                            .replace("-b-", table.tableNameJava)
                            .replace("-c-", useStartEndTimeColumn.columnNameJavaFirstToUppercase)
            );
            codeList.add("//" + wp +
                    "wrapper.lt(queryMap.hasEndTime(), -b-Entity::get-c-, queryMap.endTime());"
                            .replace("-b-", table.tableNameJava)
                            .replace("-c-", useStartEndTimeColumn.columnNameJavaFirstToUppercase)
            );
        }
    }

    private void serviceFindMethodMore(String line) {
        if (isNotEmpty(table.relateTable)) {
            for (RelateTableInfo param : table.relateTable) {
                codeList.add(t2 + "List<String> -a-List = page.stream().map(a -> a.-a-).filter(StringUtils::isNotEmpty).distinct().collect(Collectors.toList());"
                        .replaceAll("-a-", param.thisTableColumn.columnNameJava)
                );
                String template = "        PageList<-tableNameJava-Data> -tableNameJavaParam-DataList = isEmpty(-thisTableColumn-List) ? null : -tableNameJavaParam-Service.find(\n" +
                        "                QueryMap.builder()\n" +
                        "                        .list(-tableNameJava-Entity::get-otherTableColumnUpper-, -thisTableColumn-List)\n" +
                        "                        .more(queryMap.more())\n" +
                        "                        .build());";
                codeList.add(template.replace("-tableNameJava-", param.otherTable.tableNameJava)
                        .replace("-tableNameJavaParam-", param.otherTable.tableNameJavaParam)
                        .replace("-thisTableColumn-", param.thisTableColumn.columnNameJava)
                        .replace("-otherTableColumn-", param.otherTableColumn.columnNameJava)
                        .replace("-otherTableColumnUpper-", param.otherTableColumn.columnNameJavaFirstToUppercase)
                );
            }
            // 拼接查询
            codeList.add(t2 + String.format("for (%sData a : page) {", table.tableNameJava));
            for (RelateTableInfo param : table.relateTable) {
                if (param.more) {
                    String template = "            a.-tableNameJavaParam-List = -tableNameJavaParam-DataList == null ? null : -tableNameJavaParam-DataList.stream()\n" +
                            "                    .filter(b -> equals(a.-thisTableColumn-, b.-otherTableColumn-))\n" +
                            "                    .collect(Collectors.toList())\n" +
                            "                    .orElse(null);";
                    codeList.add(template.replace("-tableNameJavaParam-", param.otherTable.tableNameJavaParam)
                            .replace("-thisTableColumn-", param.thisTableColumn.columnNameJava)
                            .replace("-otherTableColumn-", param.otherTableColumn.columnNameJava)
                    );
                } else {
                    String template = "            a.-tableNameJavaParam- = -tableNameJavaParam-DataList == null ? null : -tableNameJavaParam-DataList.stream()\n" +
                            "                    .filter(b -> equals(a.-thisTableColumn-, b.-otherTableColumn-))\n" +
                            "                    .findFirst()\n" +
                            "                    .orElse(null);";
                    codeList.add(template.replace("-tableNameJavaParam-", param.otherTable.tableNameJavaParam)
                            .replace("-thisTableColumn-", param.thisTableColumn.columnNameJava)
                            .replace("-otherTableColumn-", param.otherTableColumn.columnNameJava)
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
