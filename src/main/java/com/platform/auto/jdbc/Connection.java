package com.platform.auto.jdbc;

import com.platform.auto.config.LocalEntity;
import com.platform.auto.jdbc.base.TableFactory;
import com.platform.auto.jdbc.model.Table;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import com.platform.auto.util.CharUtil;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * jdbc.mysql.Connection.java
 * 共用的处理连接的一个类 ,
 * 调用 prepare, getTable 方法可以获得数据库中指定表的信息
 * </p>
 * <p>
 * description :
 * </p>
 *
 * @author YangPu
 * @createTime 2016年7月19日 上午11:44:13
 */
public class Connection extends CharUtil {

    private static final Logger logger = AutoLogger.getLogger(Connection.class);

    public static String clazz;
    public static String url;
    public static String username;
    public static String password;
    public static String database;

    public static void prepare(String... param) {
        clazz = param[0];
        url = param[1];
        username = param[2];
        password = param[3];
        database = param[4];
    }

    public static void prepare(String clazz1, String url1, String username1, String password1, String database1) {
        clazz = clazz1;
        url = url1;
        username = username1;
        password = password1;
        database = database1;
    }

    public static List<Table> getTable(List<String> tableName) throws Exception {
        List<Table> tables = getTableRun(tableName);
        // TODO: 检查一下 是否需要其他表的数据
        // TODO: 如果有外键, 或者其他表的id的话,需要获得其他表的数据
        assert tables != null;
        String otherTableNameArray = tables.stream()
                .map(t -> t.columnInfos
                        .stream()
                        .filter(c -> c.isOtherId && isNotEmpty(c.otherTableName))
                        .map(c -> c.otherTableName)
                        .collect(Collectors.joining(","))
                )
                .collect(Collectors.joining(","));
        String relateTableName = tables.stream()
                .map(t -> t.relateTable
                        .stream()
                        .filter(r -> isNotEmpty(r.otherTableName))
                        .map(r -> r.otherTableName)
                        .collect(Collectors.joining(","))
                )
                .collect(Collectors.joining(","));
        if (isNotEmpty(otherTableNameArray) || isNotEmpty(relateTableName)) {
            List<Table> otherTables = getTableRun(List.of((otherTableNameArray + "," + relateTableName).split(",")));
            if (isNotEmpty(otherTables)) {
                tables.forEach(t -> {
                    t.columnInfos.forEach(c -> {
                        if (c.isOtherId && isNotEmpty(c.otherTableName)) {
                            c.otherTable = otherTables
                                    .stream()
                                    .filter(o -> o.tableName.equals(c.otherTableName))
                                    .findFirst()
                                    .orElse(null);
                        }
                    });
                    // todo : table.relateTable
                    t.relateTable.forEach(r -> {
                        if (isNotEmpty(r.otherTableName)) {
                            r.otherTable = otherTables.stream()
                                    .filter(o -> o.tableName.equals(r.otherTableName))
                                    .findFirst()
                                    .orElse(null);
                            if (r.otherTable != null) {
                                r.otherTableColumn = r.otherTable.columnInfos.stream()
                                        .filter(c -> c.columnName.equals(r.otherTableColumnName))
                                        .findFirst()
                                        .orElse(null);
                            }
                        }
                        r.thisTableColumn = t.columnInfos.stream()
                                .filter(c -> c.columnName.equals(r.thisTableColumnName))
                                .findFirst()
                                .orElse(null);
                    });
                });
            }
        }
        for (Table table : tables) {
            table.obtainOtherTable();
        }
        return tables;
    }

    /**
     * 返回每个表应该生成的代码的数据
     **/
    public static List<Table> getTableRun(List<String> tableName) throws Exception {
        if (isNotEmpty(tableName)) {
            logger.info("tableName: {}", String.join(",", tableName));
        }
        Class.forName(clazz);
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        java.sql.Connection conn = DriverManager.getConnection(
                url + database + "?useSSL=false&serverTimezone=GMT&autoReconnect=true",
                username,
                password);
        Statement st = conn.createStatement();
        // todo : 获得 sql, 获得查询出表结构的 sql
        String sql = obtainSql(tableName);
        if (isEmpty(sql)) {
            logger.info("no query sql");
            return null;
        }
        logger.info("get table information from database -start");
        logger.info(sql);
        // todo : 根据 sql 获得表结构的数据
        ResultSet rs = st.executeQuery(sql);
        logger.info("get table information from database -end");
        // todo : 将返回的数据库的结果处理成 table
        TableFactory tableFactory = new TableFactory();
        List<Table> tables = tableFactory.obtainTable(rs);
        logger.info("analyze table data -end");
        rs.close();
        st.close();
        conn.close();
        return tables;
    }

