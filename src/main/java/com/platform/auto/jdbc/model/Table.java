package com.platform.auto.jdbc.model;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YangPu
 * @description: <br/>
 * @version: 1<br />
 * @package com.platform.auto.jdbc.mysql.model.Table.java
 * @date 2017年1月16日 上午11:51:43
 */
public class Table {

    // todo : 表的名称
    // todo : house_side_info
    public String tableName;
    // todo : HouseSideInfo
    public String tableNameJava;
    // todo : houseSideInfo
    public String tableNameJavaParam;
    // todo : house_side_info
    public String tableNameJavaParamHump;
    // todo : HOUSE_SIDE_INFO
    public String tableNameJavaParamHumpUpper;
    // TODO: 2021/12/10 例如 : t_message 简写 : m . t_message_read 简写 : mr
    public String tableNameSimple;
    // todo : 表的注释
    public String tableComment;
    public String tableCommentRaw;
    // todo : 表中的每一列的信息
    public List<ColumnInfo> columnInfos;
    public List<FindData> findData;

    public static List<QueryMapFindParam> queryMapFindParamStatic = new ArrayList<>();
    public static List<QueryMapFindParam> pageBoundsFindParamStatic = new ArrayList<>();
    public static List<QueryMapFindParam> createUpdateTimeFindParamStatic = new ArrayList<>();
    public static List<QueryMapFindParam> idFindParamStatic = new ArrayList<>();
    public static List<QueryMapFindParam> allParamStatic = new ArrayList<>();
    // todo : 主键列,有或无--没有联合主键的情况--多个主键,现在不支持
    public ColumnInfo id;
    // todo : 第一个, 不是 id 的列, 不是以 _id 结尾的列
    public ColumnInfo firstNoId;

    /**
     * 指定前端文件的文件夹
     * 例如 : t_demo4,
     * 如果 frontFile 为空 , 那就放到 demo4/demo4.vue
     * 如果 frontFile 不为空 , 那就放到 frontFile/demo4.vue
     */
    public String frontFilePath;

    public String javaFilePath;

    // todo : 其他表的数据
    public List<PageListParam> relateTable;

    public List<Table> otherTable;

    static {
        idFindParamStatic.add(QueryMapFindParam.of(
                "id", "", "字符串", "id,例如: 1234", false, false
        ));
        idFindParamStatic.add(QueryMapFindParam.of(
                "ids", "", "字符串", "多个id以逗号分隔,例如: 1234,2345", false, false
        ));
        pageBoundsFindParamStatic.add(QueryMapFindParam.of(
                "page", "1", "整数", "页码,第几页,编号从 1 开始, 默认第1页", false, true
        ));
        pageBoundsFindParamStatic.add(QueryMapFindParam.of(
                "size", "10", "整数", "每页多少条数据,默认值:1 , 最大1000", false, true
        ));
        pageBoundsFindParamStatic.add(QueryMapFindParam.of(
                "limit", "10", "整数", "每页多少条数据,默认值:1 , 最大1000,与size 一样", false, false
        ));
        queryMapFindParamStatic.add(QueryMapFindParam.of(
                "startTime", "", "日期时间", "开始时间,例如 : 2017-01-01 12:13:14 </br> 或者 例如 : 2017-01-01", false, false
        ));
        queryMapFindParamStatic.add(QueryMapFindParam.of(
                "endTime", "", "日期时间", "结束时间,例如 : 2023-01-02 12:13:14 </br> 或者 例如 : 2023-01-02", false, false
        ));
        queryMapFindParamStatic.add(QueryMapFindParam.of(
                "all", "all", "字符串", "分页状态下,查询出全部数据,相当于page=1,size=2000", false, false
        ));
        queryMapFindParamStatic.add(QueryMapFindParam.of(
                "_all", "_all", "字符串", "查询全部的原始数据,小心使用,相当于page=1,size=20000,_raw=_raw", false, false
        ));
        queryMapFindParamStatic.add(QueryMapFindParam.of(
                "_one", "_one", "字符串", "查询出一条数据,相当于page=1,size=1,返回对象,不是数组", false, false
        ));
        queryMapFindParamStatic.add(QueryMapFindParam.of(
                "_raw", "_raw", "字符串", "原始数据查询,查询原始表数据,不附带其他,数据结构与表对应起来", false, false
        ));
        createUpdateTimeFindParamStatic.add(QueryMapFindParam.of(
                "createTimeFrom", "", "日期时间", "产生时间的开始时间,例如 : 2023-01-02 12:13:14 </br> 或者 例如 : 2023-01-02", false, false
        ));
        createUpdateTimeFindParamStatic.add(QueryMapFindParam.of(
                "createTimeTo", "", "日期时间", "产生时间的结束时间,例如 : 2023-01-02 12:13:14 </br> 或者 例如 : 2023-01-02", false, false
        ));
        createUpdateTimeFindParamStatic.add(QueryMapFindParam.of(
                "updateTimeFrom", "", "日期时间", "更新时间的开始时间,例如 : 2023-01-02 12:13:14 </br> 或者 例如 : 2023-01-02", false, false
        ));
        createUpdateTimeFindParamStatic.add(QueryMapFindParam.of(
                "updateTimeTo", "", "日期时间", "更新时间的结束时间,例如 : 2023-01-02 12:13:14 </br> 或者 例如 : 2023-01-02", false, false
        ));

        allParamStatic.addAll(queryMapFindParamStatic);
        allParamStatic.addAll(pageBoundsFindParamStatic);
        allParamStatic.addAll(createUpdateTimeFindParamStatic);
        allParamStatic.addAll(idFindParamStatic);
    }

    /**
     * 是否有其他表的信息
     **/
    public void obtainOtherTable() {
        if (ObjectUtils.isEmpty(this.otherTable) && columnInfos != null) {
            List<ColumnInfo> list = columnInfos.stream()
                    .filter(c -> c.otherTable != null)
                    .toList();
            this.otherTable = list.stream().map(l -> l.otherTable).toList();
        }
        if (this.otherTable == null) {
            this.otherTable = new ArrayList<>();
        }
    }

    public static boolean inParamStatic(String key) {
        return Table.allParamStatic.stream().anyMatch(q -> q.key.equals(key));
    }

    public static boolean inQueryMap(String key) {
        return Table.queryMapFindParamStatic.stream().anyMatch(q -> q.key.equals(key));
    }

    public void initLast() {
        findData = new ArrayList<>();
        columnInfos.forEach(c -> {
            findData.addAll(c.findData);
        });
        this.obtainOtherTable();
    }

    /**
     * 初始化 table name simple
     **/
    public void init() {
        if (StringUtils.isEmpty(this.tableName)) {
            return;
        }
        if (StringUtils.isNotEmpty(this.tableNameSimple)) {
            return;
        }
        StringBuilder result = new StringBuilder();
        String n = this.tableName;
        n = n.toLowerCase();
        int strLen = n.length();
        for (int i = 0; i < strLen; i++) {
            char c = n.charAt(i);
            // TODO: 2021/12/10 第一位是字母, 第二位不是 _
            if (i == 0 && Character.isLetter(c)) {
                i++;
                if (i < strLen) {
                    if (n.charAt(i) != '_') {
                        result.append(Character.toLowerCase(c));
                    }
                }
            }
            c = n.charAt(i);
            if (c == '_') {
                i++;
                if (i < strLen) {
                    c = n.charAt(i);
                    result.append(Character.toLowerCase(c));
                }
            }
        }
        this.tableNameSimple = result.toString();
    }

}
