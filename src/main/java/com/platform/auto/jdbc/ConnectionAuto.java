package com.platform.auto.jdbc;

import com.platform.auto.Application;
import com.platform.auto.jdbc.base.BaseCreate;
import com.platform.auto.jdbc.model.Table;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import com.platform.auto.util.CharUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * <p>
 * jdbc.mysql.Connection.java
 * </p>
 * <p>
 * description :
 * </p>
 *
 * @author YangPu
 * @createTime 2016年7月19日 上午11:44:13
 */
public class ConnectionAuto extends CharUtil {

    private static final Logger logger = AutoLogger.getLogger(ConnectionAuto.class);

    public static void prepare(String clazz, String url, String username, String password, String database) {
        Connection.prepare(clazz, url, username, password, database);
    }

    /**
     * 自动生成,
     **/
    public static void start(List<String> tableName) throws Exception {
        generate(Connection.getTable(tableName));
    }

    public static void generate(List<Table> tables) throws Exception {
        // 需要生成这些类
        List<Class<? extends BaseCreate>> needRunClazz = List.of(
                // todo: javas
                ControllerCreate.class,
                ServiceCreate.class,
                MapperCreate.class,
                SqlProviderCreate.class,
                // todo: data
                EntityCreate.class,
                DataCreate.class,
                DtoCreate.class,
                // todo : useful
                UsefulCreate.class,
                DocTableCreate.class,
                PostManTableCreate.class
        );
        tables.forEach(table -> {
            String nameShow = table.tableNameJavaParam;
            doThing(table);
            needRunClazz.forEach(clazz -> {
                try {
                    logger.info("table: {}, clazz: {}", nameShow, clazz.getName());
                    Constructor<?> constructor = clazz.getDeclaredConstructor(Table.class);
                    constructor.newInstance(table);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    public static void doThing(Table table) {
//        for (ColumnInfo c : table.columnInfos) {
////            {
////                title: '监测网点',
////                        key: 'name',
////            }
//            System.out.println(String.format(
//                            "        {\n" +
//                            "          title: '%s',\n" +
//                            "          width: 100,\n" +
//                            "          key: '%s',\n" +
//                            "        },", c.columnComment, c.columnNameJava
//            ));
//        }


    }

}
