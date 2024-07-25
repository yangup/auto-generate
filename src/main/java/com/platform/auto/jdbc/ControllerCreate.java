package com.platform.auto.jdbc;

import com.platform.auto.jdbc.base.BaseCreate;
import com.platform.auto.jdbc.model.Table;
import com.platform.auto.util.AutoUtil;
import com.platform.auto.util.FileUtil;

/**
 * <p>
 * yangpu.jdbc.mysql.ControllerCreate.java
 * </p>
 * <p>
 * description :
 * </p>
 *
 * @author YangPu
 * @createTime 2016年7月21日 下午3:50:33
 */
public class ControllerCreate extends BaseCreate {
    /**
     * 加载模板
     *
     * @param table
     */
    public ControllerCreate(Table table) throws Exception {
        new ControllerCreate(table, false);
    }

    /**
     * @param isList : 是否只把生成的数据, 放入到 list 中, 不做其他的处理
     **/
    public ControllerCreate(Table table, boolean isList) throws Exception {
        super(Constant.controller, table);
        if (!isList) {
            AutoUtil.newCodeToFile(codeList, FileUtil.createFileController(table.tableNameJava + "Controller.java"));
        }
    }

}