package me.dreamvoid.whitelist4qq.bukkit.utils;

import me.dreamvoid.miraimc.api.MiraiMC;
import me.dreamvoid.miraimc.internal.Utils;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 继承与MiraiMC类
 * 提供更多数据库操作
 * @author Tining
 */
public class DataBaseUtils extends MiraiMC {
    /**
     * 以uuid查询所有账号
     * @param uuid
     * @return
     */
    public static List<String> getAccount(String uuid){
        List<String> res = new ArrayList<>();
        String createTable = "CREATE TABLE IF NOT EXISTS miraimc_binding (uuid TINYTEXT NOT NULL, qqid long NOT NULL);";
        String select = "SELECT * FROM miraimc_binding WHERE uuid='" + uuid + "'";
        ResultSet resultSet;
        try {
            Statement statement = Utils.connection.createStatement();
            statement.executeUpdate(createTable);
            resultSet = statement.executeQuery(select);
            while(resultSet.next()){
                res.add(resultSet.getString("qqid"));
            }

            resultSet.close();
            statement.close();
        }catch (Exception e){}
        return res;
    }

    /**
     *
     * 获取所有账号
     * @return
     */
    public static List<String> getAllAccount(){
        List<String> res = new ArrayList<>();
        String createTable = "CREATE TABLE IF NOT EXISTS miraimc_binding (uuid TINYTEXT NOT NULL, qqid long NOT NULL);";
        String select = "SELECT * FROM miraimc_binding";
        ResultSet resultSet;
        try {
            Statement statement = Utils.connection.createStatement();
            statement.executeUpdate(createTable);
            resultSet = statement.executeQuery(select);
            while(resultSet.next()){
                res.add(resultSet.getString("qqid"));
            }

            resultSet.close();
            statement.close();
        }catch (Exception e){}
        return res;
    }
    /**
     * 以account查询所有ID
     * @param account
     * @return
     */
    public static List<String> getUuid(String account){
        List<String> res = new ArrayList<>();
        String createTable = "CREATE TABLE IF NOT EXISTS miraimc_binding (uuid TINYTEXT NOT NULL, qqid long NOT NULL);";
        String select = "SELECT * FROM miraimc_binding WHERE qqid='" + account + "'";
        ResultSet resultSet;
        try {
            Statement statement = Utils.connection.createStatement();
            statement.executeUpdate(createTable);
            resultSet = statement.executeQuery(select);
            while(resultSet.next()){
                res.add(resultSet.getString("uuid"));
            }

            resultSet.close();
            statement.close();
        }catch (Exception e){}
        return res;
    }

    /**
     * 获取所有uuid
     * @return
     */
    public static List<String> getAllUuid(){
        List<String> res = new ArrayList<>();
        String createTable = "CREATE TABLE IF NOT EXISTS miraimc_binding (uuid TINYTEXT NOT NULL, qqid long NOT NULL);";
        String select = "SELECT * FROM miraimc_binding";
        ResultSet resultSet;
        try {
            Statement statement = Utils.connection.createStatement();
            statement.executeUpdate(createTable);
            resultSet = statement.executeQuery(select);
            while(resultSet.next()){
                res.add(resultSet.getString("uuid"));
            }

            resultSet.close();
            statement.close();
        }catch (Exception e){}
        return res;
    }

}
