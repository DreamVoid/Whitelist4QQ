package me.dreamvoid.whitelist4qq.bukkit.tools.pool;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * @ClassName Pool
 * @Description 缓存池的抽象类
 * @Author Tining
 * @data 2019/9/30 7:39
 * @Version 1.0
 **/
public abstract class Pool<E> {

    ///缓存池大小
    public int limit = 500;

    ///缓存列表
    public ArrayList<E> list = new ArrayList<E>();

    /**
     *@Author Tining
     *@Description 加入新的项
     *@Date 2019/9/30 9:04
     *@Param [item]
     *@return boolean
     **/
    public boolean add(E item) {
        if (getSize() > limit) {
            return false;
        }
        list.add(item);
        return true;
    }

    /**
     *@Author Tining
     *@Description 移除项
     *@Date 2019/9/30 9:04
     *@Param [index]
     *@return boolean
     **/
    public boolean remove(int index) {
        if (index > getSize() - 1) {
            return false;
        }
        list.remove(index);
        return true;
    }

    /**
     *@Author Tining
     *@Description 插入项
     *@Date 2019/9/30 9:04
     *@Param [item, index]
     *@return boolean
     **/
    public boolean insert(E item, int index) {
        if (getSize() > limit) {
            return false;
        }
        list.add(index, item);
        return true;
    }

    /**
     *@Author Tining
     *@Description 获取池大小
     *@Date 2019/9/30 9:05
     *@Param []
     *@return long
     **/
    public long getSize() {
        return list.size();
    }

}
