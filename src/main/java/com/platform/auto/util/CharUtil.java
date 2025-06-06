package com.platform.auto.util;

import com.platform.auto.config.Config;
import com.platform.auto.jdbc.model.ColumnInfo;
import com.platform.auto.jdbc.model.TypeToJavaData;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * yangpu.util.CharUtil.java
 * </p>
 * <p>
 * description : charUtil
 * </p>
 *
 * @author YangPu
 * @createTime 2016年7月21日 上午10:29:53
 */
public class CharUtil extends ObjectUtils {

    public static String j = "+";
    public static String y = "-";
    public static String s = "\"";
    public static String s1 = "'";
    public static String m = ":";
    public static String d = ",";
    public static String w = " ";
    public static String a = ".";
    public static String n = "\n";
    public static String n2 = n + n;
    public static String n3 = n + n + n;
    public static String t = w + w + w + w;
    public static String t2 = t + t;
    public static String t3 = t + t + t;
    public static String PAGE_BOUNDS = "PageBounds";
    public static String QUERY_MAP = "QueryMap";
    public static String GET = "GET";
    public static String POST = "POST";
    public static String _FILTER = ".filter";
    public static String CONSTANT = "constant";
    public static String DB = "db";

    // 把一个字符串的第一个字母大写、效率是最高的、
    public static String firstToUppercase(String fieldName) {
        if (Character.isUpperCase(fieldName.charAt(0))) {
            return fieldName;
        }
        byte[] items = fieldName.getBytes();
        items[0] = (byte) ((char) items[0] - 'a' + 'A');
        return new String(items);
    }

    public static String firstToLowercase(String fieldName) {
        if (Character.isLowerCase(fieldName.charAt(0))) {
            return fieldName;
        }
        byte[] items = fieldName.getBytes();
        items[0] = (byte) ((char) items[0] - 'A' + 'a');
        return new String(items);
    }

    public static String param(String str) {
        return firstToLowercase(str);
    }

    /**
     * 将下划线命名的字符串转换为 Java 驼峰命名（小驼峰）
     * 例如：user_name -> userName
     *
     * @param str 下划线格式字符串
     * @return 驼峰格式字符串
     */
    public static String toJava(String str) {
        if (StringUtils.isBlank(str)) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        boolean upperCaseNext = false;
        for (char c : str.toLowerCase().toCharArray()) {
            if (c == '_') {
                upperCaseNext = true;
            } else {
                result.append(upperCaseNext ? Character.toUpperCase(c) : c);
                upperCaseNext = false;
            }
        }
        return result.toString();
    }


    public static boolean equalsIgnoreCase(CharSequence cs1, CharSequence cs2) {
        return StringUtils.equalsIgnoreCase(cs1, cs2);
    }

    public static boolean equalsAnyIgnoreCase(CharSequence string, CharSequence... searchStrings) {
        return StringUtils.equalsAnyIgnoreCase(string, searchStrings);
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !StringUtils.isBlank(cs);
    }

    public static boolean isNotNull(Object o) {
        return o != null;
    }

    public static boolean isTrue(Boolean bool) {
        return bool != null && bool;
    }

    public static boolean isBlank(CharSequence cs) {
        return StringUtils.isBlank(cs);
    }

    public static boolean contains(CharSequence seq, CharSequence searchSeq) {
        return StringUtils.contains(seq, searchSeq);
    }

    public static boolean endsWithIgnoreCase(CharSequence str, CharSequence suffix) {
        return StringUtils.endsWithIgnoreCase(str, suffix);
    }

    /**
     * 将 驼峰命名的数据, 转化为下划线的数据
     **/
    public static String humpToLine(String str) {
        return str.replaceAll("[A-Z]", "_$0").toLowerCase();
    }

