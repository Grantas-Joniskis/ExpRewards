package net.arsentic.exprewards.core.player;

import net.arsentic.exprewards.ExpRewards;
import net.arsentic.exprewards.core.Reward;
import net.arsentic.exprewards.core.gui.RewardGUI;
import net.arsentic.exprewards.utils.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRewardManager {

    public static PlayerRewardManager prw = new PlayerRewardManager();

    public Map<UUID, Integer> onlineTasks = new HashMap<>();
    public Map<Player, PlayerReward> playerRewards = new HashMap<>();

    public void startTask(Player player) {
        PlayerReward playerReward = playerRewards.get(player);
        int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(ExpRewards.getPlugin(), new Runnable() {
            @Override
            public void run() {
                playerReward.countMinutes();
                if(ExpRewards.rewards.containsKey(playerReward.getMinutes())) {
                    Reward reward = ExpRewards.rewards.get(playerReward.getMinutes());
                    playerReward.addAvailableReward(reward.getLevel());
                    if (Chat.TO_WARN) player.sendMessage(Chat.toColor(Chat.GET_WARN_MESSAGE(player, reward)));
                } if(player.getOpenInventory().getTopInventory().getHolder() instanceof RewardGUI){
                    playerReward.getGui().refresh();
                }
            }
        }, 0, 60 * 20 * ExpRewards.LEVEL_UP_MINS);
        onlineTasks.put(player.getUniqueId(), task);
    }


    public void cancelTask(Player player) {
        if(!onlineTasks.containsKey(player.getUniqueId())) return;
        int task = getTaskID(player);
        Bukkit.getScheduler().cancelTask(task);
        onlineTasks.remove(player.getUniqueId());
    }

    public void loadToRewards(Player player) {
        if(containsPlayerReward(player)) return;
        PlayerReward playerReward = new PlayerReward(player);
        playerRewards.put(player, playerReward);
    }

    public void removeFromRewards(Player player) {
        if(!containsPlayerReward(player)) return;
        playerRewards.remove(player);
    }

    public PlayerReward getPlayerReward(Player player) {
        PlayerReward playerReward = playerRewards.get(player);
        if(playerReward == null) throw new NullPointerException("CRITICAL ERROR: Player Reward Class cannot be NULL !");
        return playerReward;
    }

    public boolean containsPlayerReward(Player player) {
        return playerRewards.containsKey(player);
    }

    public int getTaskID(Player player) {
        return onlineTasks.get(player.getUniqueId());
    }
}

