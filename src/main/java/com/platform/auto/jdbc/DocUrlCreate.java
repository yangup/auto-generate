//package com.platform.auto.jdbc;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.platform.auto.jdbc.model.*;
//import com.platform.auto.sys.log.AutoLogger;
//import com.platform.auto.sys.log.Logger;
//import com.platform.auto.sys.order.Order;
//import com.platform.auto.util.AutoUtil;
//import com.platform.auto.util.CharUtil;
//import okhttp3.*;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.time.DateFormatUtils;
//
//import java.io.File;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//
///**
// *
// */
//public class DocUrlCreate extends CharUtil {
//
//    private static final Logger logger = AutoLogger.getLogger(DocUrlCreate.class);
//
//    private List<DocInfo> docList;
//
//    public List<String> codeList;
//
//    private OkHttpClient okHttpClient;
//    private ObjectMapper om;
//
//    public List<String> templateList;
//
//    public static Map<String, String> dataMap = new HashMap<>();
//
//
//    /**
//     * 加载模板
//     */
//    public DocUrlCreate(List<DocInfo> docList, String fileName) throws Exception {
//        this.okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .build();
//        this.om = new ObjectMapper();
//        this.docList = docList;
//
//        // TODO: 将文件写入到 showdoc 服务器中
//        boolean isToServer = false;
//        int number = 1;
//        if (StringUtils.isNoneBlank(Constant.doc_url, Constant.doc_api_key, Constant.doc_api_token)) {
//            isToServer = true;
//        }
//
//        // TODO: 最终生成的文件
//        String name = fileName + "_" + Constant.name + "_" + DateFormatUtils.format(new Date(), "yyyyMMdd_HHmmss");
//        templateList = AutoUtil.readTemplate(Constant.docUrl);
//        codeList = new ArrayList<>(templateList.size() * 2);
//        for (DocInfo docInfo : docList) {
//            generateOne(docInfo, isToServer, number);
//            number++;
//        }
//        AutoUtil.newCodeToFile(codeList, FileUtil.createFileTxt(name + "-doc.txt"));
//    }
//
//    /**
//     * 生成一个 url 的文档
//     **/
//    private void generateOne(DocInfo docInfo, boolean isToServer, int number) throws Exception {
//        // todo : 一篇 doc 文档
//        // todo : 这些字段是给 api 使用的
//        List<String> pageContent = new ArrayList<>();
//        String pageTitle = "pageTitle";
//        String catName = "catName";
//        for (String line : templateList) {
//            // 作者
//            line = Order.change(line, Order.author, Constant.author);
//            UrlInfo urlInfo = docInfo.getUrl();
//            List<ParamInfo> param = docInfo.getParam();
//            if (urlInfo != null) {
//                line = Order.change(line, Order.method, urlInfo.getMethod());
//                line = Order.change(line, Order.url, urlInfo.getUrl());
//                pageTitle = urlInfo.getUrl();
//                catName = urlInfo.getUrl();
//                if (StringUtils.containsIgnoreCase(catName, "/")) {
//                    catName = catName.substring(0, catName.indexOf("/"));
//                }
//            }
//            if (Order.check(line, Order.columnNoteWithLength) && param != null) {
//                line = Order.change(line, Order.columnNoteWithLength, columnNoteWithLength(param, urlInfo));
//            }
//            if (Order.check(line, Order.tableJson) && param != null) {
//                line = Order.change(line, Order.tableJson, createJson(param, urlInfo));
//            }
//            line = Order.dateYMDHM(line);
//            codeList.add(line);
//            pageContent.add(line);
//        }
//        if (isToServer) {
//            toShowDoc(catName, pageTitle, String.join("", pageContent), number);
//        }
//    }
//
//
//    public static void init() throws Exception {
//        List<String> dataList = AutoUtil.fileToList(new File(Constant.translate));
//        String k = null;
//        String v = null;
//        for (String s : dataList) {
//            s = StringUtils.trim(s);
//            if (StringUtils.isBlank(s)) {
//                continue;
//            }
//            if (k == null) {
//                k = s;
//            } else {
//                v = s;
//                dataMap.put(k, v);
//                k = null;
//                v = null;
//            }
//        }
//    }
//
//    static {
//        try {
//            init();
//        } catch (Exception e) {
//        }
//    }
//
//    public static String translate(String t) {
//        String result = translate1(t);
//        if (result != null) {
//            return result;
//        }
//        if (t.contains("/")) {
//            List<String> sb = new ArrayList<>();
//            String[] tt = t.split("/");
//            for (String t1 : tt) {
//                String a = translate1(t1);
//                if (a == null) {
//                    a = t1;
//                }
//                sb.add(a);
//            }
//            return String.join("-", sb);
//        }
//        return t;
//    }
//
//    public static String translate1(String t) {
//        for (String s : dataMap.keySet()) {
//            if (StringUtils.equalsIgnoreCase(s, t)) {
//                return dataMap.get(s);
//            }
//        }
//        return null;
//    }
//
//    public static Map<String, Integer> catNameNumber = new HashMap<>();
//    public static int catNameNumberMax = 1;
//
//    public static int getCatNameNumber(String t) {
//        for (String s : catNameNumber.keySet()) {
//            if (StringUtils.equalsIgnoreCase(s, t)) {
//                return catNameNumber.get(s);
//            }
//        }
//        catNameNumberMax++;
//        catNameNumber.put(t, catNameNumberMax);
//        return catNameNumberMax;
//    }
//
//    public static Map<String, Integer> catPageNumber = new HashMap<>();
//
//    public static int getPageNumber(String t) {
//        for (String s : catPageNumber.keySet()) {
//            if (StringUtils.equalsIgnoreCase(s, t)) {
//                Integer n = catPageNumber.get(s);
//                n++;
//                catPageNumber.put(s, n);
//                return n;
//            }
//        }
//        catPageNumber.put(t, 1);
//        return 1;
//    }
//
//    /**
//     * 2021/12/28 写入 showdoc 服务器
//     *
//     * @param catName     类别名
//     * @param pageTitle   title
//     * @param pageContent content
//     * @param number      排序
//     **/
//    private void toShowDoc(String catName, String pageTitle, String pageContent, int number) throws Exception {
//        Response response = null;
//        int catNumber = getCatNameNumber(catName);
//        int pageNumber = getPageNumber(catName);
//        //表单数据参数填入
//        RequestBody body = new FormBody
//                .Builder()
//                .add("api_key", Constant.doc_api_key)
//                .add("api_token", Constant.doc_api_token)
//                .add("cat_name", catNumber + "-" + translate(catName))
//                .add("page_title", catNumber + "." + pageNumber + "-" + translate(pageTitle))
//                .add("page_content", pageContent)
//                .add("s_number", number + "")
//                .build();
//        Request request = new Request.Builder()
//                .url(Constant.doc_url)
//                .post(body)
//                .build();
//        response = okHttpClient.newCall(request).execute();
//        String bodyStr = response.body().string();
//        Map result = new HashMap();
//        result = om.readValue(bodyStr, result.getClass());
//        if (!StringUtils.equalsIgnoreCase(result.get("error_code").toString(), "0")) {
//            log.out("show doc error : " + bodyStr);
//        } else {
//            log.out("show doc success : " + catName + " " + pageTitle);
//        }
//    }
//
//    /**
//     * 创建json数据
//     **/
//    private String createJson(List<ParamInfo> list, UrlInfo urlInfo) throws Exception {
//        if (urlInfo.method.equalsIgnoreCase(GET)) {
//            return "";
//        }
//        StringBuilder result = new StringBuilder();
//        result.append("{\n");
//        boolean isFirst = true;
//        for (int i = 0; i < list.size(); i++) {
//            ParamInfo columninfo = list.get(i);
//            String field = columninfo.getName();
//            if (!isFirst) {
//                result.append(",");
//                result.append("\n");
//            }
//            result.append("\t\"" + field + "\"" + " : " + "\"" + field.toUpperCase() + "\"");
//            isFirst = false;
//        }
//        result.append("\n}");
//        return result.toString();
//    }
//
//    @SafeVarargs
//    public static void columnNoteWithLengthList(List<String> codeList, List<QueryMapFindParam>... list) {
//        for (List<QueryMapFindParam> oneParam : list) {
//            for (QueryMapFindParam f : oneParam) {
//                StringBuffer sb = new StringBuffer();
//                AutoUtil.queryFindParamDocOneLine(sb, "", f.key, f.des, f.type, "", false);
//                codeList.add(sb.toString());
//            }
//        }
//    }
//
//    /**
//     * 创建列的说明数据
//     **/
//    public static String columnNoteWithLength(List<ParamInfo> list, UrlInfo urlInfo) {
//        List<String> codeList = new ArrayList<>();
//        if (list.stream().anyMatch(i -> equalsIgnoreCase(i.name, PAGE_BOUNDS))) {
//            columnNoteWithLengthList(codeList, Table.pageBoundsFindParamStatic);
//        }
//        if (list.stream().anyMatch(i -> equalsIgnoreCase(i.name, QUERY_MAP))) {
//            columnNoteWithLengthList(codeList, Table.idFindParamStatic);
//        }
//        for (int i = 0; i < list.size(); i++) {
//            boolean isGet = equalsIgnoreCase(urlInfo.method, GET);
//            ParamInfo p = list.get(i);
//            ColumnInfo c = p.columnInfo;
//            if (equalsAnyIgnoreCase(p.name, QUERY_MAP, PAGE_BOUNDS)) {
//                continue;
//            }
//            boolean required = false;
//            String max = "";
//            String name = p.name;
//            String des = p.desc;
//            String type = TypeToJavaData.obtainTypeDoc(p.type);
//            if (c != null) {
//                if (isNotEmpty(c.select)) {
//                    StringBuilder sb = new StringBuilder();
//                    c.select.stream().forEach(one -> {
//                        sb.append(String.format("%s : %s</br>", one.key, one.value));
//                    });
//                    des += "</br>" + sb.substring(0, sb.length() - "</br>".length());
//                } else if (c.isConstant) {
//                    des += "</br>" + "常量";
//                }
//                if (isNotEmpty(c.typeDocNote)) {
//                    des += "</br>" + c.typeDocNote;
//                }
//                // 最大长度(整数.小数)
//                if (TypeToJavaData.isString(c.dataTypeJava)) {
//                    max = c.characterMaximumLength;
//                }
//                // Integer
//                if (TypeToJavaData.isInt(c.dataTypeJava)) {
//                    max = c.numericPrecision + ",0";
//                }
//                // BigDecimal
//                if (TypeToJavaData.isBigDecimal(c.dataTypeJava)) {
//                    // 小数部分为空
//                    if (StringUtils.isEmpty(c.numericScale)) {
//                        max = c.numericPrecision;
//                    } else {
//                        // 有小数部分
//                        max = (c.numericPrecisionInt - c.numericScaleInt) + "," + c.numericScale;
//                    }
//                }
//                // 其他情况
//                if (StringUtils.isEmpty(max)) {
//                    if (StringUtils.isNotEmpty(c.characterMaximumLength)) {
//                        max = c.characterMaximumLength;
//                    } else if (StringUtils.isEmpty(c.numericScale)) {
//                        max = c.numericPrecision;
//                    } else {
//                        // 有小数部分
//                        max = c(c.numericPrecision) + d + c.numericScale;
//                    }
//                }
//            } else {
//                if (p.maxLength != null) {
//                    max = str(p.maxLength);
//                } else if (isNotEmpty(p.max)) {
//                    max = p.max;
//                }
//                if (p.minLength != null) {
//                    max += "," + str(p.minLength);
//                } else if (isNotEmpty(p.min)) {
//                    max += "," + p.min;
//                }
//            }
//            if (isNotEmpty(p.defaultValue)) {
//                des += "</br>默认值:" + p.defaultValue;
//            }
//            if (p.required != null && p.required) {
//                required = true;
//            }
//            if (isGet) {
//                required = false;
//                max = "";
//            } else if (equalsIgnoreCase(urlInfo.url.split("/")[1], "delete")) {
//                required = true;
//            }
//            if (isEmpty(des)) {
//                des = " - ";
//            }
//            StringBuffer sb = new StringBuffer();
//            AutoUtil.queryFindParamDocOneLine(sb, "", name, des, type, max, required);
//            codeList.add(sb.toString());
//        }
//        if (list.stream().anyMatch(i -> equalsIgnoreCase(i.name, QUERY_MAP))) {
//            columnNoteWithLengthList(codeList, Table.queryMapFindParamStatic, Table.createUpdateTimeFindParamStatic);
//        }
//        return String.join("\n", codeList);
//    }
//
//}
