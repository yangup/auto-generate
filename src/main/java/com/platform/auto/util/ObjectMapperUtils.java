package com.platform.auto.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


/**
 *
 **/
public class ObjectMapperUtils {

    public static ObjectMapper getObjectMapper() {
        ObjectMapper om = new ObjectMapper();
//        om.setDateFormat(new CustomDateFormat());
        // json是否允许属性名没有引号 ，默认是false
        om.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // json是否允许属性名为单引号 ，默认是false
        om.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 确定Map条目在序列化之前是否首先按键排序的功能：如果启用，则在必要时执行额外的排序步骤（SortedMaps不需要），如果禁用，则不需要额外的排序
        om.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        // 定义使用的默认属性序列化顺序的功能
//        om.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        // 允许为底层生成器启用（或禁用）缩进的功能
//        om.configure(SerializationFeature.INDENT_OUTPUT, true);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return om;
    }

    public static ObjectMapper getMapper() {
        ObjectMapper om = getObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(om.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);

        return om;
    }

}
