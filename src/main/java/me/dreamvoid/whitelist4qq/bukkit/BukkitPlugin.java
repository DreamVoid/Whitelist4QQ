package me.dreamvoid.whitelist4qq.bukkit;

import me.dreamvoid.miraimc.api.MiraiBot;
import me.dreamvoid.miraimc.api.MiraiMC;
import me.dreamvoid.miraimc.bukkit.event.MiraiGroupMemberLeaveEvent;
import me.dreamvoid.miraimc.bukkit.event.MiraiGroupMessageEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.NoSuchElementException;

import static me.dreamvoid.whitelist4qq.bukkit.Config.*;

public class BukkitPlugin extends JavaPlugin implements Listener {
    private Config config;
    private final HashMap<Player, Boolean> cache = new HashMap<>();

    @Override
    public void onLoad() {
        this.config = new Config(this);
    }

    @Override
    public void onEnable() {
        config.loadConfig();
        Bukkit.getPluginManager().registerEvents(new PlayerLogin(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        if(GEN_CheckRange_ACTION) {
            Bukkit.getPluginManager().registerEvents(new PlayerActions(), this);
        }
        Bukkit.getPluginManager().registerEvents(new BotEvent(), this);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new AsyncCheckBind(this), 0L, 60L);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length>0 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("whitelist4qq.command.whitelist4qq.reload")){
            config.loadConfig();
            sender.sendMessage("&a配置文件已经重新加载！");
        } else sender.sendMessage("This server is running "+getDescription().getName()+" version "+getDescription().getVersion()+" by "+ getDescription().getAuthors().toString().replace("[","").replace("]","")+" (MiraiMC version "+Bukkit.getPluginManager().getPlugin("MiraiMC").getDescription().getVersion()+")");
        return true;
    }

    /**
     * 玩家即将加入服务器事件
     */
    public class PlayerLogin implements Listener {
        @EventHandler
        public void onPlayerJoin(AsyncPlayerPreLoginEvent e) {
            boolean allow = false;

            if(GEN_CheckRange_JOIN) { // 加入服务器的时候检测
                long binder = MiraiMC.getBinding(e.getUniqueId().toString());
                if(binder != 0) {
                    // 是否需要进一步检测是否在群内
                    if(BOT_CheckQQInGroup) {
                        for(long group : BOT_UsedGroupAccounts) {
                            if(allow) break; // 如果下面的代码已经检测到在群里了，就不继续检测

                            for(long bot : BOT_UsedBotAccounts) {
                                try { // 加个try防止服主忘记登录机器人然后尝试获取的时候报错的问题
                                    if(MiraiBot.getBot(bot).getGroup(group).contains(binder)) {
                                        allow = true;
                                        break;
                                    }
                                } catch (NoSuchElementException ignored) { } // 不需要处理报错，直接ignored
                            }
                        }
                    } else allow = true; // 不需要则直接true
                }
            } else allow = true; // 如果不在加入服务器的时候检测，直接放行

            if(allow){
                e.allow();
            } else {
                e.disallow(
                        AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                        ChatColor.translateAlternateColorCodes('&',GEN_KickMessage)
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
            if (MiraiMC.getBinding(e.getPlayer().getUniqueId().toString()) == 0) {
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
            if(cache.containsKey(e.getPlayer()) && !GEN_CheckRange_SPEC)
                e.setCancelled(true);
        }

        @EventHandler
        public void onPlayerInteract(PlayerInteractEvent e) {
            if(cache.containsKey(e.getPlayer()))
                e.setCancelled(true);
        }

        @EventHandler
        public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent e) {
            if(cache.containsKey(e.getPlayer()))
                e.setCancelled(true);
        }

        @EventHandler
        public void onPlayerBedEnter(PlayerBedEnterEvent e) {
            if(cache.containsKey(e.getPlayer()))
                e.setCancelled(true);
        }

        @EventHandler
        public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
            if(cache.containsKey(e.getPlayer()))
                e.setCancelled(true);
        }

        @EventHandler
        public void onPlayerVelocity(PlayerVelocityEvent e) {
            if(cache.containsKey(e.getPlayer()))
                e.setCancelled(true);
        }

        @EventHandler
        public void onPlayerChat(AsyncPlayerChatEvent e) {
            if(cache.containsKey(e.getPlayer()))
                e.setCancelled(true);
        }

        @EventHandler
        public void onPlayerDropItem(PlayerDropItemEvent e) {
            if(cache.containsKey(e.getPlayer()))
                e.setCancelled(true);
        }

        @EventHandler
        public void onPlayerFish(PlayerFishEvent e) {
            if(cache.containsKey(e.getPlayer()))
                e.setCancelled(true);
        }
    }

    /**
     * 机器人事件
     */
    public class BotEvent implements Listener {
        @EventHandler
        public void onGroupMessage(MiraiGroupMessageEvent e) {
            if (BOT_UsedBotAccounts.contains(e.getBotID()) && BOT_UsedGroupAccounts.contains(e.getGroupID()) && BOT_UseGroupMessageCommand && e.getMessageContent().startsWith(BOT_BindCommandPrefix)) {
                if ((GEN_PreventIDRebind && MiraiMC.getBinding(Bukkit.getOfflinePlayer(e.getMessageContent().replace(BOT_BindCommandPrefix, "")).getUniqueId().toString()) != 0) || (GEN_PreventQQRebind && !MiraiMC.getBinding(e.getSenderID()).equals(""))) {
                    // 阻止绑定
                    MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(BOT_Messages_BindFailed.replace("%id%", MiraiMC.getBinding(e.getSenderID())));
                } else {
                    // 允许绑定
                    MiraiMC.addBinding(Bukkit.getOfflinePlayer(e.getMessageContent().replace(BOT_BindCommandPrefix, "")).getUniqueId().toString(), e.getSenderID());
                    MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(BOT_Messages_BindSuccess);
                }
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
            if(cache.size() > 0) {
                for(Player player : cache.keySet()){
                    if(MiraiMC.getBinding(player.getUniqueId().toString()) != 0) {
                        cache.remove(player);
                        if(GEN_CheckRange_SPEC) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    player.setGameMode(Bukkit.getDefaultGameMode());
                                }
                            }.runTask(plugin);
                        }
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',GEN_BindSuccessMessage));
                    } else player.sendMessage(ChatColor.translateAlternateColorCodes('&',GEN_NotifyMessage));
                }
            }
        }
    }
}
