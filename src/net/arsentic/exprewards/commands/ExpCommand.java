package net.arsentic.exprewards.commands;

import net.arsentic.exprewards.core.gui.RewardGUI;
import net.arsentic.exprewards.core.player.PlayerReward;
import net.arsentic.exprewards.core.player.PlayerRewardManager;
import net.arsentic.exprewards.utils.chat.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(Chat.BARE_PREFFIX + "ERROR: You must be a player to use this command!");
            return true;
        }

        Player player = (Player) sender;
        PlayerReward playerReward = PlayerRewardManager.prw.getPlayerReward(player);

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("reset")) {
                playerReward.reset();
                player.sendMessage("Your status has been reset!");
            }
        } else {
            RewardGUI rewardGUI = new RewardGUI(playerReward);
            rewardGUI.openInventory();
        }
        return true;
    }
}
