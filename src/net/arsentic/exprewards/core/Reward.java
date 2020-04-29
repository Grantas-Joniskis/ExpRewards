package net.arsentic.exprewards.core;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Reward {

    private final List<String> commands;
    private final int level;
    private ItemStack guiItem;

    public Reward(int level, List<String> commands) {
        this.level = level;
        this.commands = commands;
        this.guiItem = null;
    }

    public Reward(int level, List<String> commands, ItemStack guiItem) {
        this.level = level;
        this.commands = commands;
        this.guiItem = guiItem;
    }

    public void setGuiItem(ItemStack guiItem) {
        this.guiItem = guiItem;
    }

    public List<String> getCommands() {
        return commands;
    }

    public int getLevel() {
        return level;
    }

    public ItemStack getGuiItem() {
        return guiItem;
    }



}
