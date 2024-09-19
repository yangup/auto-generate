package com.platform.auto.jdbc;

import com.platform.auto.config.Config;
import com.platform.auto.config.ConfigEntity;
import com.platform.auto.jdbc.base.BaseCreator;
import com.platform.auto.jdbc.model.ColumnInfo;
import com.platform.auto.jdbc.model.Table;
import com.platform.auto.sys.order.Order;
import com.platform.auto.util.AutoUtil;
import com.platform.auto.util.FileUtil;

import java.util.List;

import static com.platform.auto.util.CharUtil.*;

/**
 * yangpu.jdbc.mysql.ModelCreate.java<br>
 * Description : <br>
 *
 * @author YangPu
 * @createTime 2016年7月21日 下午3:50:33
 */
public class EntityCreator extends BaseCreator {

    private List<ColumnInfo> list;
    private ConfigEntity.Info info;

    public EntityCreator(Table table, ConfigEntity.Info info) throws Exception {
        new EntityCreator(table, info, false);
    }

    /**
     * @param isList : 是否只把生成的数据, 放入到 list 中, 不做其他的处理
     */
    public EntityCreator(Table table, ConfigEntity.Info info, boolean isList) throws Exception {
        super(info.template, table);
        this.info = info;
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
            AutoUtil.newCodeToFile(codeList, FileUtil.createFile(table, info, ENTITY_JAVA));
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
            String publicMethod = isTrue(info.entityFieldIsPublic) ? "public" : "private";
            codeList.add(t + publicMethod + w + type + w + field + ";");
        }

        if (isTrue(info.entityGenerateStaticMethod)) {
            codeList.add(n + "    /**\n" + "     * static method\n" + "     **/");
            codeList.add(t + "public static " + table.tableNameJava + "Entity of() {");
//        codeList.add(t + t + "return new " + table.tableNameJava + "Entity();");
            codeList.add(t + t + "return " + table.tableNameJava + "Entity.builder().build();");
            codeList.add(t + "}");
        }
    }

}
