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
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
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
                playerReward.setGui(null);
                player.playSound(player.getLocation(), reward.getSound(), 1.0F, 1.0F);

                for(String command : reward.getCommands()) {
                    command = command.replace(Placeholder.PLAYER_NAME.getVariable(), (String) Placeholder.PLAYER_NAME.getValue(player));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            }
        } else {
            RewardGUI rewardGUI = (RewardGUI) event.getInventory().getHolder();
            if(event.getCurrentItem().isSimilar(rewardGUI.getItemNext())) {
                if(rewardGUI.getItemNext().getData().getData() == (byte) 5) {
                    RewardGUI nextGUI = new RewardGUI(playerReward,rewardGUI.getPage() + 1);
                    nextGUI.openInventory();
                }
            } else if(event.getCurrentItem().isSimilar(rewardGUI.getItemPrevious())) {
                if(rewardGUI.getItemPrevious().getData().getData() == (byte) 5){
                    RewardGUI nextGUI = new RewardGUI(playerReward,rewardGUI.getPage() - 1);
                    nextGUI.openInventory();
                }
            }
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        PlayerReward playerReward = PlayerRewardManager.prw.getPlayerReward((Player) event.getPlayer());
        if(inventory.getHolder() instanceof RewardGUI) {
            playerReward.setGui(null);
        }
    }
}
