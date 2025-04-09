package com.platform.auto.jdbc.base;


import com.platform.auto.config.Config;
import com.platform.auto.entity.LocalEntity;
import com.platform.auto.jdbc.model.*;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.platform.auto.util.CharUtil.*;

public class TableFactory {

    private static final Logger logger = AutoLogger.getLogger(TableFactory.class);

    /**
     * 根据数据库返回的 rs 处理成 table
     **/
    public List<Table> obtainTable(final ResultSet rs) throws Exception {
        Set<String> keys = ColumnInfo.FieldSet();
        List<Table> tables = new ArrayList<>();
        List<ColumnInfo> list = new ArrayList<>();
        logger.info("analyze table data -start");
        // todo : 将查出的数据解析到list中
        while (rs.next()) {
            list.add(resultSetToColumn(rs, keys));
        }
        Map<String, List<ColumnInfo>> tableInfoList = list.stream().collect(Collectors.groupingBy(ColumnInfo::getTableName));
        tableInfoList.forEach((tableName, columnInfoList) -> {
            tables.add(initTable(columnInfoList));
            // for log
            logger.info(tableName);
            for (ColumnInfo info : columnInfoList) {
                logger.info(info.columnName);
            }
        });

        return tables;
    }

    public List<LocalEntity.TableEntity> obtainAllTable(final ResultSet rs) throws Exception {
        List<LocalEntity.TableEntity> dataList = new ArrayList<>();
        while (rs.next()) {
            LocalEntity.TableEntity tableEntity = new LocalEntity.TableEntity();
            tableEntity.setTableSchema(rs.getString("TABLE_SCHEMA"));
            tableEntity.setTableName(rs.getString("TABLE_NAME"));
            dataList.add(tableEntity);
        }
        return dataList;
    }

    /**
     * 将数据解析到 ColumnInfo
     **/
    public ColumnInfo resultSetToColumn(ResultSet rs, Set<String> keys) {
        ColumnInfo columnInfo = new ColumnInfo();
        for (String key : keys) {
            try {
                String value = rs.getString(humpToLine(key));
                Field field = columnInfo.getClass().getDeclaredField(key);
                field.setAccessible(true);
                field.set(columnInfo, value);
            } catch (Exception ignored) {
            }
        }
        columnInfo.afterInit();
        return columnInfo;
    }

    /**
     * 初始化 table 相关的数据
     **/
    public Table initTable(List<ColumnInfo> list) {
        Table table = new Table();
        ColumnInfo columnFirst = list.get(0);
        table.tableName = columnFirst.tableName;
        table.columnInfos = list;
        tableCommentDeal(table, columnFirst);
        // TODO: 获得 java 中的表名显示
        table.tableNameJava = firstToUppercase(toJava(removePrefix(table.tableName)));
        table.tableNameJavaParam = firstToLowercase(table.tableNameJava);
        table.tableNameJavaParamHump = humpToLine(table.tableNameJavaParam);
        table.tableNameJavaParamHumpUpper = table.tableNameJavaParamHump.toUpperCase();
        table.javaFilePath = isTrue(Config.getConfig().getStoreByTable()) ? table.tableNameJava.toLowerCase() : "";
        table.init();

        initColumnList(table);
        table.initLast();
        for (ColumnInfo c : table.columnInfos) {
            if (isNotIdCreateTimeUpdateTime(c.columnNameJava) && !c.columnName.toUpperCase().endsWith("_ID")) {
                table.firstNoId = c;
                break;
            }
        }
        return table;
    }

