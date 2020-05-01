package net.arsentic.exprewards.core.player;

import net.arsentic.exprewards.core.gui.RewardGUI;
import net.arsentic.exprewards.utils.filesystem.ConfigFile;
import net.arsentic.exprewards.utils.filesystem.ConfigFileManager;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerReward {

    private Player player;
    private ConfigFile configFile;
    private RewardGUI gui;
    private int minutes;

    private Set<Integer> claimedRewards;
    private Set<Integer> availableRewards;

    public PlayerReward(Player player) {
        claimedRewards = new HashSet<>();
        availableRewards = new HashSet<>();
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public int getMinutes() {
        return minutes;
    }

    public void countMinutes() {
        minutes++;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public ConfigFile getConfigFile() {
        return configFile;
    }

    public void loadConfigFile() {
        this.configFile = ConfigFileManager.cfm.loadPlayerConfig(player);
    }

    public void addAvailableReward(int expLvl) {
        availableRewards.add(expLvl);
    }

    public void addClaimedReward(int expLvl) {
        claimedRewards.add(expLvl);
    }

    public void removeAvailableReward(int expLvl) {
        availableRewards.remove(expLvl);
    }

    public void reset() {
        this.minutes = 0;
        this.claimedRewards.clear();
        this.availableRewards.clear();
    }

    public void setGui(RewardGUI gui) {
        this.gui = gui;
    }

    public RewardGUI getGui() {
        return gui;
    }

    public void setAvailableRewards(Set<Integer> availableRewards) {
        this.availableRewards = availableRewards;
    }

    public void setClaimedRewards(Set<Integer> claimedRewards) {
        this.claimedRewards = claimedRewards;
    }

    public boolean hasClaimed(int expLvl) {
        return claimedRewards.contains(expLvl);
    }

    public boolean canClaim(int expLvl) {
        return availableRewards.contains(expLvl);
    }

    public Set<Integer> getAvailableRewards() {
        return availableRewards;
    }

    public Set<Integer> getClaimedRewards() {
        return claimedRewards;
    }
}
