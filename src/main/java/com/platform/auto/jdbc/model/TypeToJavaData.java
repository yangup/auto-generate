package com.platform.auto.jdbc.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库中的类型, 向 java 中的类型转变的处理类
 */
@Data
public class TypeToJavaData {

    public static List<TypeToJavaData> fieldMapping = new ArrayList<>();

    static {
        init();
    }

    // todo : 数据库中的数据类型
    // todo : 例如 : {"enum", "binary", "blob", "char", "enum", "fixed", "longblob", "tinyblob", "tinytext", "varbinary", "varchar", "longtext", "mediumblob", "set", "text"}
    public List<String> type;

    // todo : java中的数据类型
    // todo : 例如 : String
    public String typeJava;

    // todo : 用在doc中的类型
    // todo : 例如 : 字符串型
    public String typeDoc;

    // todo : 用在doc中的说明
    // todo : 例如 : 格式 : yyyy </br> 例如 : 2013
    public String typeDocNote;

    // todo : 在生成 json 数据的时候, 这种数据类型, 需要用到 冒号
    // todo : 例如 : true
    public boolean jsonNeedColon;

    // todo : 在引入这个类型的时候, 需要引入的 java 包
    // todo : 例如 : true
    public String javaPackage;

    public static TypeToJavaData of(String[] types, String typeJava, String typeDoc, String typeDocNote, boolean jsonNeedColon, String javaPackage) {
        TypeToJavaData f = new TypeToJavaData();
        f.type = List.of(types);
        f.typeJava = typeJava;
        f.typeDoc = typeDoc;
        f.typeDocNote = typeDocNote;
        f.jsonNeedColon = jsonNeedColon;
        f.javaPackage = javaPackage;
        return f;
    }

    public static TypeToJavaData of(String[] types, String typeJava, String typeDoc, String typeDocNote, boolean jsonNeedColon) {
        return of(types, typeJava, typeDoc, typeDocNote, jsonNeedColon, null);
    }

    public static void init() {
        fieldMapping = initFieldMapping();
    }

    /**
     * 初始化映射关系
     **/
    public static List<TypeToJavaData> initFieldMapping() {
        List<TypeToJavaData> fieldMapping = new ArrayList<>();

        fieldMapping.add(TypeToJavaData.of(
                new String[]{"bool", "boolean"},
                Boolean.class.getSimpleName(),
                "布尔型数据", "只能填写 true 或 false ", false));

        fieldMapping.add(TypeToJavaData.of(
                new String[]{"bit", "tinyint", "smallint", "int", "integer", "mediummint", "int4"},
                Integer.class.getSimpleName(),
                "整数", "", false));
        fieldMapping.add(TypeToJavaData.of(
                new String[]{""},
                BigInteger.class.getSimpleName(),
                "整数", "", false, BigInteger.class.getName()));

        fieldMapping.add(TypeToJavaData.of(
                new String[]{"bigint", "int8"},
                Long.class.getSimpleName(),
                "整数", "", false));

        fieldMapping.add(TypeToJavaData.of(
                new String[]{"float", "real"},
                BigDecimal.class.getSimpleName(),
                "浮点数", "", false, BigDecimal.class.getName()));

        fieldMapping.add(TypeToJavaData.of(
                new String[]{"dec", "decimal", "double", "dec", "decimal", "double", "numeric"},
                BigDecimal.class.getSimpleName(),
                "浮点数", "", false, BigDecimal.class.getName()));

        fieldMapping.add(TypeToJavaData.of(
                new String[]{"enum", "binary", "blob", "char", "enum", "fixed", "longblob", "tinyblob", "tinytext", "varbinary", "varchar", "longtext", "mediumblob", "set", "text", "json"},
                String.class.getSimpleName(),
                "字符串", "", true));

        fieldMapping.add(TypeToJavaData.of(
                new String[]{"timestamp", "timestamptz", "datetime"},
                LocalDateTime.class.getSimpleName(),
                "日期时间", "格式 : yyyy-MM-dd HH:mm:ss </br> 例如 : 2013-05-09 12:13:14", true, LocalDateTime.class.getName()));

        fieldMapping.add(TypeToJavaData.of(
                new String[]{"date"},
                LocalDate.class.getSimpleName(),
                "日期", "格式 : yyyy-MM-dd </br> 例如 : 2013-05-17", true, LocalDate.class.getName()));

        fieldMapping.add(TypeToJavaData.of(
                new String[]{"time"},
                LocalTime.class.getSimpleName(),
                "时间", "格式 : HH:mm:ss </br> 例如 : 12:13:14", true, LocalTime.class.getName()));

        fieldMapping.add(TypeToJavaData.of(
                new String[]{"year"},
                Year.class.getSimpleName(),
                "年", "格式 : yyyy </br> 例如 : 2013", true, Year.class.getName()));

        fieldMapping.add(TypeToJavaData.of(
                new String[]{"byte"},
                Byte.class.getSimpleName(),
                "字节类型", "只能填写 1 或 0", false, Byte.class.getName()));

        return fieldMapping;

    }