    /**
     * 根据注释中带来的信息,来丰富自己的字段
     * // todo : 有配置类的说明
     * // todo : 视频资源信息,每一集的信息;tb_video.id:video_id;
     * // todo : 视频资源信息,每一集的信息;tb_video.id:video_id,just_one;
     * // todo : 系统用户表;id:t_system_user_setting.user_id,just_one
     **/
    public void tableCommentDeal(Table table, ColumnInfo columnFirst) {
        // TODO: 处理 table comment
        table.tableComment = isNotEmpty(columnFirst.tableComment) ? columnFirst.tableComment : table.tableName;
        table.tableCommentRaw = table.tableComment;
        table.tableComment = table.tableCommentRaw.split(";")[0];
        if (endsWithIgnoreCase(table.tableComment, "表")) {
            table.tableComment = table.tableComment.substring(0, table.tableComment.length() - 1);
        }
        table.relateTable = new ArrayList<>();
        // todo : 视频资源信息,每一集的信息;tb_video.id:video_id,just_one;
        Stream.of(table.tableCommentRaw.split(";"))
                .filter(str -> str.contains(":"))
                .forEach(str -> {
                    RelateTableInfo relateTableInfo = new RelateTableInfo();
                    // todo : tb_video.id:video_id,just_one
                    Stream.of(str.split(":")).forEach(detailStr -> {
                        // todo : ["tb_video.id", "video_id,just_one"]
                        // todo : ["id", "t_system_user_setting.user_id,just_one"]
                        String detailDealStr = detailStr.contains(",") ? detailStr.split(",")[0] : detailStr;
                        relateTableInfo.more = equalsAnyIgnoreCase(detailDealStr, detailStr);
                        if (detailDealStr.contains(".")) {
                            String[] temp = detailDealStr.split("\\.");
                            relateTableInfo.otherTableName = temp[0];
                            relateTableInfo.otherTableColumnName = temp[1];
                        } else {
                            relateTableInfo.thisTableColumnName = detailDealStr;
                        }
                    });
                    table.relateTable.add(relateTableInfo);
                });
        // 匹配到 columnInfo
        for (RelateTableInfo relateTableInfo : table.relateTable) {
            ColumnInfo columnInfo = table.columnInfos.stream()
                    .filter(c -> equalsIgnoreCase(c.columnName, relateTableInfo.thisTableColumnName))
                    .findFirst().get();
            columnInfo.isOtherId = true;
            columnInfo.otherTableName = relateTableInfo.otherTableName;
            columnInfo.otherTable = relateTableInfo.otherTable;
        }
    }


    /**
     * 整理表数据, 将数据库的中的数据，确定好对应 java 中的什么数据
     **/
    public void initColumnList(Table table) {
        if (table == null || table.columnInfos == null) {
            return;
        }
        // todo : 确定每列的数据类型
        for (ColumnInfo columninfo : table.columnInfos) {
            String type = columninfo.dataType;
            // TODO : 确定 java 中 使用什么类型
            columninfo.typeToJavaData = TypeToJavaData.getTypeToJavaData(type, columninfo.columnName);
            // todo : 确定主键
            if (equalsIgnoreCase("pri", columninfo.columnKey)
                    || (table.id == null && equalsIgnoreCase("id", columninfo.columnKey))) {
                table.id = columninfo;
                if (!equalsIgnoreCase(columninfo.typeToJavaData.typeJava, String.class.getSimpleName())) {
                    columninfo.typeToJavaData = TypeToJavaData.obtainId();
                }
            }
            // TODO : 确定 java 中 使用什么名字
            columninfo.columnNameJava = isEmpty(columninfo.columnNameJava) ? toJava(columninfo.columnName.toLowerCase()) : columninfo.columnNameJava;
            columninfo.columnNameJavaParamHump = humpToLine(columninfo.columnNameJava);
            columninfo.columnNameJavaParamHumpUpper = columninfo.columnNameJavaParamHump.toUpperCase();
            columninfo.isId = columninfo == table.id;
            // TODO: 找到对应的 java 类型
            columninfo.dataTypeJava = isEmpty(columninfo.dataTypeJava) ? columninfo.typeToJavaData.typeJava : columninfo.dataTypeJava;
            // todo : 如果 没有, 那就使用 String
            columninfo.dataTypeJava = isEmpty(columninfo.dataTypeJava) ? String.class.getSimpleName() : columninfo.dataTypeJava;

            columninfo.typeDoc = TypeToJavaData.obtainTypeDoc(columninfo.dataTypeJava);
            columninfo.typeDocNote = TypeToJavaData.obtainJavaDocNote(columninfo.dataTypeJava);
            columninfo.isText = contains(columninfo.dataType, "text");
            columnCommentDeal(columninfo);
            if (isNotEmpty(columninfo.select)) {
                columninfo.constantName = table.tableNameJavaParamHumpUpper + "_" + columninfo.columnNameJavaParamHumpUpper;
            }
            columninfo.canNULL = equalsIgnoreCase(columninfo.isNullable, "yes");
            initColumnFindData(columninfo);
        }
    }

