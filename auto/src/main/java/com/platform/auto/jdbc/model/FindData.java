package com.platform.auto.jdbc.model;

/**
 *
 */
public class FindData {

    // todo : 在 java 中导入的静态类的变量名
    public String staticName;
    // todo : 在 java 中使用的参数名
    public String name;
    // todo : 操作符 >, = , <
    public String operator;
    // todo : 注释
    public String comment;
    // todo : 是否是数字
    public boolean isNumber;
    // todo : 是否是常量, 不做 like 查询
    public boolean isConstant;

    public static FindData of(String staticName, String name, String operator, String comment, boolean isNumber, boolean isConstant) {
        FindData f = new FindData();
        f.staticName = staticName;
        f.name = name;
        f.operator = operator;
        f.comment = comment;
        f.isNumber = isNumber;
        f.isConstant = isConstant;
        return f;
    }

    public static FindData of(String staticName, String name, String operator, String comment, boolean isNumber) {
        return of(staticName, name, operator, comment, isNumber, false);
    }

    public static FindData of(String staticName, String name, String operator, String comment) {
        return of(staticName, name, operator, comment, false, false);
    }

}
