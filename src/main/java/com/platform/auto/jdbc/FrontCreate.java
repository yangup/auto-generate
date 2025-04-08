package com.platform.auto.jdbc;

import com.platform.auto.jdbc.base.BaseCreator;
import com.platform.auto.jdbc.model.FindData;
import com.platform.auto.jdbc.model.TypeToJavaData;
import com.platform.auto.sys.annotation.AnnotationUtil;
import com.platform.auto.sys.order.Order;
import com.platform.auto.util.AutoUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * yangpu.jdbc.mysql.ModelCreate.java<br>
 * Description : <br>
 *
 * @author YangPu
 * @createTime 2021-04-20
 */
public class FrontCreate extends BaseCreator {

    /**
     * 加载模板
     *
     * @param table
     */
    public FrontCreate(Table table) throws Exception {
        new FrontCreate(table, false);
    }

    public FrontCreate(Table table, boolean isList) throws Exception {
        this.table = table;
        this.list = table.columnInfos;
        this.importCodeList = new HashSet<>();
        this.componentsCodeList = new HashSet<>();
        this.returnCodeList = new HashSet<>();
        UsefulCreate create = new UsefulCreate(table, true);
        this.codeUsefulList = create.codeList;
        List<String> templateList = AutoUtil.readTemplate(Constant.front);
        // TODO: 模板中的数据, 使用数据库中的字段替换
        templateList = AutoUtil.readWrite(templateList, table);
        codeList = new ArrayList<>(templateList.size() * 2);

        for (String line : templateList) {
            boolean run = false;
            if (Order.check(line, Order.startElTableColumn)) {
                createElTableColumn();
            } else if (Order.check(line, Order.startElFormItem)) {
                startElFormItem();
            } else if (Order.check(line, Order.startTemp)) {
                startTemp();
            } else if (Order.check(line, Order.startRules)) {
                startRules();
            } else if (Order.check(line, Order.filterAll)) {
                filterAll(getWhitespace(line.substring(0, line.indexOf(Order.getOrder(Order.filterAll))).length()));
            } else {
                run = true;
            }
            if (!run) {
                continue;
            }
            if (Order.check(line, Order.queryParam)) {
                // todo : 查询出这个命令距离左边的位置
                int leftNum = line.substring(0, line.indexOf(Order.getOrder(Order.queryParam))).length();
                String wp = getWhitespace(leftNum);
                List<String> sb = new ArrayList<>();
                for (FindData f : table.findData) {
                    if (!Arrays.asList("page", "limit", "startTime", "endTime").contains(f.name)) {
                        sb.add(f.name);
                    }
                }
                line = line.replace(Order.getOrder(Order.queryParam), String.join(": undefined,\n" + wp, sb) + ": undefined,");
            }
            codeList.add(line);
        }
        // TODO: constant
        List<String> constantList = list.stream().map(l -> l.constantName).collect(Collectors.toList());
        constantList.removeAll(Collections.singleton(null));
        if (isNotEmpty(constantList)) {
            returnCodeList.addAll(constantList);
            // todo :
            importCodeList.add(String.format("import { %s } from '@/utils/constant'", String.join(", ", constantList)));
        }
        // TODO: 富文本编辑器
        if (list.stream().anyMatch(o -> o.isText)) {
//            insertLocation(IMPORT_LOCATION, "import { EDITOR_CONFIG } from '@/utils/request'", "import ClassicEditor from '@/utils/classic-editor'");
//            insertLocation(RETURN_LOCATION, "editor: ClassicEditor,", "editorConfig: EDITOR_CONFIG,");
        }

        // TODO: 将需要导入的包的数据, 加入到代码中
        insertLocation(IMPORT_LOCATION, importCodeList);
        insertLocation(COMPONENT_LOCATION, componentsCodeList);
        insertLocation(RETURN_LOCATION, returnCodeList);

        if (!isList) {
            if (isNotEmpty(Constant.path_front)) {
                // TODO: 随便添加一下 // api/api.js
                // TODO: 随便添加一下 // router/index.js
                File api = new File(Constant.path_front + "src\\api\\api.js");
                File route = new File(Constant.path_front + "src\\router\\index.js");
                File constant = new File(Constant.path_front + "src\\utils\\constant.js");
                List<String> apiList = AutoUtil.fileToList(api);
                List<String> apiList1 = new ArrayList<>(apiList);
                List<String> constantFileList = AutoUtil.fileToList(constant);
                List<String> constantFileList1 = new ArrayList<>(constantFileList);
                List<String> routerList = AutoUtil.fileToList(route);
                List<String> routerList1 = new ArrayList<>(routerList);
                String tableNameJavaParam = table.tableNameJavaParam;

                AutoUtil.checkColumn(apiList, this.codeUsefulList, tableNameJavaParam + "Delete(", 1, 4,
                        "class Api {");

                AutoUtil.checkColumn(apiList, this.codeUsefulList, tableNameJavaParam + "AddUpdate(", 1, 4,
                        "class Api {");

                AutoUtil.checkColumn(apiList, this.codeUsefulList, tableNameJavaParam + "One(", 1, 4,
                        "class Api {");

                AutoUtil.checkColumn(apiList, this.codeUsefulList, tableNameJavaParam + "All(", 1, 4,
                        "class Api {");

                AutoUtil.checkColumn(apiList, this.codeUsefulList, tableNameJavaParam + "FindAll(", 1, 4,
                        "class Api {");

                AutoUtil.checkColumn(apiList, this.codeUsefulList, tableNameJavaParam + "Find(", 1, 4,
                        "class Api {");

                AutoUtil.checkColumn(routerList, this.codeUsefulList, "path: '/" + table.frontFilePath + "/" + tableNameJavaParam + "',", 1, 4,
                        "// todo : auto-generate");

                for (ColumnInfo c : list) {
                    if (isNotEmpty(c.constantName)) {
                        AutoUtil.checkColumn(constantFileList, this.codeUsefulList, "export const " + c.constantName, 0, 2 + c.select.size(), "// todo : auto-generate");
                    }
                }

                // TODO: constant.js
                if (!StringUtils.equals(String.join("", constantFileList1), String.join("", constantFileList))) {
                    AutoUtil.listToFile(constant, constantFileList);
                } else {
                    log.out("same file", constant.getName());
                }

                // TODO: api.js
                if (!StringUtils.equals(String.join("", apiList1), String.join("", apiList))) {
                    AutoUtil.listToFile(api, apiList);
                } else {
                    log.out("same file", api.getName());
                }

                // TODO: router/index.js
                if (!StringUtils.equals(String.join("", routerList1), String.join("", routerList))) {
                    AutoUtil.listToFile(route, routerList);
                } else {
                    log.out("same file", route.getName());
                }
            }
            AutoUtil.newCodeToFile(codeList,
                    FileUtil.createFileFront(table.frontFilePath + File.separator + table.tableNameJavaParam + ".vue"));
        }

    }

