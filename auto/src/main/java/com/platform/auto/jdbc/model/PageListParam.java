package com.platform.auto.jdbc.model;

import lombok.Builder;

/**
 * 在 page 实体类中,需要导入的数据
 */
public class PageListParam {

    // todo : 这个表中的字段
    public String thisTableColumnName;
    public ColumnInfo thisTableColumn;

    // todo : 其他表中的字段
    public String otherTableColumnName;
    public ColumnInfo otherTableColumn;

    // todo : 其他表的 table 信息
    public String otherTableName;
    public Table otherTable;

    // todo : 是需要一个数据,还是多个
    public boolean more;

    public static PageListParam of(String thisTableColumnName, String otherTableColumnName, String otherTableName, boolean more) {
        PageListParam f = new PageListParam();
        f.thisTableColumnName = thisTableColumnName;
        f.otherTableColumnName = otherTableColumnName;
        f.otherTableName = otherTableName;
        f.more = more;
        return f;
    }


}
