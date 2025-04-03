package com.platform.auto.sys.annotation;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ParamValidationAnnotation {

    // 只能为true
    // #验证对象是否为null-限制只能为null
    // @AssertTrue(message = "@AssertTrue一定为true不能为空")
    TRUE("@AssertTrue(message = {msg})"),

    // 只能为false
    // #验证对象是否为null-限制只能为null
    // @AssertFalse(message = "@AssertFalse一定为false不能为空")
    FALSE("@AssertFalse(message = {msg})"),

    // #验证 Date 和 Calendar 对象是否在当前时间之前-限制必须是一个过去的日期
    // @Past(message = "@Past一定为过去的时间不能为空")
    PAST("@Past(message = {msg}"),

    // #验证 Date 和 Calendar 对象是否在当前时间之后-限制必须是一个将来的日期
    // @Future(message = "@Future一定为未来的时间不能为空")
    FUTURE("@Future(message = {msg})"),

    // #验证对象（Array,Collection,Map,String）长度是否在给定的范围之内-限制字符长度必须在min到max之间
    // @Size(min = 2, max = 4, message = "@Size长度,2-4不能为空")
    SIZE("@Size(min = {min}, max = {max}, message = {msg})"),

    // #验证字符串的长度是否在给定的范围之内，包含两端
    // @Length(min = 2, max = 4, message = "@Length长度2-5不能为空")
    LENGTH("@Length(min = {min}, max = {max}, message = {msg})"),

    // #验证对象是否为null-限制只能为null
    // @Null(message = "@Null为空不能为空")
    // 值只能为null或者不传,传""都不行
    // 例如 : {"my00":""}--错误
    // 例如 : {"my00":null}或{}--正确
    NULL("@Null(message = {msg})"),

    // #验证对象是否不为null,无法查检长度为0的字符串-限制必须不为null
    // @NotNull(message = "@NotNull不能为空不能为空")
    // String 类型
    // 例如 : {"my01":null}--错误
    // 例如 : {"my01":""}--正确
    // 例如 : {"my01":" "}--正确
    // 例如 : {"my01":"12"}--正确
    // BigInteger 类型
    // 例如 : {"my01":null}--错误
    // 例如 : {"my01":""}--错误
    // 例如 : {"my01":" "}--错误
    // 例如 : {"my01":" 1 2 "}--错误
    // 例如 : {"my01":"12"}--正确
    // 例如 : {"my01":" 12 "}--正确
    NOT_NULL("@NotNull(message = {msg})"),

    // #检查约束元素是否为NULL或者是EMPTY.
    // @NotEmpty(message = "@NotEmpty不能为空不能为空")
    // 例如 : {"my01":""}--错误
    // 例如 : {"my01":" "}--正确
    // 例如 : {"my01":"12"}--正确
    NOT_EMPTY("@NotEmpty(message = {msg})"),

    // #检查约束字符串是不是Null还有被Trim的长度是否大于0,只对字符串,且会去掉前后空格.
    // @NotBlank(message = "@NotBlank不能为空不能为空")
    // 例如 : {"my01":""}--错误
    // 例如 : {"my01":" "}--错误
    // 例如 : {"my01":"12"}--正确
    NOT_BLANK("@NotBlank(message = {msg})"),


    // #数值检查 : 建议使用在Stirng,Integer类型，不建议使用在int类型上，因为表单值为“”时无法转换为int，但可以转换为Stirng为"",Integer为null
    // #验证 Number 和 String 对象是否大等于指定的值-限制必须为一个不大于指定值的数字
    // @Min(value = 2L, message = "@Min-2-最小值不能为空")
    MIN("@Min(value = {val}L, message = {msg})"),

    // #验证 Number 和 String 对象是否小等于指定的值-限制必须为一个不小于指定值的数字
    // @Max(value = 4L, message = "@Max-4-最大值不能为空")
    MAX("@Max(value = {val}L, message = {msg})"),

    // #被标注的值必须不大于约束中指定的最大值.
    // 这个约束的参数是一个通过BigDecimal定义的最大值的字符串表示.小数存在精度-限制必须为一个不大于指定值的数字
    // @DecimalMin(value = "2", message = "@DecimalMin-2-最小值不能为空")
    // 最大值--int : 整数部分--fra : 小数部分
    DECIMAL_MAX("@DecimalMax(value = \"{int}.{fra}\", inclusive = false, message = {msg})"),

    // #被标注的值必须不小于约束中指定的最小值.
    // 这个约束的参数是一个通过BigDecimal定义的最小值的字符串表示.小数存在精度-限制必须为一个不小于指定值的数字<br>
    // @DecimalMax(value = "4", message = "@DecimalMax-4-最大值不能为空")
    DECIMAL_MIN("@DecimalMin(value = \"{int}\", inclusive = false, message = {msg})"),

    // #验证 Number 和 String 的构成是否合法
    // #@Digits(integer=,fraction=)
    // 验证字符串是否是符合指定格式的数字，interger指定整数精度，fraction指定小数精度。-限制必须为一个小数，且整数部分的位数不能超过integer，小数部分的位数不能超过fraction
    // @Digits(integer = 5, fraction = 2, message = "@Digits-5,2-值不能为空")
    DIGITS("@Digits(integer = {int}, fraction = {fra}, message = {msg})"),

    // #验证 String 对象是否符合正则表达式的规则-限制必须符合指定的正则表达式
    // @Pattern(regexp = "\\d{8}", message = "@Pattern-正则表达式不能为空")
    PATTERN("@Pattern(regexp = {reg}, message = {msg})");


    private String value;

    private ParamValidationAnnotation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    /**
     * 更换 模板中的 msg
     **/
    public String replaceInfo(String msg) {
        return AnnotationUtil.replacePlaceholders(this.value, msg);
    }

    /**
     * 更换 模板中的 min, max, msg
     **/
    public String replaceInfo(int min, String max, String msg) {
        // 2147483647
        // 4294967295
        int maxInt = 0;
        if (Long.parseLong(max) > Integer.MAX_VALUE) {
            maxInt = Integer.MAX_VALUE;
        } else {
            maxInt = Integer.parseInt(max);
        }
        return AnnotationUtil.replacePlaceholders(this.value, List.of(min, maxInt, msg));
    }

    /**
     * 更换 模板中的 数字, msg
     **/
    public String replaceInfo(String msg, Object... is) {
        List<Object> data = new ArrayList<>();
        Arrays.stream(is).forEach(data::add);
        data.add(msg);
        return AnnotationUtil.replacePlaceholders(this.value, data);
    }

}
