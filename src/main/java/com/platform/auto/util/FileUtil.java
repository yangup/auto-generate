package com.platform.auto.util;

import com.platform.auto.config.Config;
import com.platform.auto.jdbc.model.Table;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.platform.auto.config.Config.*;
import static com.platform.auto.config.ConfigEntity.*;

public class FileUtil extends StringUtils {

    public static File createFile(final String fileName) {
        try {
            File file = new File(fileName);
            File parentFile = file.getParentFile();
            List<File> fileList = new ArrayList<>();
            while (!parentFile.exists()) {
                fileList.add(parentFile);
                parentFile = parentFile.getParentFile();
            }
            for (int i = fileList.size() - 1; i > -1; i--) {
                fileList.get(i).mkdir();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean exists(String filePath) {
        return new File(filePath).exists();
    }

    /**
     * 生成其他类型的文件
     **/
    public static File createFileOther(String fileName, String parentName) {
        return createFile(Config.project_auto_path + File.separator + "txt" + File.separator + parentName + File.separator + fileName);
    }

    /**
     * 生成文件 , 统一生成 在 db 文件夹下面的文件
     *
     * @param fileName       : 文件名称, 带有后缀的
     * @param parentFileName : 父文件夹名称
     **/
    public static File createFileDB(String fileName, String parentFileName) throws Exception {
        ProjectPackage projectPackage = getConfig().generateLocation.db != null ? getConfig().generateLocation.db : null;
        if (fileName.endsWith("Mapper.java")) {
            projectPackage = getConfig().generateLocation.mapper;
        }
        if (fileName.endsWith("Entity.java")) {
            projectPackage = getConfig().generateLocation.entity;
        }
        if (fileName.endsWith("Service.java")) {
            projectPackage = getConfig().generateLocation.service;
        }
        if (fileName.endsWith("Controller.java")) {
            projectPackage = getConfig().generateLocation.controller;
        }
        if (fileName.endsWith("SqlProvider.java")) {
            projectPackage = getConfig().generateLocation.sqlProvider;
        }
        // todo : 新的文件创建文件夹, 文件
        return createFile(getJavaFilePath(projectPackage) + (
                StringUtils.isNotEmpty(parentFileName) ? parentFileName + File.separator : ""
        ) + fileName);
    }

    public static String getTableNameJavaLower(final Table table) {
        return CharUtil.firstToLowercase(table.tableNameJava);
    }
}
