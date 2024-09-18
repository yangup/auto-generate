package com.platform.auto.jdbc;

import com.platform.auto.config.Config;
import com.platform.auto.jdbc.model.Table;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import com.platform.auto.util.CharUtil;

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

    public static void generate(List<Table> tables) {
        tables.forEach(table -> {
            try {
                if (isNotBlank(Config.getConfig().template.controller)) {
                    new ControllerCreator(table);
                    logger.info("table: {}, template_controller: {}", table.tableNameJavaParam, Config.getConfig().template.controller);
                }
                if (isNotBlank(Config.getConfig().template.service)) {
                    new ServiceCreator(table);
                    logger.info("table: {}, template_service: {}", table.tableNameJavaParam, Config.getConfig().template.service);
                }
                if (isNotBlank(Config.getConfig().template.mapper)) {
                    new MapperCreator(table);
                    logger.info("table: {}, template_mapper: {}", table.tableNameJavaParam, Config.getConfig().template.mapper);
                }
                if (isNotBlank(Config.getConfig().template.sqlProvider)) {
                    new SqlProviderCreator(table);
                    logger.info("table: {}, template_sqlProvider: {}", table.tableNameJavaParam, Config.getConfig().template.sqlProvider);
                }
                if (isNotBlank(Config.getConfig().template.entity)) {
                    new EntityCreator(table);
                    logger.info("table: {}, template_entity: {}", table.tableNameJavaParam, Config.getConfig().template.entity);
                }
                if (isNotBlank(Config.getConfig().template.data)) {
                    new DataCreator(table);
                    logger.info("table: {}, template_data: {}", table.tableNameJavaParam, Config.getConfig().template.data);
                }
                if (isNotBlank(Config.getConfig().template.dto)) {
                    new DtoCreator(table);
                    logger.info("table: {}, template_dto: {}", table.tableNameJavaParam, Config.getConfig().template.dto);
                }
                if (isNotBlank(Config.getConfig().template.useful)) {
                    new UsefulCreator(table);
                    logger.info("table: {}, template_useful: {}", table.tableNameJavaParam, Config.getConfig().template.useful);
                }
                if (isNotBlank(Config.getConfig().template.docTable)) {
                    new DocTableCreator(table);
                    logger.info("table: {}, template_docTable: {}", table.tableNameJavaParam, Config.getConfig().template.docTable);
                }
                if (isNotBlank(Config.getConfig().template.docPostMan)) {
                    new PostManTableCreator(table);
                    logger.info("table: {}, template_docPostMan: {}", table.tableNameJavaParam, Config.getConfig().template.docPostMan);
                }
            } catch (Exception e) {
                logger.info("generate_error,table: {}", table.tableNameJavaParam);
                logger.info(e);
            }
        });
    }

}
