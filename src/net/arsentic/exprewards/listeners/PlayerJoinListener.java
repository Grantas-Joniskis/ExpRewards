package net.arsentic.exprewards.listeners;

import net.arsentic.exprewards.core.player.PlayerReward;
import net.arsentic.exprewards.core.player.PlayerRewardManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerRewardManager.prw.loadToRewards(player);
        PlayerReward playerReward = PlayerRewardManager.prw.getPlayerReward(player);
        playerReward.loadConfigFile();
        PlayerRewardManager.prw.startTask(player);
    }
}
