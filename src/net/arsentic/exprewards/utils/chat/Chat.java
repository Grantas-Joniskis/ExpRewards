package net.arsentic.exprewards.utils.chat;

import net.arsentic.exprewards.core.Reward;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Chat {

    public static String PREFFIX;
    public static String WARN_MESSAGE;
    public static boolean TO_WARN;

    public static String toColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String GET_WARN_MESSAGE(Player player, Reward reward) {
        String text = WARN_MESSAGE;
        text = text.replace(Placeholder.PLAYER_NAME.getVariable(), (String) Placeholder.PLAYER_NAME.getValue(player));
        text = text.replace(Placeholder.EXP_LVL.getVariable(), "" + Placeholder.EXP_LVL.getValue(reward));
        return Chat.toColor(text);
    }
}
