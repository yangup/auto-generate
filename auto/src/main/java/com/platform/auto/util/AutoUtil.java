package com.platform.auto.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class AutoUtil extends CharUtil {
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
            log.info(e.getMessage());
            log.info("read file error", file.getName());
        }
        return data;
    }

    /**
     * 读取模板数据
     **/
    public static List<String> readTemplate(String name) {
        return fileToList(new File(name));
    }

    public static void listToFile(File file, List<String> data) throws Exception {
        if (ObjectUtils.isEmpty(data)) {
            return;
        }
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
        for (int i = 0; i < data.size(); i++) {
            writer.write(data.get(i) + n);
        }
        writer.close();
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
                log.info("same code", file.getName());
                return;
            }
            listToFile(file, codeList);
        }
    }
}