    public static boolean containHanzi(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        if (StringUtils.isNotBlank(str)) {
            Matcher m = p.matcher(str);
            if (m.find()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAllHanzi(String name) {
        int n = 0;
        for (int i = 0; i < name.length(); i++) {
            n = (int) name.charAt(i);
            if (!(19968 <= n && n < 40869)) {
                return false;
            }
        }
        return true;
    }

    // TODO: 处理掉前民的开头部分
    public static String removePrefix(String tableName) {
        if (isEmpty(Config.getConfig().getRemovePrefix())) {
            return tableName;
        }
        String s = tableName.toUpperCase();
        for (String p : Config.getConfig().getRemovePrefix()) {
            if (s.startsWith(p)) {
                return tableName.substring(p.length());
            }
        }
        return tableName;
    }

    /**
     * 在右边补充, 缺省的空格
     **/
    public static String getWhitespaceRight(int length, String str) {
        StringBuilder result = new StringBuilder();
        result.append(str + getWhitespace(length - strLength(str)));
        return result.toString();
    }

    public static String getZeroRight(int length, String str) {
        StringBuilder result = new StringBuilder();
        result.append(str + getZero(length - strLength(str)));
        return result.toString();
    }

    public static String getWhitespace(int length) {
        StringBuilder result = new StringBuilder();
        if (length < 1) {
            return result.toString();
        }
        for (int i = 0; i < length; i++) {
            result.append(" ");
        }
        return result.toString();
    }

    public static String getZero(int length) {
        return length > 0 ? "0".repeat(length) : "";
    }

    public static String get9(int length) {
        return length > 0 ? "9".repeat(length) : "";
    }

    /**
     * 字符串的长度 , 汉字算 2 个
     **/
    public static int strLength(String ss) {
        return ss.length() + hzCount(ss);
    }

    /**
     * 汉字个数
     **/
    public static int hzCount(String ss) {
        String regex = "[\u4e00-\u9fa5]";
        return ss.length() - ss.replaceAll(regex, "").length();
    }

    /**
     * str数据的转化<br>
     * null -> ""<br>
     **/
    public static String c(Object str) {
        if (ObjectUtils.isEmpty(str)) {
            return "";
        }
        return str.toString();
    }

    public static String str(Object str) {
        if (ObjectUtils.isEmpty(str)) {
            return "";
        }
        return str.toString();
    }

    /**
     * 判断是否是 create time
     **/
    public static boolean isCreateTime(String str) {
        return isEqual(str, "createTime");
    }

    public static boolean isUpdateTime(String str) {
        return isEqual(str, "updateTime");
    }

    public static boolean isID(String str) {
        return isEqual(str, "id");
    }

    public static boolean isCreateTimeUpdateTime(String str) {
        return isCreateTime(str) || isUpdateTime(str);
    }

    public static boolean isIdCreateTimeUpdateTime(String str) {
        if (StringUtils.equalsIgnoreCase("id", str)
                || StringUtils.equalsIgnoreCase("createTime", str)
                || StringUtils.equalsIgnoreCase("updateTime", str)) {
            return true;
        }
        return false;
    }

    public static boolean isNotIdCreateTimeUpdateTime(String data) {
        return !isIdCreateTimeUpdateTime(data);
    }

    public static boolean isNotCreateTimeUpdateTime(String data) {
        return !isCreateTimeUpdateTime(data);
    }

    public static boolean isEqual(String str, final String s) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        if (StringUtils.equalsIgnoreCase(str, s)) {
            return true;
        }
        if (StringUtils.equalsIgnoreCase(toJava(str), s)) {
            return true;
        }
        return false;
    }

    /**
     * json数据的格式化
     **/
    public static String formatJson(String json) {
        if (StringUtils.isEmpty(json)) {
            return json;
        }
        return json.replace("{", "{\n\t").replace("}", "\n}\n").replace(",", ",\n\t");
    }

    public static String getLine(String line) {
        Pattern pattern = Pattern.compile("[\t\r\n + \",]");
        Matcher matcher = pattern.matcher(line);
        return matcher.replaceAll("");
    }

    public static boolean isMinMax(ColumnInfo c) {
        if (TypeToJavaData.isInt(c.dataTypeJava) || TypeToJavaData.isBigDecimal(c.dataTypeJava)) {
            return true;
        }
        return false;
    }

    public static boolean isFromTo(ColumnInfo c) {
        if (TypeToJavaData.isDateTime(c.dataTypeJava)
                || TypeToJavaData.isDate(c.dataTypeJava)
                || TypeToJavaData.isTime(c.dataTypeJava)) {
            return true;
        }
        return false;
    }

}
