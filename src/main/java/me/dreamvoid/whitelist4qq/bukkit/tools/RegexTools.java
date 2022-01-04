package me.dreamvoid.whitelist4qq.bukkit.tools;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName FileTools
 * @Description 针对于正则表达式操作w的工具类
 * @Author Tining
 * @data 2019/9/18 0:35
 * @Version 1.0
 **/
public class RegexTools{
    
    /**
    *@Author Tining
    *@Description 返回文件后缀
    *@Date 2019/9/30 4:59
    *@Param [filename]
    *@return java.lang.String
    **/
    public static String getFileType(String filename){
        String prefix = "(.+//)?.([a-zA-z0-9]+)$";
        return getRegex(prefix,filename);
    }


    /**
    *@Author Tining
    *@Description 判断字符串是否以特定结束
    *@Date 2019/9/30 2:48 
    *@Param [end, str]
    *@return boolean
    **/
    public static boolean findEnd(String end,String str){
        String prefix = str + "$";
        return findRegex(prefix,str);
    }

    /**
    *@Author Tining
    *@Description 判断字符串是否以特定开头
    *@Date 2019/9/30 2:47 
    *@Param [begin, str]
    *@return boolean
    **/
    public static boolean findBegin(String begin, String str){
        String prefix="^" +begin;
        return findRegex(prefix,str);
    }

    /**
    *@Author Tining
    *@Description 匹配IP地址
    *@Date 2019/9/26 23:57 
    *@Param [str]
    *@return java.util.ArrayList<java.lang.String>
    **/
    public static ArrayList<String> matchIP(String str){
        String prefix = "((?:(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d)\\.){3}(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d))";
        return matchRegex(prefix,str);
    }

    /**
    *@Author Tining
    *@Description 匹配中文
    *@Date 2019/9/26 23:57 
    *@Param [str]
    *@return java.util.ArrayList<java.lang.String>
    **/
    public static ArrayList<String> matchChinese(String str){
        String prefix = "[\\u4e00-\\u9fa5]+";
        return matchRegex(prefix,str);
    }

    /**
    *@Author Tining
    *@Description 匹配日期
    *@Date 2019/9/26 23:57 
    *@Param [str]
    *@return java.util.ArrayList<java.lang.String>
    **/
    public static ArrayList<String> matchDate(String str){
        String prefix = "\\d{4}-\\d{1,2}-\\d{1,2}";
        ArrayList<String> get = matchRegex(prefix,str);
        if(get.size() == 0)
        {
            prefix = "\\d{4}/\\d{1,2}/\\d{1,2}";
            get = matchRegex(prefix,str);
        }
        if(get.size() == 0)
        {
            prefix = "\\d{1,2}/\\d{1,2}/\\d{4}";
            get = matchRegex(prefix,str);
        }
        if(get.size() == 0)
        {
            prefix = "\\d{1,2}-\\d{1,2}-\\d{4}";
            get = matchRegex(prefix,str);
        }
        return get;
    }
    
