package com.projecki.economy.player;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.projecki.economy.store.DatabaseException;
import com.projecki.economy.store.IDatabaseConnector;
import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class EconomyPlayerManager {

    private IDatabaseConnector _connector;
    private LoadingCache<UUID, EconomyPlayer> _cache;

    public EconomyPlayerManager(IDatabaseConnector connector, double defaultBalance) {
        _connector = connector;
        _cache = CacheBuilder.newBuilder()
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .build(new EconomyPlayerLoader(_connector, defaultBalance));
    }

    /**
     * Fetch a player's profile using the provided UUID
     * If a profile doesn't exist, create a new profile
     * @param uuid The uuid to lookup
     * @return A non-null economy player (provided the uuid is valid and non-null itself)
     */
    public EconomyPlayer fetchProfile(UUID uuid) {

        if(uuid == null)
            return null;
        try {
            return _cache.get(uuid);
        } catch(ExecutionException ex) {

            ex.printStackTrace();
            return null;

        }

    }

    /**
     * Fetch a player's profile using a Player object
     * @param ply The player to lookup
     * @return An economy player object
     * @see EconomyPlayerManager#fetchProfile(UUID)
     */
    public EconomyPlayer fetchProfile(Player ply) {

        if(ply == null)
            return null;
        return this.fetchProfile(ply.getUniqueId());

    }

    /**
     * Fetch a player's already cached economy data
     * @param uniqueId The uuid of the player
     * @return THe economy data if it is cached, null otherwise
     */
    public EconomyPlayer fetchIfCached(UUID uniqueId) {
        return _cache.getIfPresent(uniqueId);
    }

    /**
     * Push any changes made to a cached economy player
     * @param profile The profile to update
     * @return Whether the operation was successful
     */
    public boolean pushProfile(EconomyPlayer profile) {

        try {

            boolean success = _connector.updateProfile(profile);
            if(success)
                profile.setRequireUpdate(false);

            return success;

        } catch(DatabaseException ex) {
            return false;
        }

    }

}
