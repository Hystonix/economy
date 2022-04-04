package com.projecki.economy.player;

import com.google.common.cache.CacheLoader;
import com.projecki.economy.store.IDatabaseConnector;

import javax.annotation.Nonnull;
import java.util.UUID;

public class EconomyPlayerLoader extends CacheLoader<UUID, EconomyPlayer> {

    private IDatabaseConnector _dbConnector;
    private double _defaultBalance;

    public EconomyPlayerLoader(IDatabaseConnector dbConnector, double defaultBalance) {
        _dbConnector = dbConnector;
        _defaultBalance = defaultBalance;
    }

    @Override @Nonnull
    public EconomyPlayer load(@Nonnull UUID uuid) throws Exception {

        EconomyPlayer fetched = _dbConnector.fetchProfile(uuid);
        return fetched != null ? fetched : new EconomyPlayer(uuid, _defaultBalance);

    }

}
