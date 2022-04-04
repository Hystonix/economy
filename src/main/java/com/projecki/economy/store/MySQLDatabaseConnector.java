package com.projecki.economy.store;

import com.projecki.economy.player.EconomyPlayer;

import java.sql.*;
import java.util.UUID;

/*
    Very simple database connector, uses a single connection and
    runs on the main server thread. However, it shouldn't be too difficult to, for example, modify
    the code to use a connection pool (such as hikari), or execute database queries on another thread (with futures, executorservice, etc)
 */
public class MySQLDatabaseConnector implements IDatabaseConnector {

    private String _jdbcUrl;
    private Connection _connection;

    public MySQLDatabaseConnector(String socket, String dbName, String username, String password) {
        _jdbcUrl = "jdbc:mysql://" + socket + "/" + dbName + "?user=" + username + "&password=" + password;
    }

    private Connection makeConnection() throws SQLException {

        if(_connection != null && !_connection.isClosed())
            return _connection;

        _connection = DriverManager.getConnection(_jdbcUrl);
        return _connection;

    }

    @Override
    public void initialize() throws DatabaseException {

        try {

            Connection con = this.makeConnection();

            con.prepareStatement("CREATE TABLE IF NOT EXISTS `econ_profile`(" +
                        "`uuid` VARCHAR(36) NOT NULL," +
                        "`balance` DOUBLE DEFAULT 0," +
                        "PRIMARY KEY (`uuid`)" +
                    ")").execute();

        } catch(SQLException ex) {
            throw new DatabaseException(ex);
        }

    }

    @Override
    public EconomyPlayer fetchProfile(UUID uuid) throws DatabaseException {

        try {

            Connection con = this.makeConnection();

            PreparedStatement statement =
                    con.prepareStatement("SELECT `uuid`,`balance` FROM `econ_profile` WHERE `uuid`=?");
            statement.setString(1, uuid.toString());

            ResultSet rs = statement.executeQuery();
            if(!rs.next())
                    return null;

            return new EconomyPlayer(
                UUID.fromString(rs.getString("uuid")),
                rs.getDouble("balance")
            );

        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }

    }

    @Override
    public boolean updateProfile(EconomyPlayer profile) throws DatabaseException {

        try {

            Connection con = this.makeConnection();

            PreparedStatement statement =
                    con.prepareStatement("INSERT INTO `econ_profile` (uuid, balance) VALUES (?, ?) ON DUPLICATE KEY UPDATE `balance`=?");

            statement.setString(1, profile.getUniqueId().toString());

            statement.setDouble(2, profile.getBalance());
            statement.setDouble(3, profile.getBalance());

            return statement.execute();

        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }

    }

}
