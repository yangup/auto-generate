package com.platform.auto.jdbc;

import com.platform.auto.config.Config;
import com.platform.auto.entity.ConfigInfoEntity;
import com.platform.auto.jdbc.base.BaseCreator;
import com.platform.auto.jdbc.model.ColumnInfo;
import com.platform.auto.jdbc.model.FindData;
import com.platform.auto.jdbc.model.TypeToJavaData;
import com.platform.auto.sys.annotation.AnnotationUtil;
import com.platform.auto.sys.order.Order;
import com.platform.auto.util.AutoUtil;
import com.platform.auto.util.FileUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;

import static com.platform.auto.util.CharUtil.*;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

/**
 * yangpu.jdbc.mysql.ModelCreate.java<br>
 * Description : <br>
 *
 * @author YangPu
 * @createTime 2021-04-20
 */
public class FrontCreate extends BaseCreator {

    public FrontCreate(BaseCreator baseCreator) {
        super(baseCreator);
    }

    @Override
    public void create() {
        List<String> codeTempList = this.copyCodeListAndClear();
        for (String line : codeTempList) {
            if (Order.check(line, Order.startElTableColumn)) {
                createElTableColumn();
            } else if (Order.check(line, Order.startElFormItem)) {
                startElFormItem();
            } else if (Order.check(line, Order.startTemp)) {
                startTemp();
            } else if (Order.check(line, Order.startRules)) {
                startRules();
            } else if (Order.check(line, Order.filterAll)) {
                filterAll(getLeftWhitespace(line, Order.filterAll));
            } else if (Order.check(line, Order.queryParam)) {
                // todo : 查询出这个命令距离左边的位置
                String wp = getLeftWhitespace(line, Order.queryParam);
                List<String> sb = new ArrayList<>();
                for (FindData f : table.findData) {
                    if (!Arrays.asList("page", "limit", "startTime", "endTime").contains(f.name)) {
                        sb.add(f.name);
                    }
                }
                line = line.replace(Order.getOrder(Order.queryParam), String.join(": undefined,\n" + wp, sb) + ": undefined,");
                this.codeList.add(line);
            } else {
                this.codeList.add(line);
            }
        }
        // TODO: 生成其他代码
        try {
            generateOtherCode();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 生成其他代码
     */
    public void generateOtherCode() throws Exception {
        // TODO: 随便添加一下 // api/api.js
        // TODO: 随便添加一下 // router/index.js
        // TODO: 随便添加一下 // utils/constant.js
        if (info.path == null) {
            return;
        }
        ConfigInfoEntity useful = Config.getConfig().info.stream().filter(i -> isTrue(i.isUseful)).findFirst().orElse(null);
        if (useful == null) {
            return;
        }
        if (!isTrue(info.isFront)) {
            return;
        }
        List<String> usefulCodeList = AutoUtil.fileToList(FileUtil.createFile(table, useful));
        final String todo_auto_generate = "todo : auto-generate";

        File apiFile = FileUtil.createFile(info.path.apiFile, info.path.absoluteApiFile);
        List<String> apiList = AutoUtil.fileToList(apiFile);
        int apiIndex = AutoUtil.strInListIndex(apiList, todo_auto_generate) + 1;

        File constantFile = FileUtil.createFile(info.path.constantFile, info.path.absoluteConstantFile);
        List<String> constantFileList = AutoUtil.fileToList(constantFile);
        int constantIndex = AutoUtil.strInListIndex(constantFileList, todo_auto_generate) + 1;

        File routerFile = FileUtil.createFile(info.path.routerFile, info.path.absoluteRouterFile);
        List<String> routerList = AutoUtil.fileToList(routerFile);
        int routerIndex = AutoUtil.strInListIndex(routerList, todo_auto_generate) + 1;

        List<String> apiListNew = AutoUtil.subListAndTrim(usefulCodeList, "auto_generate_api_start", "auto_generate_api_end");
        int start = 0;
        for (int i = 0; i < apiListNew.size(); i++) {
            String line = apiListNew.get(i);
            if (isBlank(line)) {
                List<String> tempApiList = apiListNew.subList(start, i);
                if (!String.join("", apiList).contains(String.join("", tempApiList))) {
                    apiList.addAll(apiIndex, tempApiList);
                }
                start = i + 1;
            }
        }
        AutoUtil.listToFile(apiFile, apiList);

        List<String> routerListNew = AutoUtil.subListAndTrim(usefulCodeList, "auto_generate_router_start", "auto_generate_router_end");
        if (!String.join("", routerList).contains(String.join("", routerListNew))) {
            routerList.addAll(routerIndex, routerListNew);
        }
        AutoUtil.listToFile(routerFile, routerList);

        List<String> constantListNew = AutoUtil.subListAndTrim(usefulCodeList, "auto_generate_constant_start", "auto_generate_constant_end");
        if (!String.join("", constantFileList).contains(String.join("", constantListNew))) {
            constantFileList.addAll(constantIndex, constantListNew);
        }
        AutoUtil.listToFile(constantFile, constantFileList);
    }

    private void createElTableColumn() {
        int countNote = 0;
        for (ColumnInfo columninfo : table.columnInfos) {
            String columnNameJava = columninfo.columnNameJava;
            String columnName = columninfo.columnName;
            String columnComment = columninfo.columnComment;
            if (isID(columnName)
                    || isUpdateTime(columnName)
                    || columninfo.isText
                    || StringUtils.endsWithIgnoreCase(columnName, "_id")) {
                continue;
            }
            String oneLine = "<el-table-column label=\"" + columnComment + "\" align=\"left\" min-width=\"100\" prop=\"" + columnNameJava + "\"/>";
            String prefix = "";
            String suffix = "";
            // TODO: 是筛选框
            if (isNotEmpty(columninfo.select)) {
                // TODO: 这个变量的名称
                prefix = columninfo.constantName + "[";
                suffix = "]";
                oneLine = "" +
                        "        <el-table-column label=\"" + columnComment + "\" align=\"center\" min-width=\"100\">\n" +
                        "          <template v-slot=\"{row}\">\n" +
                        "            <span>{{ " + prefix + "row." + columnNameJava + suffix + " }}</span>\n" +
                        "          </template>\n" +
                        "        </el-table-column>";
            } else {
                prefix = "        ";
                if (countNote > 5) {
                    oneLine = "<!--" + oneLine + "-->";
                }
                oneLine = prefix + oneLine;
            }
            countNote++;
            codeList.add(oneLine);
        }
    }

    private void startElFormItem() {
        for (ColumnInfo columninfo : table.columnInfos) {
            String type = columninfo.dataTypeJava;
            String columnNameJava = columninfo.columnNameJava;
            if (StringUtils.isBlank(columninfo.columnComment)) {
                columninfo.columnComment = columninfo.columnNameJava;
            }
            if (isIdCreateTimeUpdateTime(columninfo.columnNameJava)) {
                continue;
            }
            String input = "<el-input v-model=\"temp." + columnNameJava + "\"/>\n";
            long max = 0;
            if (TypeToJavaData.isInt(columninfo.dataTypeJava)) {
                // Integer
                //<el-input-number v-model="temp.route" :min="1" :max="100" :step="0.1" show-word-limit clearable/>
                max = columninfo.numericPrecisionInt;
                input = "<el-input-number v-model=\"temp." + columnNameJava + "\" :min=\"0\" :max=\"" + AnnotationUtil.getNumberByLength(max) + "\" :step=\"1\"/>\n";
            } else if (TypeToJavaData.isBigDecimal(columninfo.dataTypeJava)) {
                // BigDecimal
                int numericPrecision = columninfo.numericPrecisionInt;
                int numericScale = columninfo.numericScaleInt;
                input = "<el-input-number v-model=\"temp." + columnNameJava + "\" :min=\"0\" :max=\"" +
                        AnnotationUtil.getNumberByLength(numericPrecision - numericScale) + "." + AnnotationUtil.getNumberByLength(numericScale) +
                        "\" :step=\"0.1\"/>\n";
            } else if (TypeToJavaData.isString(columninfo.dataTypeJava)) {
                // String
                max = Long.parseLong(columninfo.characterMaximumLength);
                if (max >= 256) {
                    input = "<el-input v-model=\"temp." + columnNameJava + "\" maxlength=\"" + max + "\" type=\"textarea\" :rows=\"3\" show-word-limit clearable/>\n";
                } else if (max > 64) {
                    input = "<el-input v-model=\"temp." + columnNameJava + "\" maxlength=\"" + max + "\" type=\"textarea\" :rows=\"2\" show-word-limit clearable/>\n";
                } else {
                    input = "<el-input v-model=\"temp." + columnNameJava + "\" maxlength=\"" + max + "\" type=\"text\" show-word-limit clearable/>\n";
                }
                // TODO: 选择框
                if (isNotEmpty(columninfo.select)) {
                    input = getInputSelect(columninfo);
                }
                // TODO: 是 富文本 编辑框
                if (columninfo.isText) {
                    input = "<text-editor v-model=\"temp." + columnNameJava + "\"/>\n";
                }
                if (columninfo.isOtherId) {
//                <vue-select v-model="temp.roles" multiple option-key="id" option-label="roleName" api-fun="roleAll"/>
                    String otherTableName = columninfo.columnNameJava;
                    otherTableName = otherTableName.substring(0, otherTableName.length() - 2);
                    if (isNotEmpty(columninfo.otherTable)) {
                        otherTableName = columninfo.otherTable.tableNameJavaParam;
                    } else if (isNotEmpty(columninfo.otherTableName)) {
                        otherTableName = firstToLowercase(firstToUppercase(toJava(removePrefix(columninfo.otherTableName))));
                    }
                    String optionLabel = "id";
                    if (isNotEmpty(columninfo.otherTable) && isNotEmpty(columninfo.otherTable.firstNoId)) {
                        optionLabel = columninfo.otherTable.firstNoId.columnNameJava;
                    }
                    input = "<vue-select v-model=\"temp." + columnNameJava + "\" option-key=\"id\" option-label=\"" + optionLabel + "\" api-fun=\"" + otherTableName + "All\"/>\n";
                }


            } else if (TypeToJavaData.isDateTime(columninfo.dataTypeJava)) {
                // datetime
                input = "<el-date-picker v-model=\"temp." + columnNameJava + "\" value-format=\"yyyy-MM-dd HH:mm:ss\" type=\"datetime\" placeholder=\"选择日期时间\"/>\n";
            } else if (TypeToJavaData.isDate(columninfo.dataTypeJava)) {
                // date
                input = "<el-date-picker v-model=\"temp." + columnNameJava + "\" value-format=\"yyyy-MM-dd\" type=\"date\" placeholder=\"选择日期\"/>\n";
            } else if (TypeToJavaData.isTime(columninfo.dataTypeJava)) {
                // time
                // <el-time-picker v-model="value1" :picker-options="{selectableRange: '18:30:00 - 20:30:00'}" placeholder="任意时间点"></el-time-picker>
                input = "<el-time-picker v-model=\"temp." + columnNameJava + "\" value-format=\"HH:mm:ss\" placeholder=\"选择时间\"/>\n";
            }
            codeList.add("" +
                    "        <el-form-item label=\"" + columninfo.columnComment + "\" prop=\"" + columninfo.columnNameJava + "\">\n" +
                    "          " + input +
                    "        </el-form-item>");
        }
    }

    /**
     * 在新增, 编辑中, 处理该属性
     **/
    private String getInputSelect(ColumnInfo columninfo) {
        String input = "";
        String multiple = "";
        String columnNameJava = columninfo.columnNameJava;
        String constantName = columninfo.constantName;
        int size = columninfo.select.size();
        boolean isSelectMore = columninfo.isSelectMore;
        if (isSelectMore) {
            multiple = " multiple";
        }
        if (size == 1) {
            input = "";
            return input;
        }
        // TODO: 两个选项, 并且是单选
        if (size == 2 && !isSelectMore) {
            String k0 = columninfo.select.get(0).key;
            String k1 = columninfo.select.get(1).key;
            // TODO: 是开关类的
            if ((isEqual(k0, "ON") && isEqual(k1, "OFF"))
                    || (isEqual(k0, "OFF") && isEqual(k1, "ON"))) {
                // TODO: 使用 switch 标签
                input = "<vue-switch v-model=\"temp." + columnNameJava + "\" :object=\"" + constantName + "\"/>";
            } else {
                input = "<vue-radio v-model=\"temp." + columnNameJava + "\" :object=\"" + constantName + "\"/>";
            }
            return input + "\n";
        }
        // TODO: 少于 10 个
        if (size <= 10) {
            if (isSelectMore) {
                input = "<vue-checkbox v-model=\"temp." + columnNameJava + "\" :object=\"" + constantName + "\"/>";
            } else {
                input = "<vue-radio v-model=\"temp." + columnNameJava + "\" :object=\"" + constantName + "\"/>";
            }
            return input + "\n";
        }
        // TODO: 多余 10 个
        input = "<vue-select v-model=\"temp." + columnNameJava + "\"" + multiple + " :object=\"" + constantName + "\"/>";
        return input + "\n";
    }

    private void startTemp() {
        String temp = null;
        List<String[]> fields = new ArrayList<>();
        for (ColumnInfo columninfo : table.columnInfos) {
            String type = columninfo.dataTypeJava;
            String field = columninfo.columnNameJava;

            String[] strings = new String[2];
            temp = field;
            strings[0] = temp;// 代码部分
            temp = columninfo.columnComment;
            strings[1] = temp;// 注释部分
            if (StringUtils.isBlank(strings[1])) {
                strings[1] = strings[0];
            }
//            if (isNotIdCreateTimeUpdateTime(strings[0])) {
            fields.add(strings);
//            }
        }
        for (String[] strings : fields) {
            codeList.add("        " + strings[0] + ": undefined,");
        }
    }

    private void startRules() {
        String temp = null;
        List<String[]> fields = new ArrayList<>();
        for (ColumnInfo columninfo : table.columnInfos) {
            String type = columninfo.dataTypeJava;
            String field = columninfo.columnNameJava;

            String[] strings = new String[2];
            temp = field;
            strings[0] = temp;// 代码部分
            temp = columninfo.columnComment;
            strings[1] = temp;// 注释部分
            if (StringUtils.isBlank(strings[1])) {
                strings[1] = strings[0];
            }
            if (isNotIdCreateTimeUpdateTime(strings[0])) {
                fields.add(strings);
            }
        }
        for (String[] strings : fields) {
            codeList.add("        " + strings[0] + ": [{ required: true, message: '" + strings[1] + " 不能为空', trigger: 'blur' }],");
        }
    }

    /**
     * 添加字段的过滤器
     **/
    private void filterAll(String wp) {
        // todo : 未注释的数量
        int countNote = 0;
        for (ColumnInfo columninfo : table.columnInfos) {
            String dataTypeJava = columninfo.dataTypeJava;
            String columnNameJava = columninfo.columnNameJava;
            if (StringUtils.isBlank(columninfo.columnComment)) {
                columninfo.columnComment = columninfo.columnNameJava;
            }
            if (isCreateTime(columnNameJava)) {
                continue;
            }
            if (columninfo.isText) {
                continue;
            }
            String columnComment = columninfo.columnComment;
            String input = "<filter-input v-model=\"query." + columnNameJava + "\" label=\"" + columnComment + "\"/>";
            int max = 0;
            if (TypeToJavaData.isInt(dataTypeJava) || TypeToJavaData.isLong(dataTypeJava)) {
                // Integer
                //<el-input-number v-model="temp.route" :min="1" :max="100" :step="0.1" show-word-limit clearable/>
                max = columninfo.numericPrecisionInt;
                input = "<filter-number-range :min.sync=\"query." + columnNameJava + "Min\" :max.sync=\"query." + columnNameJava + "Max\" label=\"" + columnComment + "\"/>";
            } else if (TypeToJavaData.isBigDecimal(columninfo.dataTypeJava)) {
                // BigDecimal
//                int numericPrecision = columninfo.numericPrecisionInt;
//                int numericScale = columninfo.numericScaleInt;
                input = "<filter-number-range :min.sync=\"query." + columnNameJava + "Min\" :max.sync=\"query." + columnNameJava + "Max\" :step=\"0.1\" :precision=\"2\" label=\"" + columnComment + "\"/>";
            } else if (TypeToJavaData.isString(columninfo.dataTypeJava)) {
                // String
                // TODO: 选择框
                if (isNotEmpty(columninfo.select)) {
                    input = "<filter-select v-model=\"query." + columnNameJava + "\" :object=\"" + columninfo.constantName + "\" label=\"" + columnComment + "\"/>";
                }
                // TODO: 是 富文本 编辑框
                if (columninfo.isText) {
                    input = "";
                }
            } else if (TypeToJavaData.isDateTime(columninfo.dataTypeJava) || TypeToJavaData.isDate(columninfo.dataTypeJava) || TypeToJavaData.isTime(columninfo.dataTypeJava)) {
                // date
                input = "<filter-date-range :start-time.sync=\"query." + columnNameJava + "From\" :end-time.sync=\"query." + columnNameJava + "To\" label=\"" + columnComment + "\"/>";
            }

            if (isIdCreateTimeUpdateTime(columnNameJava)
                    || countNote > 0
                    || isID(columninfo.columnName)
                    || columninfo.columnName.toLowerCase().endsWith("_id")) {
                input = "<!--" + input + "-->";
                countNote--;
            }
            if (isNotEmpty(input)) {
                codeList.add(wp + input);
                countNote++;
            }
        }

    }

}
