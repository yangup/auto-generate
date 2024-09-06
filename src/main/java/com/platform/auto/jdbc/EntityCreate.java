package com.platform.auto.jdbc;

import com.platform.auto.config.Config;
import com.platform.auto.jdbc.base.BaseCreate;
import com.platform.auto.jdbc.model.ColumnInfo;
import com.platform.auto.jdbc.model.Table;
import com.platform.auto.sys.order.Order;
import com.platform.auto.util.AutoUtil;
import com.platform.auto.util.FileUtil;

import java.io.File;
import java.util.List;

import static com.platform.auto.util.CharUtil.*;

/**
 * yangpu.jdbc.mysql.ModelCreate.java<br>
 * Description : <br>
 *
 * @author YangPu
 * @createTime 2016年7月21日 下午3:50:33
 */
public class EntityCreate extends BaseCreate {

    private List<ColumnInfo> list;

    public EntityCreate(Table table) throws Exception {
        new EntityCreate(table, false);
    }

    /**
     * @param isList : 是否只把生成的数据, 放入到 list 中, 不做其他的处理
     */
    public EntityCreate(Table table, boolean isList) throws Exception {
        super(Config.getConfig().template.entity, table);
        if (isEmpty(Config.getConfig().template.entity)) {
            return;
        }
        List<String> templateList = this.copyCodeListAndClear();
        this.list = this.table.columnInfos;
        for (String line : templateList) {
            if (Order.check(line, Order.startField)) {
                createField();
            } else {
                codeList.add(line);
            }
        }
        if (!isList) {
            AutoUtil.newCodeToFile(codeList, FileUtil.createFileDB(table.tableNameJava + "Entity.java", table.javaFilePath));
        }
    }

    private String getStaticString(String name, String value) {
        return String.format("public static final String %s = \"%s\";", name, value);
    }

    private void createField() {
        for (int i = 0; i < list.size(); i++) {
            ColumnInfo columninfo = list.get(i);
            String type = columninfo.dataTypeJava;
            String field = columninfo.columnNameJava;
            codeList.add(t + "// TODO: " + columninfo.columnCommentRaw);
            if (isUpdateTime(field)) {
//                codeList.add(t + "@JsonIgnore");
            }
            codeList.add(t + "public" + w + type + w + field + ";");
        }

//        String prefix = n;
//        for (int i = 0; i < list.size(); i++) {
//            ColumnInfo c = list.get(i);
//            if (isIdCreateTimeUpdateTime(c.columnNameJava)) {
//                continue;
//            }
//            for (FindData findData : c.findData) {
//                codeList.add(prefix + t + getStaticString(findData.staticName, findData.name));
//            }
//            prefix = "";
//        }
        codeList.add(n + "    /**\n" +
                "     * static method\n" +
                "     **/");
        codeList.add(t + "public static " + table.tableNameJava + "Entity of() {");
//        codeList.add(t + t + "return new " + table.tableNameJava + "Entity();");
        codeList.add(t + t + "return " + table.tableNameJava + "Entity.builder().build();");
        codeList.add(t + "}");

    }

}
