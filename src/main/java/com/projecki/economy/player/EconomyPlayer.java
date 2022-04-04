package com.projecki.economy.player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.UUID;

public class EconomyPlayer {

    private static DecimalFormat FORMATTER_1DP = new DecimalFormat("#.#");

    private UUID _uuid;
    private double _balance;

    private boolean _requireUpdate = false;

    public EconomyPlayer(UUID uuid, double balance) {

        _uuid = uuid;
        _balance = balance;

    }

    /**
     * Modify a player's balance
     * @param delta The amount to modify it by. Negative values will deduct balance, positive will add
     */
    public void modifyBalance(double delta) {
        this.setBalance(this.getBalance() + delta);
    }

    /**
     * Set a player's balance
     * @param balance The balance to set it to
     */
    public void setBalance(double balance) {

        double diff = balance - _balance;

        _requireUpdate |= _balance != balance;
        _balance = balance;

        Player ply = this.getPlayer();
        if(ply != null) { // If the player is online, tell them their balance has been updated

            String diffFormatted = (diff > 0 ? ChatColor.GREEN : ChatColor.RED + "-")
                + FORMATTER_1DP.format(diff);

            ply.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    ChatColor.GREEN + "Your balance was updated to "
                    + this.getFormattedBalance()
                    + ChatColor.GRAY + " (" + diffFormatted + ChatColor.GRAY + ")"
            ));
            ply.playSound(ply.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1.4f);

        }

    }

    /**
     * Set whether the profile has been modified, so that it is out-of-sync with the database
     * @param requireUpdate Whether the profile is out-of-sync
     */
    public void setRequireUpdate(boolean requireUpdate) {
        _requireUpdate = requireUpdate;
    }

    /**
     * Get the balance of the player
     * @return The current balance
     */
    public double getBalance() {
        return _balance;
    }

    /**
     * Get the unique id of the minecraft account
     */
    public UUID getUniqueId() {
        return _uuid;
    }

    /**
     * Get whether the profile is out-of-sync with the database, and requires an update
     * @return Whether the profile requires an update
     */
    public boolean requiresUpdate() {
        return _requireUpdate;
    }

    /**
     * Get the player that owns this economy data, if they are online
     * @return The player if they are online, null otherwise
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(_uuid);
    }

    /**
     * Get a formatted representation of the current balance
     */
    public String getFormattedBalance() {
        double bal = this.getBalance();
        return (bal >= 0 ? ChatColor.GOLD : ChatColor.RED) + FORMATTER_1DP.format(bal) + " coins";
    }

}
