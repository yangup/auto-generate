package com.platform.auto.jdbc;

import com.platform.auto.jdbc.base.BaseCreator;
import com.platform.auto.jdbc.model.ColumnInfo;
import com.platform.auto.jdbc.model.FindData;
import com.platform.auto.jdbc.model.QueryMapFindParam;
import com.platform.auto.jdbc.model.Table;
import com.platform.auto.sys.order.Order;
import com.platform.auto.util.FileUtil;

import java.util.List;

import static com.platform.auto.util.CharUtil.*;

/**
 *
 */
public class PostManTableCreator extends BaseCreator {

    public PostManTableCreator(BaseCreator baseCreator) {
        super(baseCreator);
    }

    @Override
    public void create() {
        List<String> templateList = this.copyCodeListAndClear();
        for (String line : templateList) {
            line = Order.change(line, Order.name, FileUtil.getTableNameJavaLower(table));
            if (Order.check(line, Order.jsonDemo)) {
                line = line.replace(Order.getOrder(Order.jsonDemo), createJson());
            }
            if (Order.check(line, Order.queryFindParam)) {
                String wp = getWhitespace(line.substring(0, line.indexOf(Order.getOrder(Order.queryFindParam))).length());
                line = line.replace(Order.getOrder(Order.queryFindParam), queryFindParam(wp));
            }
            codeList.add(line);
        }
    }

    //    {
//        "key": "id",
//        "value": "id",
//        "disabled": true
//    },
    private String queryFindParam(String wp) {
        StringBuffer sb = new StringBuffer();
        for (QueryMapFindParam f : Table.pageBoundsFindParamStatic) {
            appendQueryMapParamOne(sb, wp, f.key, f.value, f.des, !f.defaultToUrl);
        }
        for (QueryMapFindParam f : Table.idFindParamStatic) {
            appendQueryMapParamOne(sb, wp, f.key, f.value, f.des, !f.defaultToUrl);
        }

        for (int i = 0; i < this.table.columnInfos.size(); i++) {
            ColumnInfo c = this.table.columnInfos.get(i);
            for (FindData f : c.findData) {
                if (Table.noneMatchParamStatic(f.name)) {
                    String des = f.comment;
                    if (isNotEmpty(c.select)) {
                        StringBuilder sbTemp = new StringBuilder();
                        c.select.forEach(one -> {
                            sbTemp.append(String.format("%s:%s;", one.key, one.value));
                        });
                        des += ";" + sbTemp.toString();
                    } else if (c.isConstant) {
                        des += ";" + "常量";
                    }
                    if (isNotEmpty(c.typeDocNote)) {
                        des += ";" + c.typeDocNote;
                    }
                    appendQueryMapParamOne(sb, wp, f.name, "", des, true);
                }
            }
        }
        for (QueryMapFindParam f : Table.queryMapFindParamStatic) {
            appendQueryMapParamOne(sb, wp, f.key, f.value, f.des, !f.defaultToUrl);
        }
        for (QueryMapFindParam f : Table.createUpdateTimeFindParamStatic) {
            appendQueryMapParamOne(sb, wp, f.key, f.value, f.des, !f.defaultToUrl);
        }

        return sb.toString();
    }

    public static void appendQueryMapParamOne(StringBuffer sb, String wp, String key, String value, String description, boolean disabled) {
        sb.append(sb.length() > 0 ? ",\n" + wp : "");
        sb.append(String.format("{\n" +
                wp + "    \"key\": \"%s\",\n" +
                wp + "    \"value\": \"%s\",\n" +
                wp + "    \"description\": \"%s\",\n" +
                wp + "    \"disabled\": %s\n" +
                wp + "}", key, value, description, disabled));
    }

    private String createJson() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.table.columnInfos.size(); i++) {
            ColumnInfo columninfo = this.table.columnInfos.get(i);
            String field = columninfo.columnNameJava;
            if (isCreateTimeUpdateTime(field)) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append("\\r\\n    \\\"" + field + "\\\": \\\"" + field + "\\\"");
        }
        sb.append("\\r\\n");
        return sb.toString();
    }

}
