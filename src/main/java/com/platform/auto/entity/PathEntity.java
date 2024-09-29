package com.platform.auto.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PathEntity {

    @JsonProperty("project_name")
    public String projectName;

    @JsonProperty("package_name")
    public String packageName;

    /**
     * 生成的文件是否放在 .auto 目录下
     **/
    public String local;
}
