package me.dreamvoid.whitelist4qq.bukkit;

import me.dreamvoid.miraimc.api.MiraiBot;
import me.dreamvoid.miraimc.api.MiraiMC;
import me.dreamvoid.miraimc.bukkit.event.MiraiGroupMemberLeaveEvent;
import me.dreamvoid.miraimc.bukkit.event.MiraiGroupMessageEvent;
import me.dreamvoid.whitelist4qq.bukkit.handle.CheckAllHandle;
import me.dreamvoid.whitelist4qq.bukkit.handle.CheckHandle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import static me.dreamvoid.whitelist4qq.bukkit.Config.*;


/**
 * @author Tining
 */
public class BukkitPlugin extends JavaPlugin implements Listener , CommandExecutor {
    /**
     * 配置文件
     */
    private Config config;

    /**
     * 被拦截者缓存
     */
    private final HashMap<Player, Boolean> cache = new HashMap<>();

    /**
     * 日志打印
     */
    Logger log = Logger.getLogger("测试");

    /**
     * 加载时事件
     */
    @Override
    public void onLoad() {
        this.config = new Config(this);
    }

    /**
     * 启用时事件
     */
    @Override
    public void onEnable() {
        //加载配置
        config.loadConfig();
        //注册玩家登陆事件
        Bukkit.getPluginManager().registerEvents(new PlayerLogin(), this);
        //注册玩家加入事件
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        //注册玩家行为事件
        if (GEN_CheckRange_ACTION) {
            Bukkit.getPluginManager().registerEvents(new PlayerActions(), this);
        }
        //注册机器人事件
        Bukkit.getPluginManager().registerEvents(new BotEvent(this), this);
        //注册定时任务
        //针对被拦截玩家的服务器內提示
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new AsyncCheckBind(this), 0L, 60L);
        if (GEN_bStats) {
            new Metrics(this, 13112);
        }
    }

    /**
     * 注销插件时的事件
     */
    @Override
    public void onDisable() {
        //注意取消自身的定时任务
        Bukkit.getScheduler().cancelTasks(this);
    }

    /**
     * 命令行时间
     * @param sender 控制台对象
     * @param command 命令
     * @param label
     * @param args 参数列表
     * @return
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //这里暂时没做什么实质性的功能
        //只有一个reload重载配置文件
        if (args.length > 0 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("whitelist4qq.command.whitelist4qq.reload")) {
            config.loadConfig();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a配置文件已经重新加载！"));
        } else if(args.length > 0 && args[0].equalsIgnoreCase("check") && sender.hasPermission("whitelist4qq.command.whitelist4qq.reload")){
            CheckHandle.onCommand(sender,command,label,args);
        }else if(args.length > 0 && args[0].equalsIgnoreCase("checkAll") && sender.hasPermission("whitelist4qq.command.whitelist4qq.reload")){
            CheckAllHandle.onCommand(sender,command,label,args);
        }
        else {
            sender.sendMessage("This server is running " + getDescription().getName() + " version " + getDescription().getVersion() + " by " + getDescription().getAuthors().toString().replace("[", "").replace("]", "") + " (MiraiMC version " + Bukkit.getPluginManager().getPlugin("MiraiMC").getDescription().getVersion() + ")");
        }
        return true;
    }

    /**
     * 玩家即将加入服务器事件
     */
    public class PlayerLogin implements Listener {
        @EventHandler
        public void onPlayerJoin(AsyncPlayerPreLoginEvent e) {
            boolean allow = false;
            // 加入服务器的时候检测
            if (GEN_CheckRange_JOIN) {
                long binder = MiraiMC.getBinding(e.getUniqueId().toString());
                long checkByName = MiraiMC.getBinding(e.getName());
                if (binder != 0 || checkByName != 0) {
                    // 是否需要进一步检测是否在群内
                    if (BOT_CheckQQInGroup) {
                        for (long group : BOT_UsedGroupAccounts) {
                            if (allow) {
                                // 如果下面的代码已经检测到在群里了，就不继续检测
                                break;
                            }
                            for (long bot : BOT_UsedBotAccounts) {
                                try {
                                    // 加个try防止服主忘记登录机器人然后尝试获取的时候报错的问题
                                    if (MiraiBot.getBot(bot).getGroup(group).contains(binder)) {
                                        allow = true;
                                        break;
                                    }
                                } catch (NoSuchElementException ignored) {
                                    // 不需要处理报错，直接ignored
                                }
                            }
                        }
                    } else {
                        // 不需要则直接true
                        allow = true;
                    }
                }
            } else {
                // 如果不在加入服务器的时候检测，直接放行
                allow = true;
            }

            if (allow) {
                e.allow();
            } else {
                e.disallow(
                        AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                        ChatColor.translateAlternateColorCodes('&', GEN_KickMessage)
                );
            }
        }
    }

    /**
     * 玩家加入服务器事件
     */
    public class PlayerJoin implements Listener {
        @EventHandler
        public void onPlayerJoined(PlayerJoinEvent e) {
            //监测是否在库中，否则列入非白名单列表
            if (MiraiMC.getBinding(e.getPlayer().getUniqueId().toString()) == 0
                    && MiraiMC.getBinding(e.getPlayer().getName()) == 0) {
                cache.put(e.getPlayer(), false);
            }
            if (GEN_CheckRange_SPEC) {
                e.getPlayer().setGameMode(GameMode.SPECTATOR);
            }
        }
    }

    /**
     * 玩家动作事件
     */
    public class PlayerActions implements Listener {
        @EventHandler
        public void onPlayerMove(PlayerMoveEvent e) {
            if (cache.containsKey(e.getPlayer()) && !GEN_CheckRange_SPEC) {
                e.setCancelled(true);
            }
        }

        @EventHandler
        public void onPlayerInteract(PlayerInteractEvent e) {
            if (cache.containsKey(e.getPlayer())) {
                e.setCancelled(true);
            }
        }

        @EventHandler
        public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent e) {
            if (cache.containsKey(e.getPlayer())) {
                e.setCancelled(true);
            }
        }

        @EventHandler
        public void onPlayerBedEnter(PlayerBedEnterEvent e) {
            if (cache.containsKey(e.getPlayer())) {
                e.setCancelled(true);
            }
        }

        @EventHandler
        public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
            if (cache.containsKey(e.getPlayer())) {
                e.setCancelled(true);
            }
        }

        @EventHandler
        public void onPlayerVelocity(PlayerVelocityEvent e) {
            if (cache.containsKey(e.getPlayer())) {
                e.setCancelled(true);
            }
        }

        @EventHandler
        public void onPlayerChat(AsyncPlayerChatEvent e) {
            if (cache.containsKey(e.getPlayer())) {
                e.setCancelled(true);
            }
        }

        @EventHandler
        public void onPlayerDropItem(PlayerDropItemEvent e) {
            if (cache.containsKey(e.getPlayer())) {
                e.setCancelled(true);
            }
        }

        @EventHandler
        public void onPlayerFish(PlayerFishEvent e) {
            if (cache.containsKey(e.getPlayer())) {
                e.setCancelled(true);
            }
        }
    }

    /**
     * 机器人事件
     */
    public class BotEvent implements Listener {
        private final BukkitPlugin plugin;
        BotEvent(BukkitPlugin plugin){
            this.plugin = plugin;
        }

        @EventHandler
        public void onGroupMessage(MiraiGroupMessageEvent e) {
            //log.info("测试收到");
            if (BOT_UsedBotAccounts.contains(e.getBotID()) && BOT_UsedGroupAccounts.contains(e.getGroupID()) && BOT_UseGroupMessageCommand && e.getMessageContent().startsWith(BOT_BindCommandPrefix)) {
                if ((GEN_PreventIDRebind && MiraiMC.getBinding(Bukkit.getOfflinePlayer(e.getMessageContent().replace(BOT_BindCommandPrefix, "")).getUniqueId().toString()) != 0)
                        || (GEN_PreventQQRebind && !MiraiMC.getBinding(e.getSenderID()).equals(""))) {
                    // 阻止绑定
                    //编译不通过，并且机器人经常被封，暂时关闭回复功能
                    //MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(BOT_Messages_BindFailed.replace("%id%", MiraiMC.getBinding(e.getSenderID())));
                } else {
                    // 允许绑定
                    MiraiMC.addBinding(Bukkit.getOfflinePlayer(e.getMessageContent().replace(BOT_BindCommandPrefix, "")).getUniqueId().toString(), e.getSenderID());
                    MiraiMC.addBinding(e.getMessageContent().replace(BOT_BindCommandPrefix, "").trim(), e.getSenderID());
                    //编译不通过，并且机器人经常被封，暂时关闭回复功能
                    //MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(BOT_Messages_BindSuccess);
                }
            }
            if (e.getMessageContent().contains("测试5566")) {
                log.info("BotID:" + e.getBotID());
                log.info("群ID:" + e.getGroupID());

                MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessageMirai("测试收到了");
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessageMirai("测试收到了");
                    }
                }.runTaskAsynchronously(this.plugin);
            }
        }

        @EventHandler
        public void onGroupQuit(MiraiGroupMemberLeaveEvent e) {
            if (BOT_UsedBotAccounts.contains(e.getBotID()) && BOT_UsedGroupAccounts.contains(e.getGroupID()) && BOT_RemoveBindWhenQQQuit) {
                MiraiMC.removeBinding(e.getTargetID());
            }
        }
    }

    public class AsyncCheckBind implements Runnable {
        private final BukkitPlugin plugin;

        AsyncCheckBind(BukkitPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public void run() {
            if (cache.size() > 0) {
                for (Player player : cache.keySet()) {
                    if (MiraiMC.getBinding(player.getUniqueId().toString()) != 0) {
                        cache.remove(player);
                        if (GEN_CheckRange_SPEC) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    player.setGameMode(Bukkit.getDefaultGameMode());
                                }
                            }.runTask(plugin);
                        }
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', GEN_BindSuccessMessage));
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', GEN_NotifyMessage));
                    }
                }
            }
        }
    }
}
