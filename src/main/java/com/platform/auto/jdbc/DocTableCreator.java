package com.platform.auto.jdbc;

import com.platform.auto.config.ConfigEntity;
import com.platform.auto.jdbc.base.BaseCreator;
import com.platform.auto.jdbc.model.ColumnInfo;
import com.platform.auto.jdbc.model.Table;
import com.platform.auto.sys.order.Order;
import com.platform.auto.util.AutoUtil;
import com.platform.auto.util.FileUtil;

import java.util.ArrayList;
import java.util.List;

import static com.platform.auto.util.CharUtil.*;

/**
 * <p>
 * yangpu.jdbc.mysql.DocCreate.java
 * </p>
 * <p>
 * description :
 * </p>
 *
 * @author YangPu
 * @createTime 2016年7月21日 下午3:50:33
 */
public class DocTableCreator extends BaseCreator {
    /**
     * 加载模板
     *
     * @param table
     */
    public DocTableCreator(Table table, ConfigEntity.Info info) throws Exception {
        this(table, info, false);
    }

    public DocTableCreator(Table table, ConfigEntity.Info info, boolean isList) throws Exception {
        super(info, table);
        List<String> templateList = this.copyCodeListAndClear();
        for (String line : templateList) {
            if (Order.check(line, Order.tableJson)) {
                createJson();
            } else if (Order.check(line, Order.jsonKeyValueComment)) {
                // todo : 查询出这个命令距离左边的位置
                int leftNum = line.substring(0, line.indexOf(Order.getOrder(Order.jsonKeyValueComment))).length();
                createJsonKeyValueComment(getWhitespace(leftNum));
            } else {
                codeList.add(line);
            }
        }
        if (!isList) {
            AutoUtil.newCodeToFile(codeList, FileUtil.createFile(table, info, info.fileNameSuffix), "- 最后修改时间 :");
        }
    }

    /**
     * 创建json数据
     **/
    private void createJson() {
        codeList.add("{");
        List<String> fieldList = new ArrayList<>();
        for (int i = 0; i < this.table.columnInfos.size(); i++) {
            ColumnInfo columninfo = this.table.columnInfos.get(i);
            String field = columninfo.columnNameJava;
            if (isIdCreateTimeUpdateTime(field)) {
                continue;
            }
            fieldList.add(field);
        }
        for (int i = 0; i < fieldList.size(); i++) {
            String field = fieldList.get(i);

            String dd = "";
            if (i != fieldList.size() - 1) {
                dd = d;
            }
            codeList.add(t + s + field + s + m + " " + s + field + s + dd);
        }
        codeList.add("}");
    }

    private void createJsonKeyValueComment(String w) throws Exception {
        // TODO: json 的 key, value
        List<String> jsonList = new ArrayList<>();
        // TODO: comment
        List<String> commentList = new ArrayList<>();
        int max = 0;
        // TODO: 计算 距离最左边的长度
        for (int i = 0; i < this.table.columnInfos.size(); i++) {
            ColumnInfo c = this.table.columnInfos.get(i);
            String field = c.columnNameJava;
            if (isUpdateTime(field)) {
                continue;
            }
            //  "minVersionDevice": "Red MI 9 PROC410474D47624185B28C14209E305BDA"
            String j = "\"" + field + "\": \"" + field + "\"";
            max = Math.max(max, j.length());
            jsonList.add(j);
            commentList.add(c.columnComment);
        }

        for (int i = 0; i < jsonList.size(); i++) {
            String dd = "";
            int add = 0;
            if (i != jsonList.size() - 1) {
                dd = ",";
                add++;
            }
            codeList.add(w + jsonList.get(i) + dd + getWhitespace(max - jsonList.get(i).length() - add + 5) + "// " + commentList.get(i));
        }
    }
}
