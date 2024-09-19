package com.platform.auto.jdbc;

import com.platform.auto.config.Config;
import com.platform.auto.config.ConfigEntity;
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
                List<ConfigEntity.Info> infoList = Config.getConfig().info;
                for (ConfigEntity.Info info : infoList) {
                    if (isBlank(info.template)) {
                        continue;
                    }
                    if (info.template.contains(CONTROLLER_UP)) {
                        new ControllerCreator(table, info);
                        logger.info("table: {}, template_controller: {}", table.tableNameJavaParam, info.template);
                    }
                    if (info.template.contains(SERVICE_UP)) {
                        new ServiceCreator(table, info);
                        logger.info("table: {}, template_service: {}", table.tableNameJavaParam, info.template);
                    }
                    if (info.template.contains(MAPPER_UP)) {
                        new MapperCreator(table, info);
                        logger.info("table: {}, template_mapper: {}", table.tableNameJavaParam, info.template);
                    }
                    if (info.template.contains(SQL_PROVIDER_UP)) {
                        new SqlProviderCreator(table, info);
                        logger.info("table: {}, template_sqlProvider: {}", table.tableNameJavaParam, info.template);
                    }
                    if (info.template.contains(ENTITY_UP)) {
                        new EntityCreator(table, info);
                        logger.info("table: {}, template_entity: {}", table.tableNameJavaParam, info.template);
                    }
                    if (info.template.contains(DATA_UP)) {
                        new DataCreator(table, info);
                        logger.info("table: {}, template_data: {}", table.tableNameJavaParam, info.template);
                    }
                    if (info.template.contains(DTO_UP)) {
                        new DtoCreator(table, info);
                        logger.info("table: {}, template_dto: {}", table.tableNameJavaParam, info.template);
                    }
                    if (info.template.contains(USEFUL_UP)) {
                        new UsefulCreator(table, info);
                        logger.info("table: {}, template_useful: {}", table.tableNameJavaParam, info.template);
                    }
                    if (info.template.contains(DOC_TABLE_UP)) {
                        new DocTableCreator(table, info);
                        logger.info("table: {}, template_docTable: {}", table.tableNameJavaParam, info.template);
                    }
                    if (info.template.contains(DOC_POSTMAN_UP)) {
                        new PostManTableCreator(table, info);
                        logger.info("table: {}, template_docPostMan: {}", table.tableNameJavaParam, info.template);
                    }
                }
            } catch (Exception e) {
                logger.info("generate_error,table: {}", table.tableNameJavaParam);
                logger.info(e);
            }
        });
    }

}
