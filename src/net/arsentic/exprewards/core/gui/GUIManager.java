package net.arsentic.exprewards.core.gui;

import net.arsentic.exprewards.ExpRewards;
import net.arsentic.exprewards.core.Reward;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GUIManager {
    public static int ROWS;
    public static int SLOTS;
    public static String CLAIMED_LORE;
    public static String CTC_LORE;

    public static List<Reward> getPageRewards(int page) {
        int maxSlot = (page * SLOTS) - (page * 9);
        int minSlot = maxSlot - (SLOTS - 9);

        List<Reward> pageRewards = new ArrayList<>();
        for(int i = minSlot; i < maxSlot; i++) {
            try {
                int lvl = ExpRewards.sortedRewards.get(i);
                pageRewards.add(ExpRewards.rewards.get(lvl));
            } catch (IndexOutOfBoundsException e) {
                return pageRewards;
            }
        }
        return pageRewards;
    }

    public static boolean isPageValid(int page) {
        if(page <= 0) return false;

        int maxSlot = (page * SLOTS) - (page * 9);
        int minSlot = maxSlot - (SLOTS - 9);

        return minSlot < ExpRewards.rewards.size();
    }
}
