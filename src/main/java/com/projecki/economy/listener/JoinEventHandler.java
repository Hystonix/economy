package com.projecki.economy.listener;

import com.projecki.economy.player.EconomyPlayer;
import com.projecki.economy.player.EconomyPlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinEventHandler implements Listener {

    private EconomyPlayerManager _playerManager;

    public JoinEventHandler(EconomyPlayerManager playerManager) {
        _playerManager = playerManager;
    }

    // Listen for player joins, so we can cache their data
    @EventHandler
    public void onJoin(PlayerJoinEvent ev) {
        _playerManager.fetchProfile(ev.getPlayer());
    }

    // Listen for player quits, so we can push any changes to the database
    @EventHandler
    public void onQuit(PlayerQuitEvent ev) {

        EconomyPlayer profile = _playerManager.fetchIfCached(ev.getPlayer().getUniqueId());
        if(profile == null || !profile.requiresUpdate())
            return;

        _playerManager.pushProfile(profile);

    }

}