    /**
     * 根据注释中带来的信息,来丰富自己的字段
     * // todo : 有配置类的说明
     * // todo : 请求类型;多选;CREATED:新产生,DONE:完成,CLOSED:关闭,FAILED:失败,FAILED1:失败1,FAILED2:失败2
     * // todo : 请求类型;CREATED:新产生,DONE:完成,CLOSED:关闭,FAILED:失败,FAILED1:失败1,FAILED2:失败2
     * // todo : user_id.filter
     **/
    public void columnCommentDeal(ColumnInfo columninfo) {
        // TODO : 注释
        if (isBlank(columninfo.columnComment)) {
            columninfo.columnComment = columninfo.columnName;
            if (equalsAnyIgnoreCase(columninfo.columnName, "create_time", "create_date")) {
                columninfo.columnComment = "产生时间";
            }
            if (equalsAnyIgnoreCase(columninfo.columnName, "update_time", "update_date")) {
                columninfo.columnComment = "更新时间";
            }
        }
        if (isBlank(columninfo.columnComment)) {
            return;
        }
        String[] splitByColon = columninfo.columnComment.split(";");
        if (splitByColon.length == 2 || splitByColon.length == 3) {
            // todo : 请求类型;多选;CREATED:新产生,DONE:完成,CLOSED:关闭,FAILED:失败,FAILED1:失败1,FAILED2:失败2
            // todo : 请求类型;CREATED:新产生,DONE:完成,CLOSED:关闭,FAILED:失败,FAILED1:失败1,FAILED2:失败2
            boolean use = false;
            for (String colonOne : splitByColon) {
                if (colonOne.contains(":")) {
                    List<SelectData> selectDataList = stringToObjectList(colonOne);
                    if (isNotEmpty(selectDataList)) {
                        columninfo.select = selectDataList;
                        columninfo.isConstant = true;
                        use = true;
                        if (splitByColon.length == 3) {
                            columninfo.isSelectMore = true;
                        }
                    }
                }
            }
            // todo : 类型;常量
            if (!use) {
                columninfo.isConstant = true;
            }
        }
        // todo : columnComment 处理
        columninfo.columnCommentRaw = columninfo.columnComment;
        columninfo.columnComment = columninfo.columnComment.replaceAll(_FILTER, "");
        columninfo.columnComment = columninfo.columnComment.split(";")[0];
    }

    /**
     * 初始化 ColumnInfo 中的 findData 字段
     **/
    public void initColumnFindData(ColumnInfo columninfo) {
        // TODO : 在查询的时候, 需要的字段
        if (columninfo.isOtherId || columninfo.isConstant) {
            columninfo.findData.add(FindData.of(
                    columninfo.columnNameJavaParamHumpUpper,
                    columninfo.columnNameJava,
                    null,
                    columninfo.columnComment + "-多个以逗号分割",
                    false,
                    true
            ));
        } else if (isID(columninfo.columnName)) {
            columninfo.findData.add(FindData.of(
                    columninfo.columnNameJavaParamHumpUpper,
                    columninfo.columnNameJava,
                    null,
                    columninfo.columnComment,
                    false,
                    true
            ));
            columninfo.findData.add(FindData.of(
                    columninfo.columnNameJavaParamHumpUpper + "S",
                    columninfo.columnNameJava + "s",
                    null,
                    columninfo.columnComment + "-多个以逗号分割",
                    false,
                    true
            ));
        } else if (columninfo.columnCommentRaw.contains(_FILTER)) {
            columninfo.findData.add(FindData.of(
                    columninfo.columnNameJavaParamHumpUpper,
                    columninfo.columnNameJava,
                    null,
                    columninfo.columnComment
            ));
        }
    }

    // todo : CREATED:新产生,DONE:完成,CLOSED:关闭,FAILED:失败,FAILED1:失败1,FAILED2:失败2
    public List<SelectData> stringToObjectList(final String s) {
        List<SelectData> select = new ArrayList<>();
        String[] statusList = s.split(",");
        for (String status : statusList) {
            String[] kv = status.split(":");
            if (kv.length == 2) {
                select.add(SelectData.of(kv[0].trim().toUpperCase(), kv[1].trim()));
            }
        }
        return select;
    }


}
