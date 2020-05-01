package net.arsentic.exprewards.core.gui;

import net.arsentic.exprewards.ExpRewards;
import net.arsentic.exprewards.core.Reward;
import net.arsentic.exprewards.core.player.PlayerReward;
import net.arsentic.exprewards.utils.chat.Chat;
import net.arsentic.exprewards.utils.chat.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RewardGUI implements InventoryHolder {

    private PlayerReward playerReward;
    private Inventory inventory;
    private ItemStack itemNext;
    private ItemStack itemPrevious;
    private ItemStack itemTop;
    private ItemStack itemStats;
    private int page;


    public RewardGUI(PlayerReward playerReward, int page) {
        this.page = page;
        this.playerReward = playerReward;
        this.playerReward.setGui(this);
        this.inventory = Bukkit.createInventory(this, GUIManager.SLOTS, Chat.toColor("&b&lRewards"));
        loadDefaultItems();
        loadRewardItems();
    }

    private void loadDefaultItems() {

        ItemMeta metaNext;
        ItemMeta metaPrevious;
        ItemMeta metaTop;

        itemTop = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7);
        metaTop = itemTop.getItemMeta();
        metaTop.setDisplayName(Chat.toColor("`"));

        itemStats = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());
        SkullMeta statsMeta = (SkullMeta) itemStats.getItemMeta();
        statsMeta.setOwningPlayer(playerReward.getPlayer());
        statsMeta.setDisplayName(Chat.toColor("&d&l" + playerReward.getPlayer().getName()));

        List<String> lore = new ArrayList<>();
        lore.add(Chat.toColor("&b-----&d-----&e-----&c-----&6-----&a-----"));
        lore.add(Chat.toColor("&bExperience - &5" + playerReward.getMinutes() + " &bminutes"));
        lore.add(Chat.toColor("&fTotal rewards claimed - &5" + playerReward.getClaimedRewards().size()));
        int nextReward = 0;
        for(int i = 0; i < ExpRewards.sortedRewards.size(); i++) {
            if(playerReward.getMinutes() < ExpRewards.sortedRewards.get(i)) {
                nextReward = ExpRewards.sortedRewards.get(i) - playerReward.getMinutes();
                break;
            }
        }
        String nextRewardLore = nextReward == 0 ? "&bAll rewards are claimed!" : "&bNext reward in &5" + nextReward + " &bminutes.";
        if(nextReward == 0 && playerReward.getAvailableRewards().size() != 0) nextRewardLore = "&5" + playerReward.getAvailableRewards().size() + " &brewards are ready to be claimed!";
        lore.add(Chat.toColor(nextRewardLore));
        lore.add(Chat.toColor("&b-----&d-----&e-----&c-----&6-----&a-----"));
        statsMeta.setLore(lore);

        if(GUIManager.isPageValid(page + 1)) {
            itemNext = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
            metaNext = itemNext.getItemMeta();
            metaNext.setDisplayName(Chat.toColor("&a&lNEXT"));

        } else {
            itemNext = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
            metaNext = itemNext.getItemMeta();
            metaNext.setDisplayName(Chat.toColor("&c&lNEXT"));
        }

        if(GUIManager.isPageValid(page - 1)) {
            itemPrevious = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
            metaPrevious = itemPrevious.getItemMeta();
            metaPrevious.setDisplayName(Chat.toColor("&a&lPREVIOUS"));
        } else {
            itemPrevious = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
            metaPrevious = itemPrevious.getItemMeta();
            metaPrevious.setDisplayName(Chat.toColor("&c&lPREVIOUS"));
        }

        itemStats.setItemMeta(statsMeta);
        itemNext.setItemMeta(metaNext);
        itemPrevious.setItemMeta(metaPrevious);
        itemTop.setItemMeta(metaTop);

        inventory.setItem(0, itemPrevious);
        for(int i = 1; i <= 7; i++) {
            if(i == 4) inventory.setItem(i, itemStats);
            else inventory.setItem(i, itemTop);
        }
        inventory.setItem(8, itemNext);
    }

    public void loadRewardItems() {
        Set<Integer> claimedRewards = playerReward.getClaimedRewards();
        Set<Integer> availableRewards = playerReward.getAvailableRewards();
        List<Reward> rewards = GUIManager.getPageRewards(page);
        int slotCounter = 8;

        for (Reward reward : rewards) {
            int expLvl = reward.getLevel();
            ItemStack rewardItem = reward.getGuiItem();
            ItemMeta rewardMeta = rewardItem.getItemMeta();
            List<String> lore = rewardMeta.getLore();

            for(int i = 0; i < lore.size(); i++) {
                String line = lore.get(i);
                line = line.replace(Placeholder.EXP_LVL.getVariable(), "" + Placeholder.EXP_LVL.getValue(reward));
                line = line.replace(Placeholder.PLAYER_NAME.getVariable(), (String) Placeholder.PLAYER_NAME.getValue(playerReward.getPlayer()));
                line = line.replace(Placeholder.TIME_LEFT.getVariable(), (playerReward.getMinutes() < expLvl) ? (expLvl - playerReward.getMinutes() + "") : "0");
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
            slotCounter++;
            inventory.setItem(slotCounter, rewardItem);
        }
    }

    public void refresh() {
        inventory.clear();
        inventory.setItem(0, itemPrevious);
        for(int i = 1; i <= 7; i++) {
            if(i == 4) {
                updateSkull();
                inventory.setItem(i, itemStats);
            }
            else inventory.setItem(i, itemTop);
        }
        inventory.setItem(8, itemNext);
        loadRewardItems();
        playerReward.getPlayer().updateInventory();
    }

    private void updateSkull() {
        SkullMeta skullMeta = (SkullMeta) this.itemStats.getItemMeta();
        List<String> lore = skullMeta.getLore();
        lore.set(1, Chat.toColor("&bExperience - &5" + playerReward.getMinutes() + " &bminutes"));
        int nextReward = 0;
        for(int i = 0; i < ExpRewards.sortedRewards.size(); i++) {
            if(playerReward.getMinutes() < ExpRewards.sortedRewards.get(i)) {
                nextReward = ExpRewards.sortedRewards.get(i) - playerReward.getMinutes();
                break;
            }
        }
        String nextRewardLore = nextReward == 0 ? "&bAll rewards are claimed!" : "&bNext reward in &5" + nextReward + " &bminutes.";
        if(nextReward == 0 && playerReward.getAvailableRewards().size() != 0) nextRewardLore = "&5" + playerReward.getAvailableRewards().size() + " &brewards are ready to be claimed!";
        lore.set(3, Chat.toColor(nextRewardLore));
        skullMeta.setLore(lore);
        this.itemStats.setItemMeta(skullMeta);
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

    public int getPage() {
        return page;
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }
}
