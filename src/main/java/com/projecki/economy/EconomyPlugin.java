package com.projecki.economy;

import com.projecki.economy.command.EconomyBalanceCommand;
import com.projecki.economy.command.ModifyBalanceCommand;
import com.projecki.economy.listener.JoinEventHandler;
import com.projecki.economy.player.EconomyPlayerManager;
import com.projecki.economy.store.DatabaseException;
import com.projecki.economy.store.IDatabaseConnector;
import com.projecki.economy.store.MySQLDatabaseConnector;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class EconomyPlugin extends JavaPlugin {

    private IDatabaseConnector _dbConnector;
    private EconomyPlayerManager _playerManager;

    @Override
    public void onEnable() {

        PluginManager pluginManager = Bukkit.getPluginManager();
        try {

            this.createConfigurationFile();
            this.createDatabaseConnector();

            _playerManager = new EconomyPlayerManager(_dbConnector,
                    this.getConfig().getDouble("defaultBalance", 0));

            // Register events
            pluginManager.registerEvents(new JoinEventHandler(_playerManager), this);

            // Register commands
            this.getCommand("balance")
                    .setExecutor(new EconomyBalanceCommand(_playerManager));
            this.getCommand("modifybalance")
                    .setExecutor(new ModifyBalanceCommand(_playerManager));

        } catch(PluginStartException ex) {

            System.err.println("Failed to load economy plugin: " + ex.getMessage());
            ex.printStackTrace();

            this.setEnabled(false);

        }

    }

    // We manually create our config file, so that the structure is transferred (comments etc)
    private void createConfigurationFile() throws PluginStartException {

        File dataFolder = this.getDataFolder();
        File configFile = new File(dataFolder, "config.yml");
        if(configFile.exists())
            return;

        try(InputStream stream = this.getResource("config.yml")) {

            if(!dataFolder.exists() && !dataFolder.mkdir())
                throw new PluginStartException("Failed to create data folder");
            if(!configFile.createNewFile())
                throw new PluginStartException("Failed to create config file");

            if(stream == null)
                throw new PluginStartException("Failed to load config file to memory");

            byte[] data = new byte[stream.available()];
            stream.read(data);

            try(FileOutputStream outStream = new FileOutputStream(configFile)) {
                outStream.write(data);
            }

            this.reloadConfig();

        } catch (IOException ex) {
            throw new PluginStartException("Failed to write config file", ex);
        }

    }

    private void createDatabaseConnector() throws PluginStartException {

        FileConfiguration config = this.getConfig();
        _dbConnector = new MySQLDatabaseConnector(
                config.getString("dbSocket"),
                config.getString("dbName"),
                config.getString("dbUsername"),
                config.getString("dbPassword")
        );

        try {
            _dbConnector.initialize();
        } catch(DatabaseException ex) {
            throw new PluginStartException("Failed to connect to database", ex);
        }

    }

}
