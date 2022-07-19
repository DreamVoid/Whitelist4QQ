package me.dreamvoid.whitelist4qq.bukkit;

import me.dreamvoid.miraimc.api.MiraiBot;
import me.dreamvoid.miraimc.api.MiraiMC;
import me.dreamvoid.miraimc.bukkit.event.group.member.MiraiMemberLeaveEvent;
import me.dreamvoid.miraimc.bukkit.event.message.passive.MiraiGroupMessageEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static me.dreamvoid.whitelist4qq.bukkit.Config.*;

public class BukkitPlugin extends JavaPlugin implements Listener {
    private Config config;
    private final ArrayList<Player> cache = new ArrayList<>();
    private static File whitelist;

    @Override
    public void onLoad() {
        this.config = new Config(this);
        whitelist = new File(getDataFolder(),"whitelist.yml");
    }

    @Override
    public void onEnable() {
        config.loadConfig();
        Bukkit.getPluginManager().registerEvents(new PlayerLogin(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        if(GEN_CheckRange_ACTION) Bukkit.getPluginManager().registerEvents(new PlayerActions(), this);
        Bukkit.getPluginManager().registerEvents(new BotEvent(), this);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new AsyncCheckBind(this), 0L, 60L);
        if (GEN_bStats) new Metrics(this, 13112);

        if(GEN_UseSelfData){
            if(!whitelist.exists()) {
                try {
                    whitelist.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("whitelist4qq.command.whitelist4qq.reload")) {
                config.loadConfig();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a配置文件已经重新加载！"));
            } else if(args[0].equalsIgnoreCase("remove") && sender.hasPermission("whitelist4qq.command.whitelist4qq.remove")){
                if(args.length>=2){
                    if(!GEN_UseSelfData){
                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                        MiraiMC.removeBind(player.getUniqueId());
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a已移除指定玩家的绑定！"));
                    } else {
                        YamlConfiguration white = YamlConfiguration.loadConfiguration(whitelist);
                        if(GEN_UsePlayerName){
                            try {
                                List<String> names = white.getStringList("name");
                                names.remove(args[1]);
                                white.set("name",names);
                                white.save(whitelist);
                            } catch (IOException e) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c保存文件时出现异常，原因："+e));
                            }
                        } else try {
                            List<String> uuids = white.getStringList("uuid");
                            uuids.remove(Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString());
                            white.set("uuid",uuids);
                            white.save(whitelist);
                        } catch (IOException e) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c保存文件时出现异常，原因："+e));
                        }
                    }
                } else sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c用法：/whitelist4qq remove <玩家名>"));
            } else {
                sender.sendMessage("This server is running " + getDescription().getName() + " version " + getDescription().getVersion() + " by " + getDescription().getAuthors().toString().replace("[", "").replace("]", "") + " (MiraiMC version " + Bukkit.getPluginManager().getPlugin("MiraiMC").getDescription().getVersion() + ")");
            }
        } else {
            sender.sendMessage("This server is running " + getDescription().getName() + " version " + getDescription().getVersion() + " by " + getDescription().getAuthors().toString().replace("[", "").replace("]", "") + " (MiraiMC version " + Bukkit.getPluginManager().getPlugin("MiraiMC").getDescription().getVersion() + ")");
        }
        return true;
    }

    /**
     * 玩家即将加入服务器事件
     */
    public static class PlayerLogin implements Listener {
        @EventHandler
        public void onPlayerJoin(AsyncPlayerPreLoginEvent e) {
            boolean allow = false;

            if(GEN_CheckRange_JOIN) { // 加入服务器的时候检测
                if (!(GEN_UseSelfData)) {
                    long binder = MiraiMC.getBind(e.getUniqueId());
                    if (binder != 0) {
                        // 是否需要进一步检测是否在群内
                        if (BOT_CheckQQInGroup) {
                            for (long group : BOT_UsedGroupAccounts) {
                                if (allow) break; // 如果下面的代码已经检测到在群里了，就不继续检测

                                for (long bot : BOT_UsedBotAccounts) {
                                    try { // 加个try防止服主忘记登录机器人然后尝试获取的时候报错的问题
                                        if (MiraiBot.getBot(bot).getGroup(group).contains(binder)) {
                                            allow = true;
                                            break;
                                        }
                                    } catch (NoSuchElementException ignored) {
                                    } // 不需要处理报错，直接ignored
                                }
                            }
                        } else allow = true; // 不需要则直接true
                    }
                } else {
                    YamlConfiguration white = YamlConfiguration.loadConfiguration(whitelist);
                    if(GEN_UsePlayerName){
                        List<String> names = white.getStringList("name");
                        allow = names.contains(e.getName());
                    } else {
                        List<String> uuids = white.getStringList("uuid");
                        allow = uuids.contains(e.getUniqueId().toString());
                    }
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
            boolean whitelisted = true;
            if(!GEN_UseSelfData){
                if (MiraiMC.getBind(e.getPlayer().getUniqueId()) == 0) {
                    whitelisted = false;
                }
            } else {
                YamlConfiguration white = YamlConfiguration.loadConfiguration(whitelist);
                if(GEN_UsePlayerName){
                    List<String> names = white.getStringList("name");
                    whitelisted = names.contains(e.getPlayer().getName());
                } else {
                    List<String> uuids = white.getStringList("uuid");
                    whitelisted = uuids.contains(e.getPlayer().getUniqueId().toString());
                }
            }

            if(!whitelisted){
                cache.add(e.getPlayer());
                if (GEN_CheckRange_SPEC) {
                    e.getPlayer().setGameMode(GameMode.SPECTATOR);
                }
            }
        }
    }

    /**
     * 玩家动作事件
     */
    public class PlayerActions implements Listener {
        @EventHandler
        public void onPlayerMove(PlayerMoveEvent e) {
            if(cache.contains(e.getPlayer()) && !GEN_CheckRange_SPEC)
                e.setCancelled(true);
        }

        @EventHandler
        public void onPlayerInteract(PlayerInteractEvent e) {
            if(cache.contains(e.getPlayer()))
                e.setCancelled(true);
        }

        @EventHandler
        public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent e) {
            if(cache.contains(e.getPlayer()))
                e.setCancelled(true);
        }

        @EventHandler
        public void onPlayerBedEnter(PlayerBedEnterEvent e) {
            if(cache.contains(e.getPlayer()))
                e.setCancelled(true);
        }

        @EventHandler
        public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
            if(cache.contains(e.getPlayer()))
                e.setCancelled(true);
        }

