package com.platform.auto.jdbc;

import com.platform.auto.config.Config;
import com.platform.auto.jdbc.base.BaseCreator;
import com.platform.auto.jdbc.model.PageListParam;
import com.platform.auto.jdbc.model.Table;
import com.platform.auto.sys.order.Order;

import java.util.List;

import static com.platform.auto.util.CharUtil.*;

/**
 * yangpu.jdbc.mysql.ModelCreate.java<br>
 * Description : <br>
 *
 * @author YangPu
 * @createTime 2016年7月21日 下午3:50:33
 */
public class DataCreator extends BaseCreator {

    public DataCreator(BaseCreator baseCreator) {
        super(baseCreator);
    }

    @Override
    public void create() {
        List<String> templateList = this.copyCodeListAndClear();
        for (String line : templateList) {
            // TODO: 可以添加其他逻辑
            if (Order.check(line, Order.startFieldData)) {
                createField();
            } else if (Order.check(line, Order.importPage)) {
                importData();
            } else if (Order.check(line, Order.importPageList)) {
                importDataList();
            } else {
                codeList.add(line);
            }
        }
    }

    private void createField() {
        codeList.add("    // todo : field\n");
        for (Table t : table.otherTable) {
            codeList.add(String.format("    public %sData %s;\n", t.tableNameJava, t.tableNameJavaParam));
        }
        for (PageListParam param : table.relateTable) {
            if (param.more) {
                codeList.add(String.format("    public List<%sData> %sList;\n", param.otherTable.tableNameJava, param.otherTable.tableNameJavaParam));
            } else {
                codeList.add(String.format("    public %sData %s;\n", param.otherTable.tableNameJava, param.otherTable.tableNameJavaParam));
            }
        }

        codeList.add("\n" + "    /**\n" +
                "     * static method\n" +
                "     **/");
        codeList.add(t + "public static " + table.tableNameJava + "Data of() {");
//        codeList.add(t + t + "return new " + table.tableNameJava + "Data();");
        codeList.add(t + t + "return " + table.tableNameJava + "Data.builder().build();");
        codeList.add(t + "}");
    }

    private void importData() {
        for (Table t : table.otherTable) {
            // import com.platform.db.admin.customer.CustomerData;
            codeList.add(String.format("import %sData;", (Config.getPathByType(DB).packageName + "." + t.tableNameJavaParam.toLowerCase() + "." + t.tableNameJava)));
        }
        for (PageListParam p : table.relateTable) {
            // import com.platform.db.admin.customer.CustomerData;
            codeList.add(String.format("import %sData;", (Config.getPathByType(DB).packageName + "." + p.otherTable.tableNameJavaParam.toLowerCase() + "." + p.otherTable.tableNameJava)));
        }
    }

    private void importDataList() {
        if (isNotEmpty(table.relateTable)) {
            boolean more = false;
            List<PageListParam> pageListParams = table.relateTable;
            for (PageListParam param : pageListParams) {
                if (param.more) {
                    more = true;
                }
            }
            if (more) {
                codeList.add("import java.util.List;\n");
            }
        }
    }

}
