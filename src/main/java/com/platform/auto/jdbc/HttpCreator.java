package com.platform.auto.jdbc;

import com.platform.auto.jdbc.base.BaseCreator;
import com.platform.auto.jdbc.model.ColumnInfo;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import com.platform.auto.sys.order.Order;
import com.platform.auto.util.FileUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.platform.auto.util.AutoUtil.objectToLocalFile;
import static com.platform.auto.util.AutoUtil.readFromLocalJson;
import static com.platform.auto.util.CharUtil.*;

/**
 * yangpu.jdbc.mysql.ModelCreate.java<br>
 * Description : <br>
 *
 * @author YangPu
 * @createTime 2016年7月21日 下午3:50:33
 */
public class HttpCreator extends BaseCreator {

    private static final Logger logger = AutoLogger.getLogger(HttpCreator.class);

    public HttpCreator(BaseCreator baseCreator) {
        super(baseCreator);
    }

    @Override
    public void create() {
        try {
            createRequestInit();
        } catch (Exception e) {
            logger.info(e);
        }
        List<String> templateList = this.copyCodeListAndClear();
        for (String line : templateList) {
            if (Order.check(line, Order.jsonStart2)) {
                createJson2();
            } else {
                codeList.add(line);
            }
        }
    }

    public static void createRequestInit() throws Exception {
        // generated-requests.http
        // http-client.private.env.json
        String requestFileName = "http.http";
        String envJsonFileName = "http-client.env.json";
        String requestPath = "http/" + requestFileName;
        String envPath = "http/" + envJsonFileName;
        FileUtil.createLocalFile(requestPath);
        FileUtil.createLocalFile(envPath);
        if (StringUtils.isBlank(readFromLocalJson(envPath))) {
            objectToLocalFile(envPath, Map.of(
                    "prod", Map.of("host", "https://admin-prod.baidu.com"),
                    "test", Map.of("host", "https://admin-test.baidu.com"),
                    "dev", Map.of("host", "https://admin-dev.baidu.com"),
                    "local", Map.of("host", "http://127.0.0.1:8080")
            ));
            logger.info("init: {}", envPath);
        }
    }

    private void createJson2() {
        for (ColumnInfo columninfo : this.table.columnInfos) {
            String field = columninfo.columnNameJava;
            if (isCreateTimeUpdateTime(columninfo.columnNameJava)) {
                continue;
            }
            String value = "\"" + columninfo.columnNameJava + "\"";
            if (!columninfo.typeToJavaData.jsonNeedColon) {
                value = "" + 1234;
            }
            codeList.add("    \"" + field + "\"" + ": " + value + ",");
        }
        this.removeLastCountString(1);
    }

}
