package me.dreamvoid.whitelist4qq.bukkit;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class Config {
    private final BukkitPlugin plugin;

    public static boolean GEN_bStats;
    public static boolean GEN_CheckRange_JOIN;
    public static boolean GEN_CheckRange_ACTION;
    public static boolean GEN_CheckRange_SPEC;
    public static boolean GEN_PreventIDRebind;
    public static boolean GEN_PreventQQRebind;
    public static String GEN_KickMessage;
    public static String GEN_NotifyMessage;
    public static String GEN_BindSuccessMessage;

    public static List<Long> BOT_UsedBotAccounts;
    public static List<Long> BOT_UsedGroupAccounts;
    public static boolean BOT_CheckQQInGroup;
    public static boolean BOT_RemoveBindWhenQQQuit;
    public static boolean BOT_UseGroupMessageCommand;
    public static String BOT_BindCommandPrefix;
    public static String BOT_Messages_BindSuccess;
    public static String BOT_Messages_BindFailed;

    Config(BukkitPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();

        GEN_bStats = config.getBoolean("general.bStats", true);
        GEN_CheckRange_JOIN = config.getBoolean("general.check-range.join", true);
        GEN_CheckRange_ACTION = config.getBoolean("general.check-range.action", false);
        GEN_CheckRange_SPEC = config.getBoolean("general.check-range.spec", false);
        GEN_PreventIDRebind = config.getBoolean("general.prevent-id-rebind", true);
        GEN_PreventQQRebind = config.getBoolean("general.prevent-qq-rebind", true);
        GEN_KickMessage = config.getString("general.kick-message", "");
        GEN_NotifyMessage = config.getString("general.notify-message", "");
        GEN_BindSuccessMessage = config.getString("general.bind-success-message", "");

        BOT_UsedBotAccounts = config.getLongList("bot.used-bot-accounts");
        BOT_UsedGroupAccounts = config.getLongList("bot.used-group-numbers");
        BOT_CheckQQInGroup = config.getBoolean("bot.check-qq-in-group", true);
        BOT_RemoveBindWhenQQQuit = config.getBoolean("bot.remove-bind-when-qq-quit", false);
        BOT_UseGroupMessageCommand = config.getBoolean("bot.use-group-message-command", true);
        BOT_BindCommandPrefix = config.getString("bot.bind-command-prefix");
        BOT_Messages_BindSuccess = config.getString("bot.messages.bind-success");
        BOT_Messages_BindFailed = config.getString("bot.messages.bind-failed");
    }
}
