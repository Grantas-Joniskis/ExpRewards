package net.arsentic.exprewards.listeners;

import net.arsentic.exprewards.core.player.PlayerRewardManager;
import net.arsentic.exprewards.utils.filesystem.ConfigFileManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerRewardManager.prw.cancelTask(player);
        ConfigFileManager.cfm.updatePlayerConfig(player);
        PlayerRewardManager.prw.removeFromRewards(player);
    }
}

