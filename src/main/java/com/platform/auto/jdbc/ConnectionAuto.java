package com.platform.auto.jdbc;

import com.platform.auto.config.Config;
import com.platform.auto.entity.ConfigInfoEntity;
import com.platform.auto.jdbc.base.BaseCreator;
import com.platform.auto.jdbc.model.Table;
import com.platform.auto.jdbc.model.TypeToJavaData;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import com.platform.auto.util.AutoUtil;
import com.platform.auto.util.CharUtil;
import com.platform.auto.util.FileUtil;

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

    public static void generate(List<Table> tables) throws Exception{
        logger.info(TypeToJavaData.objectMapper.writeValueAsString(tables));
        tables.forEach(table -> {
            try {
                List<ConfigInfoEntity> infoList = Config.getConfig().info;
                for (ConfigInfoEntity info : infoList) {
                    if (isBlank(info.template)) {
                        continue;
                    }
                    BaseCreator baseCreator = new BaseCreator(info, table);
                    baseCreator.create();
                    new ControllerCreator(baseCreator).create();
                    new ServiceCreator(baseCreator).create();
                    new MapperCreator(baseCreator).create();
                    new EntityCreator(baseCreator).create();
                    new SqlProviderCreator(baseCreator).create();
                    new DataCreator(baseCreator).create();
                    new DtoCreator(baseCreator).create();
                    new UsefulCreator(baseCreator).create();
                    new DocTableCreator(baseCreator).create();
                    new PostManTableCreator(baseCreator).create();
                    new HttpCreator(baseCreator).create();
                    new FrontCreate(baseCreator).create();
                    if (info.path != null && isNotBlank(info.path.file)) {
                        AutoUtil.newCodeToAppendFile(baseCreator.codeList, FileUtil.createFile(table, info));
                    } else {
                        AutoUtil.newCodeToFile(baseCreator.codeList, FileUtil.createFile(table, info));
                    }
                    logger.info("table: {}, info: {}", table.tableNameJavaParam, info);
                }
            } catch (Exception e) {
                logger.info("generate_error,table: {}", table.tableNameJavaParam);
                logger.info(e);
            }
        });
    }

}
