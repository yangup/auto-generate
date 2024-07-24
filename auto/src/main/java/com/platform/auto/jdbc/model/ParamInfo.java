package com.platform.auto.jdbc.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 *
 */
@Data
public class ParamInfo {

    // 参数名
    public String name;
    // 参数描述
    public String desc;
    // 默认值
    public String defaultValue;
    // 是否必填
    public Boolean required;
    // 类型 java.lang.String
    public String type;
    // 最大长度
    public Integer minLength;
    public String min;
    // 最小长度
    public Integer maxLength;
    public String max;
    // 其他信息说明
    public Integer extra;
    // 有可能是 Object 类型的
    public Object detail;

    public ColumnInfo columnInfo;

    public void checkName(String n) {
        if (StringUtils.isEmpty(name)) {
            this.name = n;
        }
    }


}
