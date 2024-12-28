package moe.nmkmn.player_home.utils;

import org.bukkit.entity.Player;

import java.sql.*;

public class DatabaseManager {
    private final Connection connection;

    public DatabaseManager(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS Players (" +
                    "uuid varchar(36) primary key, " +
                    "tp_target_1_x double, tp_target_1_y double, tp_target_1_z double," +
                    "tp_target_2_x double, tp_target_2_y double, tp_target_2_z double," +
                    "tp_target_3_x double, tp_target_3_y double, tp_target_3_z double)"
            );
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void addPlayer(Player player) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO Players (uuid) VALUES (?)")) {
            statement.setString(1, player.getUniqueId().toString());
            statement.executeUpdate();
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean existsPlayer(Player player) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT uuid FROM Players WHERE uuid = ?")) {
            statement.setString(1, player.getUniqueId().toString());
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    public String getCoordinate(Player player, String num) throws SQLException {
        validateCoordinateNumber(num);

        if (!existsPlayer(player)) {
            addPlayer(player);
        }

        String query = switch (num) {
            case "1" -> "SELECT tp_target_1_x, tp_target_1_y, tp_target_1_z FROM Players WHERE uuid = ?";
            case "2" -> "SELECT tp_target_2_x, tp_target_2_y, tp_target_2_z FROM Players WHERE uuid = ?";
            case "3" -> "SELECT tp_target_3_x, tp_target_3_y, tp_target_3_z FROM Players WHERE uuid = ?";
            default -> throw new SQLException("Invalid coordinate number: " + num);
        };

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, player.getUniqueId().toString());
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    if (result.wasNull()) return "not_found";

                    double x = result.getDouble(1);
                    double y = result.getDouble(2);
                    double z = result.getDouble(3);

                    return x + " " + y + " " + z;
                } else {
                    return "not_found";
                }
            }
        }
    }


    public void setCoordinate(Player player, String num) throws SQLException {
        validateCoordinateNumber(num);

        if (!existsPlayer(player)) {
            addPlayer(player);
        }

        String query = switch (num) {
            case "1" -> "UPDATE Players SET tp_target_1_x = ?, tp_target_1_y = ?, tp_target_1_z = ? WHERE uuid = ?";
            case "2" -> "UPDATE Players SET tp_target_2_x = ?, tp_target_2_y = ?, tp_target_2_z = ? WHERE uuid = ?";
            case "3" -> "UPDATE Players SET tp_target_3_x = ?, tp_target_3_y = ?, tp_target_3_z = ? WHERE uuid = ?";
            default -> throw new SQLException("Invalid coordinate number: " + num);
        };

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDouble(1, player.getLocation().getX());
            statement.setDouble(2, player.getLocation().getY());
            statement.setDouble(3, player.getLocation().getZ());
            statement.setString(4, player.getUniqueId().toString());
            statement.executeUpdate();
        }
    }


    private void validateCoordinateNumber(String num) {
        if (Integer.parseInt(num) < 1 || Integer.parseInt(num) > 3) {
            throw new IllegalArgumentException("Invalid coordinate number: " + num);
        }
    }
}
