package com.platform.auto.sys.annotation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnnotationUtil {

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
        return replacePlaceholders(template, List.of(msg));
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
