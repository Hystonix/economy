package com.projecki.economy.command;

import com.projecki.economy.player.EconomyPlayer;
import com.projecki.economy.player.EconomyPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;

public class ModifyBalanceCommand implements CommandExecutor {

    private EconomyPlayerManager _playerManager;

    public ModifyBalanceCommand(EconomyPlayerManager playerManager) {
        _playerManager = playerManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!sender.hasPermission("command.economy.modifybalance")) {

            sender.sendMessage(ChatColor.DARK_RED + "You do not have the permissions required to execute this command!");
            return true;

        }

        if(args.length < 3) {
            sender.sendMessage(ChatColor.DARK_RED + "Invalid arguments! Refer to: "
                    + ChatColor.RED + "/modifybalance <name> <add|remove|set> <amount>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if(target == null || !target.hasPlayedBefore()) {

            sender.sendMessage(ChatColor.DARK_RED + "Failed to find player with name " + ChatColor.RED + "'" + args[0] + "'");
            return true;

        }

        // Parse the amount we modify by
        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch(NumberFormatException ex) {
            sender.sendMessage(ChatColor.RED + "'" + args[2] + "'" + ChatColor.DARK_RED + " is not a valid number!");
            return true;
        }

        EconomyPlayer profile = _playerManager.fetchProfile(target.getUniqueId());

        // Parse the operation
        switch(args[1].toUpperCase()) {
            case "ADD":
                profile.modifyBalance(amount);
                break;
            case "REMOVE": case "TAKE":
                profile.modifyBalance(-1 * amount);
                break;
            case "SET":
                profile.setBalance(amount);
                break;
            default:
                sender.sendMessage(ChatColor.DARK_RED + "Invalid arguments! Refer to: "
                        + ChatColor.RED + "/modifybalance <name> <add|remove|set> <amount>");
                return true;

        }

        sender.sendMessage(ChatColor.GREEN + "Updated "
                + ChatColor.GOLD + target.getName() + "'s"
                + ChatColor.GREEN + " balance to "
                + profile.getFormattedBalance());
        return true;

    }

}
