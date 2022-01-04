package me.dreamvoid.whitelist4qq.bukkit.tools;

import java.util.Random;

/**
 * @ClassName MathTools
 * @Description 针对数字操作的工具类
 * @Author Tining
 * @data 2019/9/30 0:58
 * @Version 1.0
 **/
public class MathTools{

    /**
    *@Author Tining
    *@Description 返回一个随机真假值
    *@Date 2019/9/30 1:05 
    *@Param []
    *@return boolean
    **/
    public static boolean getRandomBool(){
        Random rd = new Random();
        boolean get = rd.nextBoolean();
        return get;
    }
    
    /**
    *@Author Tining
    *@Description 返回一个随机小数
    *@Date 2019/9/30 1:03 
    *@Param []
    *@return double
    **/
    public static double getRandomDouble(){
        Random rd = new Random();
        double get = rd.nextDouble();
        return get;
    }
    
    /**
    *@Author Tining
    *@Description 返回一个界限内的随机整数
    *@Date 2019/9/30 1:08 
    *@Param [begin, end]
    *@return int
    **/
    public static int getRandomInt(int begin,int end){
        int get = getRandomInt(end - begin);
        return get + begin;
    }

    /**
    *@Author Tining
    *@Description 返回一个随机整数
    *@Date 2019/9/30 1:01 
    *@Param [bound]
    *@return int
    **/
    public static int getRandomInt(int bound){
        Random rd = new Random();
        int get = rd.nextInt(bound);
        return get;
    }
}
