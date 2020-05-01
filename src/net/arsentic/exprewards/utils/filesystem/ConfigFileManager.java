package net.arsentic.exprewards.utils.filesystem;

import net.arsentic.exprewards.ExpRewards;
import net.arsentic.exprewards.core.Reward;
import net.arsentic.exprewards.core.gui.GUIManager;
import net.arsentic.exprewards.core.player.PlayerReward;
import net.arsentic.exprewards.core.player.PlayerRewardManager;
import net.arsentic.exprewards.utils.chat.Chat;
import org.bukkit.Material;
import org.bukkit.Sound;
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
    public static final String CTC_LORE = "click-to-claim-lore";
    public static final String MINUTES_ONLINE = "minutes-online";
    public static final String AVAILABLE_REWARDS = "available-rewards";
    public static final String CLAIMED_REWARDS = "claimed-rewards";
    public static final String LEVEL_UP = "level-up";
    public static final String TO_WARN = "warn";
    public static final String WARN_MESSAGE = "warn-message";
    public static final String PREFIX = "prefix";



    private ConfigFile mainConfigFile;
    private ConfigFile rewardsConfigFile;

    public static ConfigFileManager cfm = new ConfigFileManager();

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

        for(int expLvl : ExpRewards.sortedRewards) {
            if(expLvl > minutesOnline) continue;
            if(!availableRewards.contains(expLvl) && !claimedRewards.contains(expLvl)) {
                availableRewards.add(expLvl);
                if(Chat.TO_WARN) {
                    Reward reward = ExpRewards.rewards.get(expLvl);
                    playerReward.getPlayer().sendMessage(Chat.GET_WARN_MESSAGE(playerReward.getPlayer(), reward));
                }
            }
        }
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

        if(mainConfigFile.getConfigurationSection(LEVEL_UP) == null) mainConfigFile.setValue(LEVEL_UP, 1);
        ExpRewards.LEVEL_UP_MINS = (int) mainConfigFile.getValue(LEVEL_UP);

        if(mainConfigFile.getConfigurationSection(PREFIX) == null) mainConfigFile.setValue(PREFIX, "&bExpRewards &7&l>> &f");
        Chat.PREFFIX = (String) ConfigFileManager.cfm.getMainConfigFile().getValue(PREFIX);

        if(mainConfigFile.getConfigurationSection(WARN_MESSAGE) == null) mainConfigFile.setValue(WARN_MESSAGE, "&e&l%player_name% your level %exp_lvl% reward is ready!");
        Chat.WARN_MESSAGE = (String) ConfigFileManager.cfm.getMainConfigFile().getValue(WARN_MESSAGE);

        if(mainConfigFile.getConfigurationSection(WARN_MESSAGE) == null) mainConfigFile.setValue(TO_WARN, true);
        Chat.TO_WARN = (boolean) ConfigFileManager.cfm.getMainConfigFile().getValue(TO_WARN);
    }

    public void loadRewardsConfig() throws Exception{
        rewardsConfigFile = new ConfigFile(ExpRewards.getPlugin(), REWARDS_CONFIG, true).load();

        if(rewardsConfigFile.getConfigurationSection(INVENTORY_ROWS) == null) rewardsConfigFile.setValue(INVENTORY_ROWS, 2);
        GUIManager.ROWS = (int) rewardsConfigFile.getValue(INVENTORY_ROWS);
        GUIManager.ROWS = (GUIManager.ROWS > 2 && GUIManager.ROWS <= 6) ? GUIManager.ROWS : 4;

        GUIManager.SLOTS = GUIManager.ROWS * 9;

        if(rewardsConfigFile.getConfigurationSection(CLAIMED_LORE) == null) rewardsConfigFile.setValue(CLAIMED_LORE, "&b&lClaimed!");
        GUIManager.CLAIMED_LORE = (String) rewardsConfigFile.getValue(CLAIMED_LORE);

        if(rewardsConfigFile.getConfigurationSection(CTC_LORE) == null) rewardsConfigFile.setValue(CTC_LORE, "&eClick to claim!");
        GUIManager.CTC_LORE = (String) rewardsConfigFile.getValue(CTC_LORE);

        if(!rewardsConfigFile.exists(REWARDS)) {
            rewardsConfigFile.createSection(REWARDS);
            ExpRewards.getPlugin().warn("Rewards are missing!");
            return;
        }
        Set<String> rewards = rewardsConfigFile.getConfigurationSection(REWARDS).getKeys(false);
        if(rewards.isEmpty()) {
            ExpRewards.getPlugin().warn("Rewards are empty!!");
            return;
        }
        for(String reward : rewards) {
            if(!reward.matches("^\\d+$")) {
                throw new Exception("ERROR: '" + reward + "' is not a number!");
            }
            int lvl = Integer.parseInt(reward);

            if(!rewardsConfigFile.exists(REWARDS + "." + reward + ".gui-item")) {
                ExpRewards.getPlugin().warn("Material for '" + reward + "' is not found!");
                rewardsConfigFile.setValue(REWARDS + "." + reward + ".gui-item", "DIAMOND_BLOCK");
            }
            String material = (String) rewardsConfigFile.getValue(REWARDS + "." + reward + ".gui-item");

            ItemStack guiItem = new ItemStack(Material.valueOf(material));
            if(guiItem == null) guiItem = new ItemStack(Material.DIAMOND_BLOCK);
            ItemMeta itemMeta = guiItem.getItemMeta();

            if(!rewardsConfigFile.exists(REWARDS + "." + reward + ".display-name") || rewardsConfigFile.getValue(REWARDS + "." + reward + ".display-name") == null) {
                ExpRewards.getPlugin().warn("Display-name for '" + reward + "' is not found!");
                rewardsConfigFile.setValue(REWARDS + "." + reward + ".display-name", "&b&lReward!");
            }
            String displayName = (String) rewardsConfigFile.getValue(REWARDS + "." + reward + ".display-name");
            displayName = Chat.toColor(displayName);

            if(!rewardsConfigFile.exists(REWARDS + "." + reward + ".description") || rewardsConfigFile.getValue(REWARDS + "." + reward + ".description") == null) {
                ExpRewards.getPlugin().warn("description for '" + reward + "' is not found!");
                List<String> lore = new ArrayList<>();
                lore.add("&5Simple description!");
                rewardsConfigFile.setValue(REWARDS + "." + reward + ".description", lore);
            }
            List<String> lore = (ArrayList<String>) rewardsConfigFile.getValue(REWARDS + "." + reward + ".description");

            if(!rewardsConfigFile.exists(REWARDS + "." + reward + ".commands") || rewardsConfigFile.getValue(REWARDS + "." + reward + ".commands") == null) {
                ExpRewards.getPlugin().warn("commands for '" + reward + "' is not found!");
                List<String> commands = new ArrayList<>();
                commands.add("give %player_name% diamond_block 1");
                rewardsConfigFile.setValue(REWARDS + "." + reward + ".commands", commands);
            }
            List<String> commands = (ArrayList<String>) rewardsConfigFile.getValue(REWARDS + "." + reward + ".commands");

            itemMeta.setLocalizedName("" + lvl);
            itemMeta.setDisplayName(displayName);
            itemMeta.setLore(lore);
            guiItem.setItemMeta(itemMeta);

            Reward rewardObj = new Reward(lvl, commands, guiItem);

            Sound sound;
            if(!rewardsConfigFile.exists(REWARDS + "." + reward + ".sound") || rewardsConfigFile.getValue(REWARDS + "." + reward + ".sound") == null) {
                ExpRewards.getPlugin().warn("Sound for '" + reward + "' is not found!");
                rewardsConfigFile.setValue(REWARDS + "." + reward + ".sound", Sound.ENTITY_PLAYER_LEVELUP.toString());
            }
            sound =  Sound.valueOf((String) rewardsConfigFile.getValue(REWARDS + "." + reward + ".sound"));

            rewardObj.setSound(sound);
            ExpRewards.rewards.put(lvl, rewardObj);
        }
    }

    private ConfigFile getPlayersConfig(Player player) {
        return new ConfigFile(ExpRewards.getPlugin(), PLAYERS_FOLDER + File.separator + player.getUniqueId(), false).load();
    }

    public ConfigFile getMainConfigFile() {
        return mainConfigFile;
    }

    public ConfigFile getRewardsConfigFile() {
        return rewardsConfigFile;
    }
}
