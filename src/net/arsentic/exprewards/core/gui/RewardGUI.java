package net.arsentic.exprewards.core.gui;

import net.arsentic.exprewards.ExpRewards;
import net.arsentic.exprewards.core.Reward;
import net.arsentic.exprewards.core.player.PlayerReward;
import net.arsentic.exprewards.utils.chat.Chat;
import net.arsentic.exprewards.utils.chat.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Set;

public class RewardGUI implements InventoryHolder {

    private PlayerReward playerReward;
    private Inventory inventory;
    private ItemStack itemNext;
    private ItemStack itemPrevious;
    private ItemStack itemTop;


    public RewardGUI(PlayerReward playerReward) {
        this.playerReward = playerReward;
        System.out.println(GUIManager.SLOTS);
        this.inventory = Bukkit.createInventory(this, GUIManager.SLOTS, Chat.toColor("&b&lRewards"));
        loadDefaultItems();
        loadRewardItems();
    }

    private void loadDefaultItems() {
        itemNext = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
        itemPrevious = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
        itemTop = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7);

        ItemMeta metaNext = itemNext.getItemMeta();
        ItemMeta metaPrevious = itemPrevious.getItemMeta();
        ItemMeta metaTop = itemTop.getItemMeta();

        metaNext.setDisplayName(Chat.toColor("&a&lNEXT"));
        metaPrevious.setDisplayName(Chat.toColor("&c&lPREVIOUS"));
        metaTop.setDisplayName(Chat.toColor("`"));

        itemNext.setItemMeta(metaNext);
        itemPrevious.setItemMeta(metaPrevious);
        itemTop.setItemMeta(metaTop);
        if(inventory == null) System.out.println("INVENTORY IS NULL!!");
        inventory.setItem(0, itemPrevious);
        for(int i = 1; i <= 7; i++) inventory.setItem(i, itemTop);
        inventory.setItem(8, itemNext);
    }

    public void loadRewardItems() {
        Set<Integer> claimedRewards = playerReward.getClaimedRewards();
        Set<Integer> availableRewards = playerReward.getAvailableRewards();

        for (int expLvl : ExpRewards.sortedRewards) {

            Reward reward = ExpRewards.rewards.get(expLvl);
            ItemStack rewardItem = reward.getGuiItem();
            ItemMeta rewardMeta = rewardItem.getItemMeta();
            List<String> lore = rewardMeta.getLore();

            for(int i = 0; i < lore.size(); i++) {
                String line = lore.get(i);
                line = line.replace(Placeholder.EXP_LVL.getVariable(), "" + Placeholder.EXP_LVL.getValue(reward));
                line = line.replace(Placeholder.PLAYER_NAME.getVariable(), (String) Placeholder.PLAYER_NAME.getValue(playerReward.getPlayer()));
                line = Chat.toColor(line);
                lore.set(i, line);
            }
            rewardMeta.setLore(lore);
            rewardItem.setItemMeta(rewardMeta);

            if (claimedRewards.contains(expLvl)) {
                List<String> extraLore = rewardMeta.getLore();

                if(extraLore.contains(Chat.toColor(GUIManager.CTC_LORE))) extraLore.remove(extraLore.size()-1);
                if(!extraLore.contains(Chat.toColor(GUIManager.CLAIMED_LORE))) extraLore.add(Chat.toColor(GUIManager.CLAIMED_LORE));
                rewardMeta.setLore(extraLore);

                rewardItem.setItemMeta(rewardMeta);
                rewardItem.removeEnchantment(Enchantment.DIG_SPEED);


            } else if (availableRewards.contains(expLvl)) {
                rewardItem.addUnsafeEnchantment(Enchantment.DIG_SPEED, 1);
                rewardMeta = rewardItem.getItemMeta();
                List<String> extraLore = rewardMeta.getLore();

                if(!extraLore.contains(Chat.toColor(GUIManager.CTC_LORE))) {
                    extraLore.add(Chat.toColor(GUIManager.CTC_LORE));
                    rewardMeta.setLore(extraLore);
                }

                rewardMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                rewardItem.setItemMeta(rewardMeta);
            }
            inventory.addItem(rewardItem);
        }
    }

    public void refresh() {

    }

    public void openInventory() {
        playerReward.getPlayer().openInventory(inventory);
    }

    public ItemStack getItemNext() {
        return itemNext;
    }

    public ItemStack getItemPrevious() {
        return itemPrevious;
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }
}