    /**
    *@Author Tining
    *@Description 匹配强密码(必须包含大小写字母和数字的组合，不能使用特殊字符，长度在8-10之间)
    *@Date 2019/9/26 23:58
    *@Param [str]
    *@return java.util.ArrayList<java.lang.String>
    **/
    public static ArrayList<String> matchLegalStrongPassword(String str){
        String prefix = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,10}";
        return matchRegex(prefix,str);
    }

    /**
    *@Author Tining
    *@Description 匹配密码(以字母开头，长度在6~18之间，只能包含字母、数字和下划线)
    *@Date 2019/9/26 23:58 
    *@Param [str]
    *@return java.util.ArrayList<java.lang.String>
    **/
    public static ArrayList<String> matchLegalPassword(String str){
        String prefix = "[a-zA-Z]\\w{6,18}";
        return matchRegex(prefix,str);
    }

    /**
    *@Author Tining
    *@Description 匹配合法帐号(字母开头，允许5-16字节，允许字母数字下划线)
    *@Date 2019/9/26 23:58 
    *@Param [str]
    *@return java.util.ArrayList<java.lang.String>
    **/
    public static ArrayList<String> matchLegalAccount(String str){
        String prefix = "[a-zA-Z][a-zA-Z0-9_]{4,15}";
        return matchRegex(prefix,str);
    }

    /**
    *@Author Tining
    *@Description 匹配域名
    *@Date 2019/9/26 23:58 
    *@Param [str]
    *@return java.util.ArrayList<java.lang.String>
    **/
    public static ArrayList<String> matchDomain(String str){
        String prefix = "[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(/.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+/.?";
        return matchRegex(prefix,str);
    }

    /**
    *@Author Tining
    *@Description 匹配Url
    *@Date 2019/9/26 23:58 
    *@Param [str]
    *@return java.util.ArrayList<java.lang.String>
    **/
    public static ArrayList<String> matchUrl(String str){
        String prefix = "[a-zA-z]+://[^\\s]* 或 ^http://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?";
        return matchRegex(prefix,str);
    }

    /**
    *@Author Tining
    *@Description 匹配邮箱
    *@Date 2019/9/26 23:59 
    *@Param [str]
    *@return java.util.ArrayList<java.lang.String>
    **/
    public static ArrayList<String> matchEmail(String str){
        String prefix = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        return matchRegex(prefix,str);
    }

    /**
    *@Author Tining
    *@Description 匹配大写字母
    *@Date 2019/9/26 23:59 
    *@Param [str]
    *@return java.util.ArrayList<java.lang.String>
    **/
    public static ArrayList<String> matchUpWord(String str){
        String prefix = "[A-Z]+";
        return matchRegex(prefix,str);
    }

    /**
    *@Author Tining
    *@Description 匹配小写字母
    *@Date 2019/9/26 23:59 
    *@Param [str]
    *@return java.util.ArrayList<java.lang.String>
    **/
    public static ArrayList<String> matchLowWord(String str){
        String prefix = "[a-z]+";
        return matchRegex(prefix,str);
    }

    /**
    *@Author Tining
    *@Description 匹配非空项
    *@Date 2019/9/27 0:00 
    *@Param [str]
    *@return java.util.ArrayList<java.lang.String>
    **/
    public static ArrayList<String> matchNonBlank(String str){
        String prefix = "\\S+";
        return matchRegex(prefix,str);
    }

    /**
    *@Author Tining
    *@Description 匹配空的项
    *@Date 2019/9/27 0:01 
    *@Param [str]
    *@return java.util.ArrayList<java.lang.String>
    **/
    public static ArrayList<String> matchBlank(String str){
        String prefix = "\\S+";
        return matchRegex(prefix,str);
    }

    /**
    *@Author Tining
    *@Description 匹配特殊字符
    *@Date 2019/9/27 0:01 
    *@Param [str]
    *@return java.util.ArrayList<java.lang.String>
    **/
    public static ArrayList<String> matchSpecialSymbol(String str){
        String prefix = "\\W+";
        return matchRegex(prefix,str);
    }

    /**
    *@Author Tining
    *@Description 匹配英文单词
    *@Date 2019/9/27 0:01 
    *@Param [str]
    *@return java.util.ArrayList<java.lang.String>
    **/
    public static ArrayList<String> matchWords(String str){
        String prefix = "[a-zA-Z]+";
        return matchRegex(prefix,str);
    }

    /**
    *@Author Tining
    *@Description 匹配数字，包括正负和小数
    *@Date 2019/9/27 0:02 
    *@Param [str]
    *@return java.util.ArrayList<java.lang.String>
    **/
    public static ArrayList<String>  matchNumber(String str){
        String prefix = "-?\\d+(\\.\\d+)?";
        return matchRegex(prefix,str);
    }

    /**
    *@Author Tining
    *@Description 匹配符合两端字符串中间的内容
    *@Date 2019/9/27 0:03 
    *@Param [begin, end, str]
    *@return java.util.ArrayList<java.lang.String>
    **/
    public static ArrayList<String> matchInner(String begin,String end,String str){
        String prefix = "(?<=" + begin + ").*?(?=" + end + ")";
        return matchRegex(prefix,str);
    }

    /**
    *@Author Tining
    *@Description 获取字符串中符合正则表达式的字段
    *@Date 2019/9/30 3:17 
    *@Param [patternStr, str]
    *@return java.lang.String
    **/
    public static String getRegex(String patternStr,String str){
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(str);
        matcher.find();
        return matcher.group();
    }

    /**
    *@Author Tining
    *@Description 判断字符串是否符合正则表达式
    *@Date 2019/9/30 2:45 
    *@Param [patternStr, str]
    *@return boolean
    **/
    public static boolean findRegex(String patternStr,String str){
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    /**
    *@Author Tining
    *@Description 返回符合正则表达式的内容
    *@Date 2019/9/27 0:03 
    *@Param [patten, str]
    *@return java.util.ArrayList<java.lang.String>
    **/
    public static ArrayList<String> matchRegex(String patternStr,String str){
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(str);
        ArrayList<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;
    }
}
