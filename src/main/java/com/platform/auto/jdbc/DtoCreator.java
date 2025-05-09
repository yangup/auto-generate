package com.platform.auto.jdbc;

import com.platform.auto.jdbc.base.BaseCreator;
import com.platform.auto.jdbc.model.ColumnInfo;
import com.platform.auto.jdbc.model.TypeToJavaData;
import com.platform.auto.sys.annotation.AnnotationUtil;
import com.platform.auto.sys.annotation.ParamValidationAnnotation;
import com.platform.auto.sys.order.Order;
import com.platform.auto.util.CharUtil;

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

    public DtoCreator(BaseCreator baseCreator) {
        super(baseCreator);
    }

    @Override
    public void create() {
        List<String> templateList = this.copyCodeListAndClear();
        for (String line : templateList) {
            if (line.contains(Order.getOrder(Order.startFieldDto))) {
                createField();
            } else {
                codeList.add(line);
            }
        }
    }

    private void createField() {
        for (int i = 0; i < this.table.columnInfos.size(); i++) {
            ColumnInfo columninfo = this.table.columnInfos.get(i);
            final String name = columninfo.columnNameJava;
            // 注释部分
            codeList.add(t + "// todo: " + columninfo.columnCommentRaw);
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
                long maxTemp = 0L;
                try {
                    maxTemp = Long.parseLong(AnnotationUtil.getNumberByLength(columninfo.numericPrecisionInt));
                } catch (Exception e) {
                    maxTemp = Long.MAX_VALUE;
                }
                codeList.add(prefix + ParamValidationAnnotation.MAX.replaceInfo(msg, maxTemp));
            } else if (TypeToJavaData.isBigDecimal(columninfo.dataTypeJava)) {
                // BigDecimal
                String prefix1 = "//" + CharUtil.t;
                codeList.add(prefix1 + ParamValidationAnnotation.DECIMAL_MIN.replaceInfo(msg, 0));
                int numericPrecision = columninfo.numericPrecisionInt;
                int numericScale = columninfo.numericScaleInt;
                codeList.add(prefix1 + ParamValidationAnnotation.DECIMAL_MAX.replaceInfo(msg,
                        get9(numericPrecision - numericScale) + "." + get9(numericScale)));
                codeList.add(prefix1 + ParamValidationAnnotation.DIGITS.replaceInfo(msg,
                        numericPrecision - numericScale, numericScale));
                codeList.add(prefix + ParamValidationAnnotation.NOT_NULL.replaceInfo(msg));
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

            String needJsonProperty = isTrue(info.showJsonProperty) ? "" : "//";
            codeList.add(needJsonProperty + t + "@JsonProperty(\"" + columninfo.columnName + "\")");

            // 代码部分
            codeList.add(t + (isTrue(info.fieldIsPublic) ? "public" : "private") + " " + columninfo.dataTypeJava + " " + columninfo.columnNameJava + ";\n");
        }


        if (isTrue(info.generateStaticMethod)) {
            codeList.add("    /**\n" +
                    "     * static method\n" +
                    "     **/");
            String dto = getClazzNameSuffix();
            codeList.add(t + "public static " + table.tableNameJava + dto + " of() {");
            codeList.add(t + t + "return " + table.tableNameJava + dto + ".builder().build();");
            codeList.add(t + "}");
            // convert entity to dto
            codeList.add("");
            codeList.add(t + "public static " + table.tableNameJava + "Dto of(" + table.tableNameJava + "Entity entity) {");
            codeList.add(t + t + "return " + table.tableNameJava + "Dto.builder()");
            for (ColumnInfo c : table.columnInfos) {
                codeList.add(t + t + t + String.format(".%s(entity.%s)", c.columnNameJava, c.columnNameJava));
            }
            codeList.add(t + t + t + ".build();");
            codeList.add(t + "}");
            // convert data to dto
            codeList.add("");
            codeList.add(t + "public static " + table.tableNameJava + "Dto of(" + table.tableNameJava + "Data data) {");
            codeList.add(t + t + "return " + table.tableNameJava + "Dto.builder()");
            for (ColumnInfo c : table.columnInfos) {
                codeList.add(t + t + t + String.format(".%s(data.%s)", c.columnNameJava, c.columnNameJava));
            }
            codeList.add(t + t + t + ".build();");
            codeList.add(t + "}");
        }
    }

}
