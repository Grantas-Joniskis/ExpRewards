package net.arsentic.exprewards.commands;

import net.arsentic.exprewards.ExpRewards;
import net.arsentic.exprewards.core.gui.RewardGUI;
import net.arsentic.exprewards.core.player.PlayerReward;
import net.arsentic.exprewards.core.player.PlayerRewardManager;
import net.arsentic.exprewards.utils.chat.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExpCommand implements CommandExecutor {

    private static final String ADMIN_PERM = "exprewards.op";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(Chat.PREFFIX + "&cERROR: You must be a player to use this command!");
            return true;
        }

        Player player = (Player) sender;
        PlayerReward playerReward = PlayerRewardManager.prw.getPlayerReward(player);

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("reset")) {
                playerReward.reset();
                player.sendMessage("Your status has been reset!");
            } else if(args[0].equalsIgnoreCase("reload") && player.hasPermission(ADMIN_PERM)) {
                player.sendMessage(Chat.toColor(Chat.PREFFIX + "Reloading configs..."));
                ExpRewards.getPlugin().reloadConfigs();
                player.sendMessage(Chat.toColor(Chat.PREFFIX + "&aConfigs reloaded!"));
            } else {
                RewardGUI rewardGUI = new RewardGUI(playerReward, 1);
                rewardGUI.openInventory();
            }
        } else {
            RewardGUI rewardGUI = new RewardGUI(playerReward, 1);
            rewardGUI.openInventory();
        }
        return true;
    }
}
