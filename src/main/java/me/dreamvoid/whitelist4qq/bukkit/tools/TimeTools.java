package me.dreamvoid.whitelist4qq.bukkit.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName TimeTools
 * @Description TODO
 * @Author Tining
 * @data 2019/9/26 3:28
 * @Version 1.0
 **/
public class TimeTools {

    /**
    *@Author Tining
    *@Description 返回纯数字化当前时间
    *@Date 2019/9/30 3:05 
    *@Param []
    *@return java.lang.String
    **/
    public static String getDigitNow(){
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("yyyyMMddhhmmss");
        return ft.format(dNow);
    }

    /**
    *@Author Tining
    *@Description 返回当前日期和时间
    *@Date 2019/9/26 3:33
    *@Param []
    *@return java.lang.String;
    **/
    public static String getNow(){
        Date dNow = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy/MM/dd hh:mm:ss");
        return ft.format(dNow);
    }

    /**
    *@Author Tining
    *@Description 获取当前日期
    *@Date 2019/9/26 3:30 
    *@Param []
    *@return java.lang.String
    **/
    public static String getDate(){
        Date dNow = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");
        return ft.format(dNow);
    }

    /**
    *@Author Tining
    *@Description 获取当前时间
    *@Date 2019/9/26 3:31 
    *@Param []
    *@return java.lang.String
    **/
    public static String getTime(){
        Date dNow = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat("hh:mm");
        return ft.format(dNow);
    }


}
