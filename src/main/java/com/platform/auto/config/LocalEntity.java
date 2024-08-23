package com.platform.auto.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 *
 */

@Data
public class LocalEntity {

    public Date time;

    public String filterTableNameText;

    public String selectedDbName;

    // 数据处理好了
    public List<DbEntity> dbInfoList;

    @Data
    public static class TableEntity {

        public String tableSchema;
        public String tableName;
    }


}