    /**
     * 在指定位置的下一行, 插入代码
     **/
    private void insertLocation(String indexStr, Set<String> insertList) {
        if (isEmpty(insertList)) {
            return;
        }
        for (String s : insertList) {
            insertLocation(indexStr, s);
        }
    }

    /**
     * 在指定位置的下一行, 插入代码
     **/
    private void insertLocation(String indexStr, String... ss) {
        if (isEmpty(ss)) {
            return;
        }
        int importIndex = AutoUtil.listIndex(codeList, indexStr);
        if (importIndex != -1) {
            for (String s : ss) {
                String prefix = "";
                String suffix = "";
                if (COMPONENT_LOCATION.equals(indexStr)) {
                    if (!s.startsWith("    ")) {
                        prefix = "    ";
                    }
                    if (!s.endsWith(",")) {
                        suffix = ",";
                    }
                }
                if (RETURN_LOCATION.equals(indexStr)) {
                    if (!s.startsWith("      ")) {
                        prefix = "      ";
                    }
                    if (!s.endsWith(",")) {
                        suffix = ",";
                    }
                }
                codeList.add(importIndex + 1, prefix + s + suffix);
            }
        }
    }

    private void createElTableColumn() {
        int countNote = 0;
        for (int i = 0; i < list.size(); i++) {
            ColumnInfo columninfo = list.get(i);
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
        for (int i = 0; i < list.size(); i++) {
            ColumnInfo columninfo = list.get(i);
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
        for (int i = 0; i < list.size(); i++) {
            ColumnInfo columninfo = list.get(i);
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
        for (int i = 0; i < list.size(); i++) {
            ColumnInfo columninfo = list.get(i);
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
        for (int i = 0; i < list.size(); i++) {
            ColumnInfo columninfo = list.get(i);
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

    /**
     *
     **/
    public static void checkColumn(Table table) throws Exception {
        File file = FileUtil.getFile(table, "");
        if (file == null) {
            log.out("front is not exist");
            return;
        }
        FrontCreate create = new FrontCreate(table, true);
        // TODO: 现在代码中的情况
        List<String> nowList = AutoUtil.fileToList(file);

        AutoUtil.checkColumn(nowList, create.codeList, "<el-table v-loading=\"listLoading\"", "<el-table-column fixed=\"right\" label");
        AutoUtil.checkColumn(nowList, create.codeList, "<el-form ref=\"dataForm\"", "</el-form>");
        AutoUtil.checkColumn(nowList, create.codeList, "data() {", "methods: {");

        // TODO: 新的代码, 放入到文件中
        AutoUtil.listToFile(file, nowList);
    }


}
