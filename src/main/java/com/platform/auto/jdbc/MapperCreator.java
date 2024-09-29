package com.platform.auto.jdbc;

import com.platform.auto.jdbc.base.BaseCreator;
import com.platform.auto.jdbc.model.ColumnInfo;
import com.platform.auto.sys.order.Order;
import com.platform.auto.util.AutoUtil;

import java.util.List;

import static com.platform.auto.util.CharUtil.*;

/**
 * <p>
 * yangpu.jdbc.mysql.ModelCreate.java
 * </p>
 * <p>
 * description :
 * </p>
 *
 * @author YangPu
 * @createTime 2016年7月21日 下午3:50:33
 */
public class MapperCreator extends BaseCreator {

    // todo : 换行, 每满5个就换行
    public static final int COUNT = 5;

    public MapperCreator(BaseCreator baseCreator) {
        super(baseCreator);
    }

    @Override
    public void create() {
        List<String> codeTempList = this.copyCodeListAndClear();
        for (String line : codeTempList) {
            if (Order.check(line, Order.sqlFieldRaw)) {
                StringBuffer sb = new StringBuffer();
                int temp_total = 0;
                boolean changeLine = false;
                for (ColumnInfo c : table.columnInfos) {
                    if (Order.checkNeed(c.columnName)) {
                        if (temp_total % COUNT == 0 && temp_total != 0) {
                            changeLine = true;
                        }
                        if (changeLine) {
                            AutoUtil.changeLine(sb);
                            changeLine = false;
                        }
                        sb.append(w).append(c.columnName).append(d);
                        temp_total++;
                    }
                }
                line = line.replace(Order.getOrder(Order.sqlFieldRaw), sb.subSequence(0, sb.length() - 1));
                this.codeList.add(line);
                continue;
            }
            if (Order.check(line, Order.sqlFieldValue)) {
                StringBuffer sb = new StringBuffer();
                int temp_total = 0;
                boolean changeLine = false;
                for (ColumnInfo c : table.columnInfos) {
                    if (Order.checkNeed(c.columnName)) {
                        if (temp_total % COUNT == 0 && temp_total != 0) {
                            changeLine = true;
                        }
                        if (changeLine) {
                            AutoUtil.changeLine(sb);
                            changeLine = false;
                        }
                        sb.append(w).append("#{").append(c.columnNameJava).append("}").append(d);
                        temp_total++;
                    }
                }
                line = line.replace(Order.getOrder(Order.sqlFieldValue), sb.subSequence(0, sb.length() - 1));
                this.codeList.add(line);
                continue;
            }
            this.codeList.add(line);
        }
    }


}


















