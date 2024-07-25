package com.platform.auto.util;

import com.platform.auto.jdbc.Constant;
import com.platform.auto.jdbc.model.Table;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * 生成其他类型的文件
     **/
    public static File createFileOther(String fileName, String parentName) {
        return createFile(Constant.path_no_java + File.separator + parentName + File.separator + fileName);
    }

    public static File createFileController(String fileName) {
        return createFile(Constant.path_controller + fileName);
    }

    /**
     * 生成文件 , 统一生成 在 db 文件夹下面的文件
     *
     * @param fileName       : 文件名称, 带有后缀的
     * @param parentFileName : 父文件夹名称
     **/
    public static File createFileDB(String fileName, String parentFileName) {
        // todo : 新的文件创建文件夹, 文件
        return createFile(Constant.path + parentFileName + File.separator + fileName);
    }

    public static File getFile(final Table table, final String suffix) {
        File file = getFilePath(table, suffix + ".java", Constant.path);
        if (file != null) {
            return file;
        }
        file = getFilePath(table, suffix + ".java", Constant.path_controller);
        if (file != null) {
            return file;
        }
        return getFilePath(table, suffix + ".vue", Constant.path_front + "src\\views\\");
    }

    public static File getFilePath(final Table table, final String suffix, final String path) {
        return getFile(table.tableNameJava + suffix, path);
    }

    /**
     * 在系统中找到 这个文件
     **/
    public static File getFile(final String fileName, String findPath) {
        File file = new File(findPath);
        if (file.exists()) {
            if (checkFile(fileName, file)) {
                return file;
            }
            File r = getFile(fileName, file.listFiles());
            if (r != null) {
                return r;
            }
        }
        return null;
    }

    public static File getFile(final String fileName, File[] files) {
        if (files == null || files.length == 0) {
            return null;
        }
        for (File f : files) {
            if (checkFile(fileName, f)) {
                return f;
            }
        }
        for (File f : files) {
            File r = getFile(fileName, f.listFiles());
            if (r != null) {
                return r;
            }
        }
        return null;
    }

    public static boolean checkFileSuffix(final String suffix, final File file) {
        if (file.isDirectory()) {
            return false;
        }
        if (file.getName().toUpperCase().endsWith(suffix.toUpperCase())) {
            return true;
        }
        return false;
    }

    // TODO: 找到所有的 .java 文件
    public static List<File> getFileWithSuffix(final String suffix, String findPath) {
        List<File> result = new ArrayList<>();
        File file = new File(findPath);
        if (file.exists()) {
            if (checkFileSuffix(suffix, file)) {
                result.add(file);
            }
            getFileWithSuffix(suffix, file.listFiles(), result);
        }
        return result;
    }

    public static void getFileWithSuffix(final String fileName, File[] files, List<File> result) {
        if (files == null || files.length == 0) {
            return;
        }
        for (File f : files) {
            if (checkFileSuffix(fileName, f)) {
                result.add(f);
            }
        }
        for (File f : files) {
            getFileWithSuffix(fileName, f.listFiles(), result);
        }
    }

    // TODO: 判断是否是, 要找的文件
    public static boolean checkFile(final String fileName, final File file) {
        if (file.isDirectory()) {
            return false;
        }
        if (file.getName().toUpperCase().endsWith(".CLASS")) {
            return false;
        }
        if (file.getName().toUpperCase().startsWith(fileName.toUpperCase())) {
            return true;
        }
        return false;
    }

    public static List<File> getAllFile(final String path) {
        List<File> allFileList = new ArrayList<>();
        File fileNow = new File(path);
        getAllFileDetail(fileNow, allFileList);
        return allFileList;
    }

    public static void getAllFileDetail(final File file, final List<File> allFileList) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                File[] subFiles = file.listFiles();
                for (File f : subFiles) {
                    getAllFileDetail(f, allFileList);
                }
            } else {
                allFileList.add(file);
            }
        }
    }

    public static List<String> readFileToList(String filePath) {
        List<String> lines = new ArrayList<>();
        try {
            // 使用Paths获取文件路径
            Path path = Paths.get(filePath);

            // 使用Files.newBufferedReader打开文件并创建BufferedReader
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                // 逐行读取文件内容并添加到List中
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lines;
    }

    public static String getTableNameJavaLower(final Table table) {
        return CharUtil.firstToLowercase(table.tableNameJava);
    }
}