        @EventHandler
        public void onPlayerVelocity(PlayerVelocityEvent e) {
            if(cache.contains(e.getPlayer()))
                e.setCancelled(true);
        }

        @EventHandler
        public void onPlayerChat(AsyncPlayerChatEvent e) {
            if(cache.contains(e.getPlayer()))
                e.setCancelled(true);
        }

        @EventHandler
        public void onPlayerDropItem(PlayerDropItemEvent e) {
            if(cache.contains(e.getPlayer()))
                e.setCancelled(true);
        }

        @EventHandler
        public void onPlayerFish(PlayerFishEvent e) {
            if(cache.contains(e.getPlayer()))
                e.setCancelled(true);
        }
    }

    /**
     * 机器人事件
     */
    public static class BotEvent implements Listener {
        @EventHandler
        public void onGroupMessage(MiraiGroupMessageEvent e) {
            if (BOT_UsedBotAccounts.contains(e.getBotID()) && BOT_UsedGroupAccounts.contains(e.getGroupID()) && BOT_UseGroupMessageCommand && e.getMessage().startsWith(BOT_BindCommandPrefix)) {
                String playerName = e.getMessage().replace(BOT_BindCommandPrefix, "");
                if(!GEN_UseSelfData){
                    if ((GEN_PreventIDRebind && (MiraiMC.getBind(Bukkit.getOfflinePlayer(playerName).getUniqueId()) != 0)) || (GEN_PreventQQRebind && (MiraiMC.getBind(e.getSenderID()) != null))) {
                        // 阻止绑定
                        MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(BOT_Messages_BindFailed.replace("%id%", Bukkit.getOfflinePlayer(MiraiMC.getBind(e.getSenderID())).getName()));
                    } else {
                        // 允许绑定
                        MiraiMC.addBind(Bukkit.getOfflinePlayer(e.getMessage().replace(BOT_BindCommandPrefix, "")).getUniqueId(), e.getSenderID());
                        MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(BOT_Messages_BindSuccess);
                    }
                } else {
                    YamlConfiguration white = YamlConfiguration.loadConfiguration(whitelist);
                    if(GEN_UsePlayerName){
                        List<String> names = white.getStringList("name");
                        if (GEN_PreventIDRebind && names.contains(playerName)) {
                            MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(BOT_Messages_BindFailed);
                        } else try {
                            names.add(playerName);
                            white.set("name", names);
                            white.save(whitelist);
                            MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(BOT_Messages_BindSuccess);
                        } catch (IOException ex) {
                            MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(BOT_Messages_BindFailedSelfdata);
                            throw new RuntimeException(ex);
                        }
                    } else {
                        List<String> uuids = white.getStringList("uuid");
                        if (GEN_PreventIDRebind && uuids.contains(Bukkit.getOfflinePlayer(playerName).getUniqueId().toString())) {
                            MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(BOT_Messages_BindFailed);
                        } else try {
                            uuids.add(Bukkit.getOfflinePlayer(playerName).getUniqueId().toString());
                            white.set("uuid", uuids);
                            white.save(whitelist);
                            MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(BOT_Messages_BindSuccess);
                        } catch (IOException ex) {
                            MiraiBot.getBot(e.getBotID()).getGroup(e.getGroupID()).sendMessage(BOT_Messages_BindFailedSelfdata);
                            throw new RuntimeException(ex);
                        }

                    }
                }
            }
        }

        @EventHandler
        public void onGroupQuit(MiraiMemberLeaveEvent e) {
            if (!GEN_UseSelfData && BOT_UsedBotAccounts.contains(e.getBotID()) && BOT_UsedGroupAccounts.contains(e.getGroupID()) && BOT_RemoveBindWhenQQQuit) {
                MiraiMC.removeBind(e.getTargetID());
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
                for(Player player : cache){
                    if(!GEN_UseSelfData){
                        if(MiraiMC.getBind(player.getUniqueId()) != 0) {
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
                    } else {
                        YamlConfiguration white = YamlConfiguration.loadConfiguration(whitelist);
                        if(GEN_UsePlayerName){
                            List<String> names = white.getStringList("name");
                            if(names.contains(player.getName())){
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
                            }
                        } else {
                            List<String> uuids = white.getStringList("uuid");
                            if(uuids.contains(player.getUniqueId().toString())){
                                cache.remove(player);
                                if(GEN_CheckRange_SPEC) {
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            player.setGameMode(Bukkit.getDefaultGameMode());
                                        }
                                    }.runTask(plugin);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
