package com.apidoc.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * 常量类
 */
public class Const {
    private static final Logger logger = LoggerFactory.getLogger(Const.class);

    /**
     * 项目路径
     */
    public static String projectPath;
    /**
     * 代码的存放路径
     */
    public static String codePath;

    /**
     * 字符集
     */
    public static final String CHARSET = "utf-8";
    /**
     * 521k
     */
    public static final int BUFFERSIZE = 512 * 1024;

    /**
     * ---类型常量---
     * 字符串string 数字number 自定义对象 数组（普通数组，对象数组） 泛型（list map） 文件 boolean 日期Data
     */
    public static final String STRING = "string 字符串";
    public static final String NUMBER = "number 数字";
    public static final String OBJECT = "object 对象: ";
    public static final String ARRAY = "array 数组: ";
    public static final String BOOLEANN = "boolean 布尔类型（是/否）";
    public static final String FILE = "file 文件";
    public static final String DATE = "date 日期时间";

    /**
     * ----HTTP 动作-------
     */
    public static final String GET = "GET";
    public static final String PUT = "PUT";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";


    /**
     * -----请求或响应方式（类型）--------
     */
    public static final String JSON = "JSON类型数据";
    public static final String URL = "URL拼接参数 (示例: ?a=XX&&b=XX)";
    public static final String URI = "URI占位符 (示例: /XXX/{id}/{name})";
    public static final String FROM = "FROM表单数据";
    public static final String BLOB = "BLOB二进制流";

    static {
        Properties properties = new Properties();
        try {
            properties.load(Const.class.getResourceAsStream("/application.properties"));
        } catch (IOException e) {
            logger.error("load clearing config file error", e);
        }

        projectPath = new File("").getAbsolutePath() + File.separator;
        codePath = projectPath + "src/main/java/";
//        projectPath = properties.getProperty("java.doc.projectPath");
//        codePath = projectPath;
    }

}