    public static boolean isSame(String a, String... b) {
        if (StringUtils.equalsAnyIgnoreCase(a, b)) {
            return true;
        }
        return false;
    }

    public static boolean isSame(String a, Class c) {
        if (StringUtils.equalsAnyIgnoreCase(a, c.getName(), c.getSimpleName())) {
            return true;
        }
        return false;
    }

    public static boolean isSame(String a, Class c, String... b) {
        if (StringUtils.equalsAnyIgnoreCase(a, c.getName(), c.getSimpleName())
                || StringUtils.equalsAnyIgnoreCase(a, b)) {
            return true;
        }
        return false;
    }

    public static boolean isInt(String type) {
        return isSame(type, Integer.class.getSimpleName(), "int");
    }

    public static boolean isLong(String type) {
        return isSame(type, Long.class, "long");
    }

    public static boolean isString(String type) {
        return isSame(type, String.class);
    }

    public static boolean isDate(String type) {
        return isSame(type, LocalDate.class);
    }

    public static boolean isTime(String type) {
        return isSame(type, LocalTime.class);
    }

    public static boolean isDateTime(String type) {
        return isSame(type, LocalDateTime.class);
    }

    public static boolean isBigDecimal(String type) {
        return isSame(type, BigDecimal.class);
    }

    public static boolean isBoolean(String type) {
        return isSame(type, Boolean.class);
    }

    public static TypeToJavaData obtainTypeToJavaData(String type) {
        for (TypeToJavaData f : fieldMapping) {
            if (f.type.stream().anyMatch(t -> StringUtils.equalsIgnoreCase(t, type))) {
                return f;
            }
        }
        return null;
    }

    public static TypeToJavaData obtainByJavaType(String type) {
        for (TypeToJavaData f : fieldMapping) {
            if (StringUtils.equalsIgnoreCase(f.typeJava, type)) {
                return f;
            }
        }
        return null;
    }


    public static String obtainTypeJava(String type) {
        for (TypeToJavaData f : fieldMapping) {
            if (f.type.stream().anyMatch(t -> StringUtils.equalsIgnoreCase(t, type))) {
                return f.typeJava;
            }
        }
        return type;
    }

    /**
     * 根据 java 类型获得 doc
     *
     * @param type : java.lang.String
     **/
    public static String obtainTypeDoc(String type) {
        for (TypeToJavaData f : fieldMapping) {
            if (StringUtils.equalsIgnoreCase(f.typeJava, type)) {
                return f.typeDoc;
            }
        }
        for (TypeToJavaData f : fieldMapping) {
            if (StringUtils.contains(type, ".")
                    && StringUtils.equalsIgnoreCase(f.typeJava, type.substring(type.lastIndexOf(".") + 1))) {
                return f.typeDoc;
            }
        }
        return "";
    }

    /***
     * 在文档中是否需要冒号
     * **/
    public static boolean needM(String typeJava) {
        for (TypeToJavaData f : fieldMapping) {
            if (StringUtils.equalsIgnoreCase(typeJava, f.typeJava)) {
                return f.jsonNeedColon;
            }
        }
        for (TypeToJavaData f : fieldMapping) {
            if (StringUtils.endsWith(typeJava, "." + f.typeJava)) {
                return f.jsonNeedColon;
            }
        }
        return false;
    }

    public static String obtainJavaDocNote(String type) {
        for (TypeToJavaData f : fieldMapping) {
            if (StringUtils.equalsIgnoreCase(f.typeJava, type)) {
                return f.typeDocNote;
            }
        }
        for (TypeToJavaData f : fieldMapping) {
            if (StringUtils.contains(type, ".")
                    && StringUtils.equalsIgnoreCase(f.typeJava, type.substring(type.lastIndexOf(".") + 1))) {
                return f.typeDocNote;
            }
        }
        return type;
    }


}
