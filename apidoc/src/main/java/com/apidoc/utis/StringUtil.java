package com.apidoc.utis;

import cn.hutool.core.util.ReUtil;
import com.apidoc.common.Const;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 */
public class StringUtil {

    public static boolean isEmpty(String str) {
        return (str == null || "".equals(str));
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isNotEmpty(String[] value) {
        return !isEmpty(value);
    }

    private static boolean isEmpty(String[] value) {
        return (value == null || value.length == 0);
    }

    /**
     * 得到java类文件的字段和注释
     *
     * @param className 类全名
     * @return Map
     */
    public synchronized static Map<String, String> getFieldsNotes(String className) {
        String path = getClassPath(className);
        if (!new File(path).exists()) {
            return null;
        }
        //1.行注释 //
        //2.多行注释/*  */
        //3.文档注释 /**  */
        //文档注释可以合并为多行注释
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), Const.CHARSET), Const.BUFFERSIZE)
        ) {

            //存放字段的注释 key为字段名 value为注释
            Map<String, String> noteMap = new HashMap<>();
            String line;
            StringBuilder sb = new StringBuilder();
            String valueUp = null;
            while ((line = bufferedReader.readLine()) != null) {
                ////读取行注释
                //正则解释： 任意多个任意字符//任意多个任意字符
                boolean match = ReUtil.isMatch(".*//.*", line);
                if (line.contains("*")) {
                    valueUp = null;
                }
                if (match) {
                    //正则解释： 任意多个任意字符//
                    valueUp = line.replaceAll(".*//", "").trim();
                    //行注释只能是在代码行的上边或右边
                    //正则解释： //任意多个任意字符
                    String str = line.replaceAll("//.*", "");
                    //行注释位于代码行右边
                    if (isNotEmpty(str.trim())) {
                        //正则解释： 空白字符
                        String[] split = str.split("\\s");
                        if (split.length > 0) {
                            //key
                            String key = split[split.length - 1].replace(";", "");
                            //value
                            //正则解释： 任意多个任意字符//
                            String value = line.replaceAll(".*//", "");
                            noteMap.put(key, value);
                        }
                    }
                } else {//行注释位于代码行上方
                    if (isNotEmpty(valueUp) && line.contains(";") && line.contains("private")) {
                        //key
                        String[] split = line.split("\\s");
                        String key = split[split.length - 1].replace(";", "");
                        String value = valueUp;
                        valueUp = null;
                        noteMap.put(key, value);
                    }
                }
                sb.append(line);
            }
            //java文件的内容
            String str = sb.toString();
            ////读取多行注释
            //正则解释 /+1或2个* +任意个除{之外的字符非贪婪式匹配 +*/+人一个空白字符+private+任意个任意字符+;
            Pattern p = Pattern.compile("/\\*{1,2}[^{]*?\\*/\\s*private.+?;");
            Matcher m = p.matcher(str);
            while (m.find()) {
                String group = m.group();
                Pattern pkey = Pattern.compile("/\\*{1,2}.*?\\*/");
                Matcher mkey = pkey.matcher(group);
                mkey.find();
                String value = mkey.group();
                value = value.replaceAll("/|\\*", "").trim();

                Pattern pvalue = Pattern.compile("private.+?;");
                Matcher mvalue = pvalue.matcher(group);
                mvalue.find();
                String key = mvalue.group();
                String[] keys = key.split("\\s");
                key = keys[keys.length - 1].replaceAll(";", "");

                noteMap.put(key, value);
            }
            System.err.println(JsonUtil.toJsonString(noteMap));
            return noteMap;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 根据类名获得java源文件的路径
     *
     * @param className
     * @return
     */
    private static String getClassPath(String className) {
        String path = className.replace(".", File.separator) + ".java";
        path = Const.codePath + path;
        System.out.println(path);
        return path;
    }

    /**
     * 获得类所有public方法的注释
     *
     * @param className 类的全名
     */
    public static Map<String, String> getMethodNotes(String className) {
        String path = getClassPath(className);
        //兼容class文件 和源代码
        if (!new File(path).exists()) {
            return new HashMap<>();
        }
        //1.行注释 //
        //2.多行注释/*  */
        //3.文档注释 /**  */
        //文档注释可以合并为多行注释
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), Const.CHARSET), Const.BUFFERSIZE)
        ) {

            //存放字段的注释 key为字段名 value为注释
            Map<String, String> noteMap = new HashMap<>();
            String line;
            StringBuilder sb = new StringBuilder();
            String valueUp = null;
            while ((line = bufferedReader.readLine()) != null) {
                ////读取行注释
                //行注释只能是在代码行的上边
                //正则解释： 任意多个空白字符//任意多个任意字符
                boolean match = ReUtil.isMatch("\\s*//.*", line);
                if (match) {
                    //正则解释： 任意多个任意字符//
                    valueUp = line.replaceAll(".*//", "").trim();
                } else {
                    if (isNotEmpty(valueUp) && line.contains("public") && line.contains("(")) {
                        Pattern pvalue = Pattern.compile("public.+?\\(");
                        Matcher mvalue = pvalue.matcher(line);
                        mvalue.find();
                        String key = mvalue.group();
                        String[] keys = key.split("\\s");
                        key = keys[keys.length - 1].replaceAll("\\(|\\)", "");
                        String value = valueUp;
                        valueUp = null;
                        noteMap.put(className + "-" + key, value);
                    }
                }
                sb.append(line);
            }
            //java文件的内容
            String str = sb.toString();
            ////读取多行注释
            Pattern p = Pattern.compile("/\\*{1,2}[^/]*?\\*/[^**]+?public[^{]+?\\(");
            Matcher m = p.matcher(str);
            while (m.find()) {
                String group = m.group();
                System.out.println(group);
                Pattern pkey = Pattern.compile("/\\*{1,2}.*?\\*/");
                Matcher mkey = pkey.matcher(group);
                mkey.find();
                String value = mkey.group();
                value = value.replaceAll("/|\\*", "").trim();
                value = value.split("\\s")[0];

                Pattern pvalue = Pattern.compile("public.+?\\(");
                Matcher mvalue = pvalue.matcher(group);
                mvalue.find();
                String key = mvalue.group();
                String[] keys = key.split("\\s");
                key = keys[keys.length - 1].replaceAll("\\(|\\)", "");

                noteMap.put(className + "-" + key, value);
            }
            System.err.println(JsonUtil.toJsonString(noteMap));
            return noteMap;
        } catch (IOException e) {
            return null;
        }
    }

}
