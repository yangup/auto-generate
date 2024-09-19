package com.platform.auto.jdbc;

import com.platform.auto.config.Config;
import com.platform.auto.config.ConfigEntity;
import com.platform.auto.jdbc.base.BaseCreator;
import com.platform.auto.jdbc.model.ColumnInfo;
import com.platform.auto.jdbc.model.Table;
import com.platform.auto.jdbc.model.TypeToJavaData;
import com.platform.auto.sys.annotation.AnnotationUtil;
import com.platform.auto.sys.annotation.ParamValidationAnnotation;
import com.platform.auto.sys.order.Order;
import com.platform.auto.util.AutoUtil;
import com.platform.auto.util.CharUtil;
import com.platform.auto.util.FileUtil;

import java.util.List;

import static com.platform.auto.util.CharUtil.*;

/**
 * yangpu.jdbc.mysql.ModelAnnotationCreate.java<br>
 * Description : <br>
 *
 * @author YangPu
 * @createTime 2016年7月21日 下午3:50:33
 */
public class DtoCreator extends BaseCreator {
    /**
     * 加载模板
     *
     * @param table
     */
    public DtoCreator(Table table, ConfigEntity.Info info) throws Exception {
        new DtoCreator(table, info, false);
    }

    public DtoCreator(Table table, ConfigEntity.Info info, boolean isList) throws Exception {
        super(info, table);
        List<String> templateList = this.copyCodeListAndClear();
        for (String line : templateList) {
            if (line.contains(Order.getOrder(Order.startField))) {
                createField();
            } else {
                codeList.add(line);
            }
        }

        if (!isList) {
            AutoUtil.newCodeToFile(codeList, FileUtil.createFile(table, info, DTO_JAVA));
        }

    }

    private void createField() {
        for (int i = 0; i < this.table.columnInfos.size(); i++) {
            ColumnInfo columninfo = this.table.columnInfos.get(i);
            final String name = columninfo.columnNameJava;
            // 注释部分
            codeList.add(CharUtil.t + "/* " + columninfo.columnCommentRaw + " */");
            String note = "";
            // TODO: 2021/11/9 这些都注释掉
            if (isIdCreateTimeUpdateTime(name)
                    || columninfo.isText
                    || columninfo.canNULL
                    || columninfo.isOtherId) {
                note = "//";
            }
            String msg = columninfo.columnNameJava + " is empty or incorrect";
            // 注解部分
            String prefix = note + CharUtil.t;
            if (columninfo.isId || columninfo.isOtherId) {
                codeList.add(prefix + ParamValidationAnnotation.NOT_NULL.replaceInfo(msg));
            } else if (TypeToJavaData.isBoolean(columninfo.dataTypeJava)) {
                // Boolean
                codeList.add(prefix + ParamValidationAnnotation.TRUE.replaceInfo(msg));
            } else if (TypeToJavaData.isInt(columninfo.dataTypeJava)) {
                // Integer
                codeList.add(prefix + ParamValidationAnnotation.MIN.replaceInfo(msg, 0));
                codeList.add(prefix + ParamValidationAnnotation.MAX.replaceInfo(msg,
                        Integer.parseInt(AnnotationUtil.getNumberByLength(columninfo.numericPrecisionInt))
                ));
            } else if (TypeToJavaData.isBigDecimal(columninfo.dataTypeJava)) {
                // BigDecimal
                codeList.add(prefix + ParamValidationAnnotation.DECIMAL_MIN.replaceInfo(msg, 0));
                int numericPrecision = columninfo.numericPrecisionInt;
                int numericScale = columninfo.numericScaleInt;
                codeList.add(prefix + ParamValidationAnnotation.DECIMAL_MAX.replaceInfo(msg,
                        numericPrecision - numericScale, numericScale));
                codeList.add(prefix + ParamValidationAnnotation.DIGITS.replaceInfo(msg,
                        numericPrecision - numericScale, numericScale));
            } else if (TypeToJavaData.isString(columninfo.dataTypeJava)) {
                // String
                codeList.add(prefix + ParamValidationAnnotation.NOT_EMPTY.replaceInfo(msg));
                codeList.add(prefix + ParamValidationAnnotation.LENGTH.replaceInfo(0, columninfo.characterMaximumLength, msg));
            } else if (TypeToJavaData.isDate(columninfo.dataTypeJava)
                    || TypeToJavaData.isTime(columninfo.dataTypeJava)
                    || TypeToJavaData.isDateTime(columninfo.dataTypeJava)) {
                // date , time , datetime
                // 不能为空
                codeList.add(prefix + ParamValidationAnnotation.NOT_NULL.replaceInfo(msg));
            } else {
                // 不能为空
                codeList.add(prefix + ParamValidationAnnotation.NOT_NULL.replaceInfo(msg));
            }

            // 代码部分
            codeList.add(CharUtil.t + "public " + columninfo.dataTypeJava + " " + columninfo.columnNameJava + ";\n");
        }

        codeList.add("    /**\n" +
                "     * static method\n" +
                "     **/");
        codeList.add(t + "public static " + table.tableNameJava + "Dto of() {");
//        codeList.add(t + t + "return new " + table.tableNameJava + "Dto();");
        codeList.add(t + t + "return " + table.tableNameJava + "Dto.builder().build();");
        codeList.add(t + "}");
    }

}
