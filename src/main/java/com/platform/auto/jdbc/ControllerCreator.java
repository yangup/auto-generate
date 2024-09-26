package com.platform.auto.jdbc;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.platform.auto.jdbc.base.BaseCreator;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import com.platform.auto.util.AutoUtil;
import com.platform.auto.util.FileUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.platform.auto.util.AutoUtil.readFromLocalJson;
import static com.platform.auto.util.CharUtil.*;
import static com.platform.auto.config.Config.*;

/**
 * <p>
 * yangpu.jdbc.mysql.ControllerCreate.java
 * </p>
 * <p>
 * description :
 * </p>
 *
 * @author YangPu
 * @createTime 2016年7月21日 下午3:50:33
 */
public class ControllerCreator extends BaseCreator {

    private static final Logger logger = AutoLogger.getLogger(ControllerCreator.class);

    public ControllerCreator(BaseCreator baseCreator) {
        super(baseCreator);
    }

    @Override
    public void create() {


        // 最后生成 request http 文件
        try {
            createRequest();
        } catch (Exception e) {
            logger.info(e);
        }
    }

    /**
     * 生成 .http 文件
     **/
    public void createRequest() throws Exception {
        // generated-requests.http
        // http-client.private.env.json
        String requestFileName = "generated-requests.http";
        String envJsonFileName = "http-client.private.env.json";
        String requestPath = "http/" + requestFileName;
        String envPath = "http/" + envJsonFileName;
        FileUtil.createLocalFile(requestPath);
        FileUtil.createLocalFile(envPath);
        if (StringUtils.isBlank(readFromLocalJson(envPath))) {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            AutoUtil.listToLocalFile(envPath, List.of(objectMapper.writeValueAsString(Map.of(
                    "prod", Map.of("host", "https://admin-prod.baidu.com"),
                    "test", Map.of("host", "https://admin-test.baidu.com"),
                    "dev", Map.of("host", "https://admin-dev.baidu.com"),
                    "local", Map.of("host", "http://127.0.0.1:8080")
            ))));
            logger.info("init: {}", envPath);
        }

//        ### v1/Report/type/buriedPointCommon.all
//        POST {{host}}/v1/Report/type/buriedPointCommon.all
//        Content-Type: application/json;charset=utf-8
//
//        {
//            "phase_code": "LOAN"
//        }
//        @RequestMapping("/user/")
//        @PostMapping("delete")
//        @PostMapping("addUpdate")
//        @GetMapping("find")
        List<String> httpCodeList = new ArrayList<>(64);
        List<String> httpHostList = new ArrayList<>(64);
        String frontPath = "";
        Pattern pattern = Pattern.compile("@(\\w+)Mapping\\(\"([^\"]*)\"\\)");
        for (int i = 0; i < this.codeList.size(); i++) {
            Matcher matcher = pattern.matcher(this.codeList.get(i).trim());
            if (matcher.find()) {
                String path = matcher.group(2);
                if (isBlank(frontPath)) {
                    int frontIndex = i + 3;
                    while (i - frontIndex < 10 && isBlank(frontPath) && frontIndex >= 0) {
                        frontIndex--;
                        if (equalsAnyIgnoreCase(this.codeList.get(frontIndex).trim(), "@RestController", "@RestController")) {
                            frontPath = path;
                        }
                    }
                }
                // 需要的 path
                if (isEqual(frontPath, path)) {
                    continue;
                }
                frontPath = frontPath.startsWith("/") ? frontPath : "/" + frontPath;
                httpCodeList.add("### " + frontPath + path);
                if (this.codeList.get(i).trim().contains("@GetMapping(\"")) {
                    String host = "GET {{host}}" + frontPath + path;
                    httpCodeList.add(host);
                    httpHostList.add(host);
                } else {
                    String host = "POST {{host}}" + frontPath + path;
                    httpCodeList.add(host);
                    httpHostList.add(host);
                    httpCodeList.add("Content-Type: application/json");
                    httpCodeList.add("");
                    httpCodeList.add("{");
                    if (equalsIgnoreCase(path, "delete")) {
                        httpCodeList.add("  " + String.join(",\n  ",
                                String.format("\"%s\": %s",
                                        table.id.columnNameJava,
                                        (table.id.typeToJavaData.jsonNeedColon ? "\"" : "") + table.id.columnNameJava + (table.id.typeToJavaData.jsonNeedColon ? "\"" : "")
                                )));
                    } else {
                        httpCodeList.add("  " + String.join(",\n  ",
                                this.table.columnInfos.stream()
                                        .map(c -> String.format("\"%s\": %s",
                                                c.columnNameJava,
                                                (c.typeToJavaData.jsonNeedColon ? "\"" : "") + c.columnNameJava + (c.typeToJavaData.jsonNeedColon ? "\"" : "")
                                        )).toList()
                        ));
                    }
                    httpCodeList.add("}");
                }
                httpCodeList.add("");
            }
        }
        if (isNotEmpty(httpCodeList)) {
            List<String> oldList = AutoUtil.readFromLocal(requestPath);
            boolean exist = false;
            for (String hostCode : httpHostList) {
                for (String old : oldList) {
                    if (equalsIgnoreCase(old, hostCode)) {
                        exist = true;
                    }
                }
            }
            if (!exist) {
                oldList.add("");
                oldList.addAll(httpCodeList);
            }
            AutoUtil.listToLocalFile(requestPath, oldList);
        }

    }


}














