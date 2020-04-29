package net.arsentic.exprewards.listeners;

import net.arsentic.exprewards.ExpRewards;
import net.arsentic.exprewards.core.Reward;
import net.arsentic.exprewards.core.gui.GUIManager;
import net.arsentic.exprewards.core.gui.RewardGUI;
import net.arsentic.exprewards.core.player.PlayerReward;
import net.arsentic.exprewards.core.player.PlayerRewardManager;
import net.arsentic.exprewards.utils.chat.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class GUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(!(event.getInventory().getHolder() instanceof RewardGUI)) return;
        if(event.getCurrentItem() == null || event.getCurrentItem().getData().getItemType() == Material.AIR) return;
        if(!(event.getRawSlot() < (GUIManager.SLOTS))) return;

        Player player = (Player) event.getWhoClicked();
        PlayerReward playerReward = PlayerRewardManager.prw.getPlayerReward(player);
        ItemMeta itemMeta = event.getCurrentItem().getItemMeta();

        if(event.getRawSlot() > 8) {

            String lvlString = itemMeta.getLocalizedName();
            int lvl = Integer.parseInt(lvlString);
            Reward reward = ExpRewards.rewards.get(lvl);

            if(playerReward.hasClaimed(lvl)) {
                event.setCancelled(true);
                return;
            } else if(playerReward.canClaim(lvl)) {
                playerReward.removeAvailableReward(lvl);
                playerReward.addClaimedReward(lvl);
                player.closeInventory();

                for(String command : reward.getCommands()) {
                    System.out.println(command);
                    command = command.replace(Placeholder.PLAYER_NAME.getVariable(), (String) Placeholder.PLAYER_NAME.getValue(player));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            } else {
                player.sendMessage("You need to play " + (reward.getLevel() - playerReward.getMinutes()) + " minutes more!");
            }
        }
        event.setCancelled(true);
    }
}
