package com.platform.auto.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PathEntity {

    @JsonProperty("project_name")
    public String projectName;

    @JsonProperty("package_name")
    public String packageName;

    // todo : 相对路径
    // todo : .auto 下面 的文件
    public String file;

    // todo : 绝对路径
    @JsonProperty("absolute_path")
    public String absolutePath;

    // todo : 生成的文件是否放在 .auto 目录下
    public String local;
}
