package net.arsentic.exprewards;

import net.arsentic.exprewards.commands.ExpCommand;
import net.arsentic.exprewards.core.Reward;
import net.arsentic.exprewards.core.player.PlayerReward;
import net.arsentic.exprewards.core.player.PlayerRewardManager;
import net.arsentic.exprewards.listeners.GUIListener;
import net.arsentic.exprewards.listeners.JoinQuitListener;
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
        plugin.getServer().getPluginManager().registerEvents(new JoinQuitListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new GUIListener(), plugin);
    }

    private void loadConfigs() {
        ConfigFileManager.cfm.loadMainConfig();
        try {
            ConfigFileManager.cfm.loadRewardsConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void reloadConfigs() {
        rewards.clear();
        loadConfigs();
        sortRewards();
        for(Player player : Bukkit.getOnlinePlayers()) {
            ConfigFileManager.cfm.updatePlayerConfig(player);
            PlayerRewardManager.prw.getPlayerReward(player).reset();
            ConfigFileManager.cfm.loadPlayerConfig(player);
        }
    }

    public void warn(String text) {
        plugin.getLogger().warning(text);
    }

    public static ExpRewards getPlugin() {
        return plugin;
    }
}
