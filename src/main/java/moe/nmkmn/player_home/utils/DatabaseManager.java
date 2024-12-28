package moe.nmkmn.player_home.utils;

import org.bukkit.entity.Player;

import java.sql.*;

public class DatabaseManager {
    private Connection connection;

    public void connect(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS Players (" +
                    "uuid varchar(36) primary key, " +
                    "tp_target_1_x int, tp_target_1_y int, tp_target_1_z int," +
                    "tp_target_2_x int, tp_target_2_y int, tp_target_2_z int," +
                    "tp_target_3_x int, tp_target_3_y int, tp_target_3_z int)"
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

    public boolean existsPlayer(Player player) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM Players WHERE uuid = ?")) {
            statement.setString(1, player.getUniqueId().toString());
            ResultSet result = statement.executeQuery();
            return result.next();
        }
    }
}
