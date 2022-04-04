package com.projecki.economy.command;

import com.projecki.economy.player.EconomyPlayer;
import com.projecki.economy.player.EconomyPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class EconomyBalanceCommand implements CommandExecutor {

    private EconomyPlayerManager _playerManager;

    public EconomyBalanceCommand(EconomyPlayerManager playerManager) {
        _playerManager = playerManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player target;
        if(args.length > 0) { // /balance <name>

            target = Bukkit.getPlayer(args[0]);
            if(target == null) {

                sender.sendMessage(ChatColor.DARK_RED + "Failed to find player with name " + ChatColor.RED + "'" + args[0] + "'");
                return true;

            }

        } else if(sender instanceof Player) { // executed with no args, as a player
            target = (Player) sender;
        } else { // executed on console

            sender.sendMessage(ChatColor.DARK_RED + "Invalid usage! Refer to: " + ChatColor.RED + "/balance <name>");
            return true;

        }

        EconomyPlayer player = _playerManager.fetchProfile(target);
        boolean ranOnSelf = target.equals(sender);
        sender.sendMessage(
                ChatColor.GOLD + (ranOnSelf ? "You " : target.getName()) +
                ChatColor.GREEN + (ranOnSelf ? "have" : "has") +
                " a balance of " + player.getFormattedBalance()
        );
        return true;

    }

}
