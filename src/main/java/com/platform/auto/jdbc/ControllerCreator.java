package com.platform.auto.jdbc;

import com.platform.auto.jdbc.base.BaseCreator;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import com.platform.auto.util.AutoUtil;
import com.platform.auto.util.FileUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.platform.auto.util.AutoUtil.*;
import static com.platform.auto.util.CharUtil.*;
import static com.platform.auto.config.Config.*;

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

    private static final Logger logger = AutoLogger.getLogger(ControllerCreator.class);

    public ControllerCreator(BaseCreator baseCreator) {
        super(baseCreator);
    }

    @Override
    public void create() {
    }
}














