package me.dreamvoid.whitelist4qq.bukkit.handle;

import me.dreamvoid.whitelist4qq.bukkit.tools.StringTools;
import me.dreamvoid.whitelist4qq.bukkit.utils.DataBaseUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * 用于全体管理数据检查的相关handle
 * @author Tining
 */
public class CheckAllHandle {
    /**
     * 传递函数
     * @param sender 发送者句柄
     * @param command 具体命令
     * @param label
     * @param args 参数列表
     * @return
     */
    public static boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(args[1] == null){
            fail(sender);
            return true;
        }
        String secondArg = args[1];
        switch (secondArg.toLowerCase()){
            case "qq":
            case "account": return ok(sender, DataBaseUtils.getAllAccount());
            case "uuid":
            case "name":
            case "id": return ok(sender,DataBaseUtils.getAllUuid());
            default: return fail(sender);
        }
    }

    public static boolean ok(CommandSender sender, List<String> str){
        String res = StringTools.listToLine(str);
        sender.sendMessage("查询结果:\n" + res);
        return true;
    }

    public static boolean fail(CommandSender sender){
        sender.sendMessage("指令错误，请使用 /Whitelist4QQ checkAll [属性]");
        return true;
    }
}
