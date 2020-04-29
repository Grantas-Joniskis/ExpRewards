package net.arsentic.exprewards.utils.chat;

public class Chat {

    public static final String BARE_PREFFIX = "[ExpRewards] ";

    public static String toColor(String text) {
        return text.replaceAll("&", "§");
    }
}
