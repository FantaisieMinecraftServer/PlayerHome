package moe.nmkmn.player_home;

import moe.nmkmn.player_home.commands.HomeCommand;
import moe.nmkmn.player_home.commands.SethomeCommand;
import moe.nmkmn.player_home.listeners.SethomeListener;
import moe.nmkmn.player_home.utils.DatabaseManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;

public final class PlayerHome extends JavaPlugin {
    public MiniMessage miniMessage = MiniMessage.miniMessage();
    public Component prefix = miniMessage.deserialize("<gray>[</gray><gradient:#44DAEA:#3163EB>PlayerHome</gradient><gray>]</gray> ");

    public DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            if (!getDataFolder().mkdirs()) {
                System.out.println("Failed to create directory: " + getDataFolder());
            }
        };

        // データベースに接続
        try {
            databaseManager = new DatabaseManager(getDataFolder().getAbsolutePath() + "/database.db");
        } catch (SQLException e) {
            getLogger().severe("Failed to connect to database: " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Objects.requireNonNull(getCommand("home")).setExecutor(new HomeCommand(this));
        Objects.requireNonNull(getCommand("sethome")).setExecutor(new SethomeCommand(this));
        Bukkit.getPluginManager().registerEvents(new SethomeListener(this), this);
    }

    @Override
    public void onDisable() {
        try {
            databaseManager.close();
        } catch (SQLException e) {
            getLogger().severe("Failed to close to database: " + e.getMessage());
        }
    }
}
