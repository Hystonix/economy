package com.projecki.economy.store;

import com.projecki.economy.player.EconomyPlayer;

import java.util.UUID;

public interface IDatabaseConnector {

    void initialize() throws DatabaseException;

    EconomyPlayer fetchProfile(UUID uuid) throws DatabaseException;
    boolean updateProfile(EconomyPlayer player) throws DatabaseException;

}
