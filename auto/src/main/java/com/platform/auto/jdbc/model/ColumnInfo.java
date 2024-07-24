package com.platform.auto.jdbc.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 从数据库中, 挑出需要的字段
 *
 * @author YangPu
 * @description: <br/>
 * @version: 1<br />
 * @package com.platform.auto.jdbc.mysql.model.ColumnInfo.java
 * @date 2017年1月16日 上午11:51:52
 */
@Data
public class ColumnInfo {

    // todo : 数据库中的字段 , customer_id
    public String columnName;
    // todo : java 中的驼峰命名 customerId
    public String columnNameJava;
    // todo : customer_id
    public String columnNameJavaParamHump;
    // todo : CUSTOMER_ID
    public String columnNameJavaParamHumpUpper;

    // todo : 是文本吗, 前端判断, 如果使用文本的话, 那就要使用 富文本编辑器
    public boolean isText;

    // todo : 是否是本表的id
    public boolean isId;

    // todo : 是否是其他的id
    public boolean isOtherId;
    // todo : 其他表的表明
    public String otherTableName;
    // todo : 其他表的数据
    public Table otherTable;

    // todo : 数据库中的数据类型
    public String dataType;

    // todo : java中的数据类型
    public String dataTypeJava;

    // todo : 用在doc中的类型
    // todo : 例如 : 字符串型
    public String typeDoc;

    // todo : 用在doc中的说明
    // todo : 例如 : 格式 : yyyy </br> 例如 : 2013
    public String typeDocNote;

    public TypeToJavaData typeToJavaData;

    public String tableName;

    /**
     * todo : 查询的时候,需要使用的参数
     * amount : 金额, 需要 amountMin, amountMax
     * FindData = {staticName=AMOUNT, name=amount, operator='='}
     * FindData = {staticName=AMOUNT_MIN, name=amountMin, operator='<='}
     * FindData = {staticName=AMOUNT_MAX, name=amountMax, operator='>'}
     **/
    public List<FindData> findData;

    // todo : 这个字段是否, 可以为空
    public boolean canNULL;
    public String isNullable;
    public String characterMaximumLength;
    public long characterMaximumLengthInt;
    public String numericPrecision;
    public int numericPrecisionInt = -1;
    public String numericScale;
    public int numericScaleInt = -1;

    public String characterSetName;
    public String collationName;
    public String columnType;
    public String columnKey;

    // todo : 字段的注释部分
    public String columnComment;
    public String columnCommentRaw;
    public String tableComment;
    public String tableCommentRaw;

    public String columnDefault;

    // todo : 是否是常量, 常量在查询的时候, 不使用模糊查询
    public boolean isConstant;

    // todo : 这个字段是状态类型的, 需要选择
    public List<SelectData> select;
    // todo : 是多选 , 还是单选
    public boolean isSelectMore;
    // todo : 爱字段的常量类, 例如 : USER_INFO_TYPE
    public String constantName;


    public void afterInit() {
        if (StringUtils.isNotEmpty(numericPrecision)) {
            this.numericPrecisionInt = Integer.parseInt(numericPrecision);
        }
        if (StringUtils.isNotEmpty(numericScale)) {
            this.numericScaleInt = Integer.parseInt(numericScale);
        }
        if (StringUtils.isNotEmpty(this.columnComment)) {
            this.columnComment = this.columnComment.trim();
        }
        if (StringUtils.isNotEmpty(this.tableComment)) {
            this.tableComment = this.tableComment.trim();
        }
        if (StringUtils.isNotEmpty(this.characterMaximumLength)) {
            this.characterMaximumLengthInt = Long.parseLong(characterMaximumLength);
        }
        if (findData == null) {
            findData = new ArrayList<>();
        }
    }


    public static Set<String> FieldSet() {
        Set<String> keys = new HashSet<>();
        Field[] fields = ColumnInfo.class.getDeclaredFields();
        for (Field field : fields) {
            keys.add(field.getName());
        }
        return keys;
    }


}
