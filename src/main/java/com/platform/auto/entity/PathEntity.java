package com.platform.auto.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PathEntity {

    /**
     * {
     * "local": true,
     * "front_file_path": "system",
     * "path": "front",
     * "absolute_path": "D:\\github\\auto-generate\\.auto\\front",
     * "api_file": ".config\\front\\api.js",
     * "absolute_api_file": "D:\\github\\auto-generate\\.auto\\.config\\front\\api.js",
     * "router_file": ".config\\front\\router.js",
     * "absolute_router_file": "D:\\github\\auto-generate\\.auto\\.config\\front\\router.js",
     * "constant_file": ".config\\front\\constant.js",
     * "absolute_constant_file": "D:\\github\\auto-generate\\.auto\\.config\\front\\constant.js"
     * },
     **/
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

    @JsonProperty("path")
    public String path;

    @JsonProperty("absolute_api_file")
    public String absoluteApiFile;

    @JsonProperty("api_file")
    public String apiFile;

    @JsonProperty("absolute_router_file")
    public String absoluteRouterFile;

    @JsonProperty("router_file")
    public String routerFile;

    @JsonProperty("absolute_constant_file")
    public String absoluteConstantFile;

    @JsonProperty("constant_file")
    public String constantFile;

    // todo : 生成的文件是否放在 .auto 目录下
    public Boolean local;
}
