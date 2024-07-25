package com.platform.auto.jdbc.model;

/**
 * 在查询接口中的公共数据
 * <p>
 * | all                 | 分页状态下,查询出全部数据  | 否     | 字符串   | 最大20000,相当于page=1,size=20000
 * {
 * "key": "all",
 * "value": "all",
 * "description": "分页状态下的全部数据,最大20000,相当于page=1,size=20000",
 * "disabled": true
 * }
 */
public class QueryMapFindParam {

    // todo : 在查询接口中使用的 key
    // todo : all
    public String key;

    // todo : 在查询接口中使用的 默认 value , 用在 postman 文件中
    // todo : all
    public String value;

    // todo : 类型
    // todo : 字符串
    public String type;

    // todo : 参数描述
    // todo : 分页状态下,查询出全部数据, 相当于page=1,size=20000
    public String des;

    // todo : 是否必填
    public boolean require;

    // todo : 是否默认添加在 url 中
    public boolean defaultToUrl;

    public static QueryMapFindParam of(String key, String value, String type, String des, boolean require, boolean defaultToUrl) {
        QueryMapFindParam f = new QueryMapFindParam();
        f.key = key;
        f.value = value;
        f.type = type;
        f.des = des;
        f.require = require;
        f.defaultToUrl = defaultToUrl;
        return f;
    }


}
