package net.arsentic.exprewards;

import net.arsentic.exprewards.commands.ExpCommand;
import net.arsentic.exprewards.core.Reward;
import net.arsentic.exprewards.core.player.PlayerReward;
import net.arsentic.exprewards.core.player.PlayerRewardManager;
import net.arsentic.exprewards.listeners.GUIListener;
import net.arsentic.exprewards.listeners.PlayerJoinListener;
import net.arsentic.exprewards.listeners.PlayerQuitListener;
import net.arsentic.exprewards.utils.filesystem.ConfigFileManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ExpRewards extends JavaPlugin {

    private static ExpRewards plugin;
    public static int LEVEL_UP_MINS;

    public static final Map<Integer, Reward> rewards = new HashMap<>();
    public static List<Integer> sortedRewards;

    @Override
    public void onEnable() {
        plugin = this;
        plugin.loadConfigs();
        plugin.sortRewards();
        plugin.registerCommands();
        plugin.registerListeners();
        plugin.loadOnlinePlayers();
        plugin.getLogger().info("Has been enabled!");
    }

    @Override
    public void onDisable() {
        plugin.saveOnlinePlayers();
        plugin.getLogger().info("Has been disabled!");
        plugin = null;
    }

    public void registerListeners() {
        plugin.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new GUIListener(), plugin);
    }

    private void loadConfigs() {
        ConfigFileManager.cfm.loadMainConfig();
        ConfigFileManager.cfm.loadRewarsConfig();
    }

    private void registerCommands() {
        plugin.getCommand("experience").setExecutor(new ExpCommand());
    }

    private void sortRewards() {
        sortedRewards = new ArrayList<>(rewards.keySet());
        Collections.sort(sortedRewards);
    }

    private void saveOnlinePlayers() {
        for(Player player : Bukkit.getServer().getOnlinePlayers()) {
            PlayerRewardManager.prw.cancelTask(player);
            ConfigFileManager.cfm.updatePlayerConfig(player);
            PlayerRewardManager.prw.removeFromRewards(player);
        }
    }

    private void loadOnlinePlayers() {
        for(Player player : Bukkit.getServer().getOnlinePlayers()) {
            PlayerRewardManager.prw.loadToRewards(player);
            PlayerReward playerReward = PlayerRewardManager.prw.getPlayerReward(player);
            playerReward.loadConfigFile();
            PlayerRewardManager.prw.startTask(player);
        }
    }

    public static ExpRewards getPlugin() {
        return plugin;
    }
}
