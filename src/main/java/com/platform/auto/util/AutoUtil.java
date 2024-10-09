package com.platform.auto.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.platform.auto.config.Config;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.platform.auto.config.Config.project_auto_path;

public class AutoUtil extends CharUtil {

    private static final Logger logger = AutoLogger.getLogger(AutoUtil.class);

    public static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * 读取文件到 list
     **/
    public static List<String> fileToList(File file) {
        List<String> data = new LinkedList<>();
        try {
            if (!file.exists()) {
                return data;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            String line = null;
            while ((line = reader.readLine()) != null) {
                data.add(line);
            }
            reader.close();
        } catch (Exception e) {
            logger.info(e.getMessage());
            logger.info("read file error", file.getName());
        }
        return data;
    }

    /**
     * 读取模板数据
     **/
    public static List<String> readFromResources(String name) {
        List<String> data = new LinkedList<>();
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
        try {
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    data.add(line);
                }
            }
        } catch (Exception e) {
            logger.info("readFromResources error, name: {}", name);
        }
        return data;
    }

    public static List<String> readFromLocal(String name) {
        String path = Config.project_auto_path + "/" + name;
        if (!FileUtil.exists(path)) {
            return new ArrayList<>();
        }
        List<String> data = new LinkedList<>();
        try {
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(path))) {
                // 逐行读取文件内容并添加到List中
                String line;
                while ((line = reader.readLine()) != null) {
                    data.add(line);
                }
            }
        } catch (Exception e) {
            logger.info("readFromLocal error, name: {}", path);
        }
        return data;
    }

    /**
     * 读取 json ， 过滤掉注释
     ***/
    public static String readFromLocalJson(String name) {
        return readFromLocal(name).stream().filter(string ->
                        !string.trim().startsWith("//")
                                && !string.trim().startsWith("#")
                )
                .collect(Collectors.joining(" "));
    }

    public static void listToFile(File file, List<String> data) throws Exception {
        if (ObjectUtils.isEmpty(data)) {
            return;
        }
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
        for (String line : data) {
            writer.write(line + n);
        }
        writer.close();
    }

    public static void listToFile(String path, List<String> data) throws Exception {
        if (!FileUtil.exists(path)) {
            FileUtil.createFile(path);
        }
        listToFile(new File(path), data);
    }

    public static void strToLocalFile(String path, String data) throws Exception {
        listToLocalFile(path, List.of(data));
    }

    public static void objectToLocalFile(String path, Object data) throws Exception {
        strToLocalFile(path, objectToString(data));
    }

    public static String objectToString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    public static void listToLocalFile(String path, List<String> data) throws Exception {
        listToFile(project_auto_path + "/" + path, data);
    }

    /**
     * 在 dataList 中查找以 str 开头的那一行的位置
     **/
    public static int listIndex(List<String> dataList, String str) {
        for (int i = 0; i < dataList.size(); i++) {
            String line = dataList.get(i);
            String l = getLine(line);
            if (l.startsWith(getLine(str))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * list 中 不包含 start , 也不包含 end, 删除
     **/
    public static void listRemove(List<String> dataList, int start, int end) {
        int t = end - start;
        while (t > 0) {
            dataList.remove(start);
            t--;
        }
    }

    /**
     * list 中 包含 containStr 的删除掉
     **/
    public static List<String> listRemove(List<String> dataList, String containStr) {
        List<String> returnList = new ArrayList<>(dataList.size());
        returnList.addAll(dataList);
        for (int i = 0; i < returnList.size(); i++) {
            String s = StringUtils.trim(returnList.get(i));
            if (StringUtils.isBlank(s)) {
                continue;
            }
            if (s.toUpperCase().contains(getLine(containStr.toUpperCase().trim()))) {
                returnList.remove(i);
                i = 0;
            }
        }
        return returnList;
    }

    /**
     * @param nowList  : 目前代码中的情况
     * @param newList  : 新生成的情况
     * @param startStr : 开始的字符串
     * @param endStr   : 结束的字符串
     * @return true : 发生了修改 , 需要重新写入. false : 没有更改, 一切正常
     **/
    public static boolean checkColumn(List<String> nowList, List<String> newList, String startStr, String endStr) {
        int add = 0;
        if (startStr != null && startStr.equals(endStr)) {
            add = 1;
        }
        int startNow = 0;
        if (startStr != null) {
            startNow = listIndex(nowList, startStr);
        }
        int endNow = nowList.size();
        if (endStr != null) {
            endNow = listIndex(nowList, endStr) + add;
        }
        // todo : 没有找到, 就不修改
        if (startNow == -1 || endNow == -1) {
            return false;
        }
        // todo : 当前文件的中间部分
        List<String> insertList = nowList.subList(startNow, endNow);
        int startData = 0;
        if (startStr != null) {
            startData = listIndex(newList, startStr);
        }
        int endData = newList.size();
        if (endStr != null) {
            endData = listIndex(newList, endStr) + add;
        }
        // todo : 新生成文件的中间部分
        List<String> insertDataList = newList.subList(startData, endData);
        // TODO: 一样的, 不需要再次, 生成了
        if (StringUtils.equals(String.join("", insertList), String.join("", insertDataList))) {
            return false;
        }
        listRemove(nowList, startNow, endNow);
        // TODO: 将新生成的文件的部分,添加到现在的文件的中间部分
        nowList.add(startNow, String.join(n, insertDataList));
        return true;
    }

    /**
     * 添加换行代码
     **/
    public static void changeLine(StringBuffer sb) {
        sb.append("\" +\n            \"");
    }

    public static void newCodeToFile(List<String> codeList, File file) throws Exception {
        newCodeToFile(codeList, file, -1, -1);
    }

    public static void newCodeToFile(List<String> codeList, File file, String... exclude) throws Exception {
        newCodeToFile(codeList, file, -1, -1, exclude);
    }

    /**
     * 将新产生的代码放入到文件中
     *
     * @param codeList : 新产生的代码
     * @param file     : 文件
     * @param start    : 要排除比较生成的文件的，开始的位置
     * @param end      : 要排除比较生成的文件的，结束的位置
     * @param exclude  : 要排除比较生成的文件的，以这些行开始
     **/
    public static void newCodeToFile(List<String> codeList, File file, int start, int end, String... exclude) throws Exception {
        if (ObjectUtils.isEmpty(codeList)) {
            logger.info("codeList is empty");
            return;
        }
        List<String> nowList = fileToList(file);
        if (ObjectUtils.isEmpty(nowList)) {
            listToFile(file, codeList);
        } else {
            List<String> codeList1 = codeList.stream().map(CharUtil::getLine).collect(Collectors.toList());
            List<String> nowList1 = nowList.stream().map(CharUtil::getLine).collect(Collectors.toList());
            if (exclude != null) {
                for (String s : exclude) {
                    codeList1 = listRemove(codeList1, s);
                    nowList1 = listRemove(nowList1, s);
                }
            }
            if (start != -1 && end != -1) {
                listRemove(codeList1, start, end);
                listRemove(nowList1, start, end);
            }
            // TODO: 和以前的文件一样, 不需要修改
            if (StringUtils.equals(String.join("", codeList1), String.join("", nowList1))) {
                logger.info("same code: {}", file.getName());
                return;
            }
            listToFile(file, codeList);
        }
    }
}
