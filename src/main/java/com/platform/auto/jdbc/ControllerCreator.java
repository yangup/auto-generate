package com.platform.auto.jdbc;

import com.platform.auto.config.Config;
import com.platform.auto.config.ConfigEntity;
import com.platform.auto.jdbc.base.BaseCreator;
import com.platform.auto.jdbc.model.Table;
import com.platform.auto.util.AutoUtil;
import com.platform.auto.util.FileUtil;

import static com.platform.auto.util.CharUtil.*;

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
public class ControllerCreator extends BaseCreator {
    /**
     * 加载模板
     *
     * @param table
     */
    public ControllerCreator(Table table, ConfigEntity.Info info) throws Exception {
        new ControllerCreator(table, info, false);
    }

    /**
     * @param isList : 是否只把生成的数据, 放入到 list 中, 不做其他的处理
     **/
    public ControllerCreator(Table table, ConfigEntity.Info info, boolean isList) throws Exception {
        super(info, table);
        if (!isList) {
            AutoUtil.newCodeToFile(codeList, FileUtil.createFile(table, info, CONTROLLER_JAVA));
        }
    }

}
