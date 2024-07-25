package com.platform.auto.sys.annotation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnnotationUtil {

//    public static String change(String str, String... msgs) {
//        if (StringUtils.isEmpty(str) || msgs == null) {
//            return str;
//        }
//        int s = -1, e = -1;
//        for (int i = 0; i < msgs.length; i++) {
//            String msg = msgs[i];
//            if ((s = str.indexOf("{")) != -1 && (e = str.indexOf("}")) != -1) {
//                str = str.replace(str.substring(s, e + 1), CharUtil.s + msg + CharUtil.s);
//            }
//        }
//        return str;
//    }
//
//    // @Length(min = {min}, max = {max}, message = {msg})
//    public static String changeInt(String str, String msg, int... is) {
//        if (StringUtils.isEmpty(str) || msg == null) {
//            return str;
//        }
//        int s = -1, e = -1;
//        // 替换数字
//        for (int i = 0; i < is.length; i++) {
//            int ii = is[i];
//            if ((s = str.indexOf("{")) != -1 && (e = str.indexOf("}")) != -1) {
//                str = str.replace(str.substring(s, e + 1), "" + ii);
//            }
//        }
//        // 替换msg
//        if ((s = str.indexOf("{")) != -1 && (e = str.indexOf("}")) != -1) {
//            str = str.replace(str.substring(s, e + 1), CharUtil.s + msg + CharUtil.s);
//        }
//        return str;
//    }
//
//    public static String changeIntNumber(String str, String msg, int... is) {
//        if (StringUtils.isEmpty(str) || msg == null) {
//            return str;
//        }
//        int s = -1, e = -1;
//        // 替换数字
//        for (int i = 0; i < is.length; i++) {
//            int ii = is[i];
//            if ((s = str.indexOf("{")) != -1 && (e = str.indexOf("}")) != -1) {
//                str = str.replace(str.substring(s, e + 1), getNumberByLength(ii));
//            }
//        }
//        // 替换msg
//        if ((s = str.indexOf("{")) != -1 && (e = str.indexOf("}")) != -1) {
//            str = str.replace(str.substring(s, e + 1), CharUtil.s + msg + CharUtil.s);
//        }
//        return str;
//    }
//
//    public static String changeIntNumberMax(String str, String msg, int... is) {
//        if (StringUtils.isEmpty(str) || msg == null) {
//            return str;
//        }
//        int s = -1, e = -1;
//        // 替换数字
//        for (int i = 0; i < is.length; i++) {
//            int ii = is[i];
//            if ((s = str.indexOf("{")) != -1 && (e = str.indexOf("}")) != -1) {
//                str = str.replace(str.substring(s, e + 1), getNumberByLength(ii));
//            }
//        }
//        // 替换msg
//        if ((s = str.indexOf("{")) != -1 && (e = str.indexOf("}")) != -1) {
//            str = str.replace(str.substring(s, e + 1), CharUtil.s + msg + CharUtil.s);
//        }
//        return str;
//    }
//
//    public static String changeIntNumberMin(String str, String msg, int... is) {
//        if (StringUtils.isEmpty(str) || msg == null) {
//            return str;
//        }
//        int s = -1, e = -1;
//        // 替换数字
//        for (int i = 0; i < is.length; i++) {
//            int ii = is[i];
//            if ((s = str.indexOf("{")) != -1 && (e = str.indexOf("}")) != -1) {
//                str = str.replace(str.substring(s, e + 1), getNumberByLength(ii));
//            }
//        }
//        // 替换msg
//        if ((s = str.indexOf("{")) != -1 && (e = str.indexOf("}")) != -1) {
//            str = str.replace(str.substring(s, e + 1), CharUtil.s + msg + CharUtil.s);
//        }
//        return str;
//    }

    /**
     * 获得i个9
     **/
    public static String getNumberByLength(long i) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < i; j++) {
            sb.append("9");
        }
        if (i <= 0) {
            sb.append("0");
        }
        return sb.toString();
    }

    public static String replacePlaceholders(String template, String msg) {
        return AnnotationUtil.replacePlaceholders(template, List.of(msg));
    }

    /**
     * 将 @DecimalMax(value = "{int}.{fra}", message = {msg}) 字符串的占位符, 替换成数据
     **/
    public static String replacePlaceholders(String template, List<Object> replacementList) {
        Pattern pattern = Pattern.compile("\\{(\\w+)}");
        Matcher matcher = pattern.matcher(template);
        StringBuilder result = new StringBuilder();
        int index = 0;
        while (matcher.find()) {
            Object replacement = replacementList.get(index);
            matcher.appendReplacement(result, Matcher.quoteReplacement(
                    replacement instanceof String ? String.format("\"%s\"", replacement) : replacement.toString()));
            index++;
        }
        matcher.appendTail(result);
        return result.toString();
    }


}