    public static List<LocalEntity.TableEntity> getAllTableInfo() throws Exception {
        Class.forName(clazz);
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        java.sql.Connection conn = DriverManager.getConnection(
                url + database + "?useSSL=false&serverTimezone=GMT&autoReconnect=true",
                username,
                password);
        String sql = "SELECT table_schema, table_name\n" +
                "FROM `information_schema`.`tables`\n" +
                "WHERE table_schema NOT IN ('information_schema', 'performance_schema', 'mysql', 'sys')\n" +
                "order by 1 asc, 2 asc;";
        Statement st = conn.createStatement();
        logger.info(sql);
        // todo : 根据 sql 获得表结构的数据
        ResultSet rs = st.executeQuery(sql);
        // todo : 将返回的数据库的结果处理成 table
        TableFactory tableFactory = new TableFactory();
        List<LocalEntity.TableEntity> dataList = tableFactory.obtainAllTable(rs);
        rs.close();
        st.close();
        conn.close();
        return dataList;
    }

    /**
     * 获得sql
     * 目前支持 mysql, pgsql
     **/
    public static String obtainSql(final List<String> tableName) {
        String sql = null, tableSql = null;
        if (isNotEmpty(tableName)) {
            tableSql = tableName.stream().map(t -> s1 + t + s1).collect(Collectors.joining(","));
        }
        String ts = "";
        if (clazz.contains("mysql")) {
            if (isNotEmpty(tableSql)) {
                ts = String.format(" and c.table_name in (%s)", tableSql);
            }
            // todo : 必须按照table_name排序,否则,后面就可能出错了
            sql = "SELECT" +
                    " c.table_catalog," +
                    " c.table_schema," +
                    " c.table_name," +
                    " c.column_name," +
                    " c.ordinal_position," +
                    " c.column_default," +
                    " c.is_nullable," +
                    " c.data_type," +
                    " c.character_maximum_length," +
                    " c.character_octet_length," +
                    " c.numeric_precision," +
                    " c.numeric_scale," +
                    " c.character_set_name," +
                    " c.collation_name," +
                    " c.column_type," +
                    " c.column_key," +
                    " c.extra," +
                    " c.PRIVILEGES," +
                    " c.column_comment," +
                    " t.table_comment as table_comment \n" +
                    " FROM information_schema.COLUMNS c\n" +
                    " INNER JOIN information_schema.TABLES t ON c.table_name = t.table_name\n" +
                    " WHERE c.table_schema = '%s'\n" +
                    " and t.table_schema = '%s'\n" +
                    ts +
                    " order by c.table_name asc, c.ordinal_position asc, c.table_schema asc, c.column_key desc;";
            sql = String.format(sql, database, database);
        }
        if (clazz.contains("postgresql")) {
            if (isNotEmpty(tableSql)) {
                ts = String.format(" and c.relname in (%s)", tableSql);
            }
            sql = "SELECT a.attnum," +
                    " c.relname as table_name," +
                    " a.attname AS column_name," +
                    " t.typname AS data_type," +
                    " a.attlen AS numeric_precision," +
                    " a.atttypmod AS character_maximum_length," +
                    " a.attnotnull AS is_nullable," +
                    " b.description AS column_comment" +
                    " FROM pg_class c, pg_attribute a" +
                    " LEFT OUTER JOIN pg_description b ON a.attrelid=b.objoid AND a.attnum = b.objsubid," +
                    " pg_type t WHERE a.attnum > 0" +
                    ts +
                    " and a.attrelid = c.oid and a.atttypid = t.oid" +
                    " ORDER BY a.relname asc, a.attnum";
        }
        sql = sql.replaceAll(",", ", \n");
        return sql;
    }

}
