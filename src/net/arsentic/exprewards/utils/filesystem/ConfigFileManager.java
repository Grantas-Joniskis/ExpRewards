package net.arsentic.exprewards.utils.filesystem;

import net.arsentic.exprewards.ExpRewards;
import net.arsentic.exprewards.core.Reward;
import net.arsentic.exprewards.core.gui.GUIManager;
import net.arsentic.exprewards.core.player.PlayerReward;
import net.arsentic.exprewards.core.player.PlayerRewardManager;
import net.arsentic.exprewards.utils.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

public class ConfigFileManager {

    public static final String PLAYERS_FOLDER = "Players";
    public static final String MAIN_CONFIG = "config";
    public static final String REWARDS_CONFIG = "rewards";

    public static final String REWARDS = "rewards";
    public static final String INVENTORY_ROWS = "inventory-rows";
    public static final String CLAIMED_LORE = "claimed-lore";
    public static final String EXP_LVL_LORE = "exp-lvl-lore";
    public static final String CTC_LORE = "click-to-claim-lore";
    public static final String MINUTES_ONLINE = "minutes-online";
    public static final String AVAILABLE_REWARDS = "available-rewards";
    public static final String CLAIMED_REWARDS = "claimed-rewards";
    public static final String LEVEL_UP = "level-up";


    public ConfigFile mainConfigFile;
    public ConfigFile rewardsConfigFile;

    public static ConfigFileManager cfm = new ConfigFileManager();

    public void saveAllPlayersConfigs() {
        for(Player player : Bukkit.getServer().getOnlinePlayers()) {
            updatePlayerConfig(player);
        }
    }

    public ConfigFile loadPlayerConfig(Player player) {
        ConfigFile playerConfig = getPlayersConfig(player).load();
        PlayerReward playerReward = PlayerRewardManager.prw.getPlayerReward(player);
        loadPlayerConfigData(playerReward, playerConfig);
        return playerConfig;
    }


    private void loadPlayerConfigData(PlayerReward playerReward, ConfigFile playerConfig) {
        int minutesOnline = 0;
        Set<Integer> availableRewards = new HashSet<>();
        Set<Integer> claimedRewards = new HashSet<>();

        if(playerConfig.getValue(MINUTES_ONLINE) == null) playerConfig.setValue(MINUTES_ONLINE, 0);
        else minutesOnline = (int) playerConfig.getValue(MINUTES_ONLINE);
        playerReward.setMinutes(minutesOnline);

        if(playerConfig.getValue(AVAILABLE_REWARDS) == null) playerConfig.setValue(AVAILABLE_REWARDS, availableRewards);
        else availableRewards = (Set<Integer>) playerConfig.getValue(AVAILABLE_REWARDS);

        if(playerConfig.getValue(CLAIMED_REWARDS) == null) playerConfig.setValue(CLAIMED_REWARDS, claimedRewards);
        else claimedRewards = (Set<Integer>) playerConfig.getValue(CLAIMED_REWARDS);

        playerReward.setAvailableRewards(availableRewards);
        playerReward.setClaimedRewards(claimedRewards);
    }


    public void updatePlayerConfig(Player player) {
        PlayerReward playerReward = PlayerRewardManager.prw.getPlayerReward(player);
        ConfigFile playerConfig = playerReward.getConfigFile();
        playerConfig.setValue(MINUTES_ONLINE, playerReward.getMinutes());
        playerConfig.setValue(AVAILABLE_REWARDS, playerReward.getAvailableRewards());
        playerConfig.setValue(CLAIMED_REWARDS, playerReward.getClaimedRewards());
        playerConfig.save();
    }

    public void loadMainConfig() {
        mainConfigFile = new ConfigFile(ExpRewards.getPlugin(), MAIN_CONFIG, true).load();
        ExpRewards.LEVEL_UP_MINS = (int) mainConfigFile.getValue(LEVEL_UP);
    }

    public void loadRewarsConfig() {
        rewardsConfigFile = new ConfigFile(ExpRewards.getPlugin(), REWARDS_CONFIG, true).load();
        GUIManager.ROWS = (int) rewardsConfigFile.getValue(INVENTORY_ROWS);
        GUIManager.SLOTS = GUIManager.ROWS * 9;
        GUIManager.CLAIMED_LORE = (String) rewardsConfigFile.getValue(CLAIMED_LORE);
        GUIManager.EXP_LVL_LORE = (String) rewardsConfigFile.getValue(EXP_LVL_LORE);
        GUIManager.CTC_LORE = (String) rewardsConfigFile.getValue(CTC_LORE);

        Set<String> rewards = rewardsConfigFile.getConfigurationSection("rewards").getKeys(false);
        for(String reward : rewards) {
            int lvl = Integer.parseInt(reward);
            String material = (String) rewardsConfigFile.getValue(REWARDS + "." + reward + ".gui-item");

            ItemStack guiItem = new ItemStack(Material.valueOf(material));
            ItemMeta itemMeta = guiItem.getItemMeta();

            String displayName = (String) rewardsConfigFile.getValue(REWARDS + "." + reward + ".display-name");
            displayName = Chat.toColor(displayName);
            List<String> lore = (ArrayList<String>) rewardsConfigFile.getValue(REWARDS + "." + reward + ".description");

            itemMeta.setLocalizedName("" + lvl);
            itemMeta.setDisplayName(displayName);
            itemMeta.setLore(lore);
            guiItem.setItemMeta(itemMeta);

            List<String> commands = (ArrayList<String>) rewardsConfigFile.getValue(REWARDS + "." + reward + ".commands");

            Reward rewardObj = new Reward(lvl, commands, guiItem);
            ExpRewards.rewards.put(lvl, rewardObj);
            System.out.println(ExpRewards.rewards.get(lvl).getGuiItem().toString());
        }
    }

    private ConfigFile getPlayersConfig(Player player) {
        return new ConfigFile(ExpRewards.getPlugin(), PLAYERS_FOLDER + File.separator + player.getUniqueId(), false).load();
    }
}
