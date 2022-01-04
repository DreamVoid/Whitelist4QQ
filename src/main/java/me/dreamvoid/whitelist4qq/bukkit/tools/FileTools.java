package me.dreamvoid.whitelist4qq.bukkit.tools;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * @ClassName FileTools
 * @Description 针对于文件操作的工具类
 * @Author Tining
 * @data 2019/9/18 0:35
 * @Version 1.0
 **/
public class FileTools {

    /**
     * 日志纪录类
     */
    public static Logger log = LoggerFactory.getLogger("日志");

    /**
     * @return java.lang.String
     * @Author Tining
     * @Description 返回文件路径
     * @Date 2019/9/30 3:39
     * @Param [name]
     **/
    public static String path(String name) {
        try {
            File file = new File(name);
            if (!file.exists()) {
                return "";
            }
            return file.getPath();
        } catch (Exception e) {
            if (log != null) {
                log.info(e.toString());
            } else {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * @return java.lang.String
     * @Author Tining
     * @Description 返回文件名，不包含后缀
     * @Date 2019/9/30 3:38
     * @Param [name]
     **/
    public static String filename(String name) {
        if (!name.contains(".")) {
            return name;
        }
        return name.replaceAll(filetype(name), "");
    }

    /**
     * @return java.lang.String
     * @Author Tining
     * @Description 返回文件后缀名
     * @Date 2019/9/30 3:37
     * @Param [name]
     **/
    public static String filetype(String name) {
        if (!name.contains(".")) {
            return "";
        }
        String[] arr = name.split("\\.");
        return arr[arr.length - 1];
    }

    /**
     * @return long
     * @Author Tining
     * @Description 获取文件大小
     * @Date 2019/9/29 3:43
     * @Param [path]
     **/
    public static long size(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                return -1;
            }
            return file.length();
        } catch (Exception e) {
            if (log != null) {
                log.info(e.toString());
            } else {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * @return java.lang.String
     * @Author Tining
     * @Description 读取文件到字符串
     * @Date 2019/9/18 0:57
     * @Param [path]
     **/
    public static String read(String path) {
        String text = null;
        try {
            // 在给定从中读取数据的文件名的情况下创建一个新 FileReader
            FileReader fr = new FileReader(path);
            // 创建一个使用默认大小输入缓冲区的缓冲字符输入流
            BufferedReader br = new BufferedReader(fr);
            String string = null;
            text = br.readLine();
            //如果为空返回空
            if (text == null) {
                return text;
            }
            text = "";
            while (null != (string = br.readLine())) {
                text += string;
            }
        } catch (Exception e) {
            if (log != null) {
                log.info(e.toString());
            } else {
                e.printStackTrace();
            }
        }
        //确保此步已经返回非空
        return text;
    }

    /**
     * @return void
     * @Author Tining
     * @Description 写字符串到文件中，如果没有，则创建文件，这将会复写文件
     * @Date 2019/9/18 1:05
     * @Param [text, path]
     **/
    public static boolean write(String path, String text) {
        File file = new File(path);
        FileWriter fw;
        //测试文件是否存在,如果不存在则创建
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file);
            fw.write(text);
        } catch (Exception e) {
            log.info(e.toString());
            return false;
        }
        return true;
    }

    /**
     * @return void
     * @Author Tining
     * @Description 追加写字符串到文件中，如果没有，则创建文件
     * @Date 2019/9/18 1:05
     * @Param [text, path]
     **/
    public static boolean writeAppend(String path, String text) {
        File file = new File(path);
        FileWriter fw;
        BufferedWriter bw;
        //测试文件是否存在,如果不存在则创建
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                log.info(e.toString());
                return false;
            }
        }
        try {
            fw = new FileWriter(file, true);
            fw.write(text);
        } catch (Exception e) {
            log.info(e.toString());
            return false;
        }
        return true;
    }

    /**
     * @return boolean
     * @Author Tining
     * @Description 重命名文件
     * @Date 2019/9/27 2:44
     * @Param [name, newName]
     **/
    public static boolean renameFile(String name, String newName) {
        File file = new File(name);
        File newFile = new File(newName);
        return file.renameTo(newFile);
    }
}
