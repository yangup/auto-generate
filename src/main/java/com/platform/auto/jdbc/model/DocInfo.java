package com.platform.auto.jdbc.model;

import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
public class DocInfo {

    public UrlInfo url;

    public List<ParamInfo> param;


}
