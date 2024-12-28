package moe.nmkmn.player_home.commands;

import moe.nmkmn.player_home.PlayerHome;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class HomeCommand implements CommandExecutor {
    private final PlayerHome plugin;

    public HomeCommand(PlayerHome plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("このコマンドはプレイヤーのみ実行可能です。").color(NamedTextColor.RED));
            return true;
        }

        String homeNumber = "1";
        if (args.length > 0) {
            homeNumber = args[0];
        }

        try {
            String coordinate = plugin.databaseManager.getCoordinate(player, homeNumber);

            if ("not_found".equals(coordinate)) {
                player.sendMessage(plugin.prefix.append(Component.text("ホーム " + homeNumber + " は設定されていません。").color(NamedTextColor.RED)));
                return true;
            }

            Location location = parseCoordinate(coordinate);
            World railwayWorld = Bukkit.getWorld("railway");
            if (railwayWorld == null) {
                plugin.getLogger().severe("Failed get world railway");
                player.sendMessage(plugin.prefix.append(Component.text("内部サーバーエラーが発生しました、運営に報告してください。").color(NamedTextColor.RED)));
                return true;
            }

            location.setWorld(railwayWorld);
            player.teleport(location);
            player.sendMessage(plugin.prefix.append(Component.text("ホーム " + homeNumber + " にテレポートしました。").color(NamedTextColor.GREEN)));

        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to access database: " + e.getMessage());
            player.sendMessage(plugin.prefix.append(Component.text("内部サーバーエラーが発生しました、運営に報告してください。").color(NamedTextColor.RED)));
        } catch (IllegalArgumentException e) {
            plugin.getLogger().severe("Incorrect coordinate data:" + e.getMessage());
            player.sendMessage(plugin.prefix.append(Component.text("内部サーバーエラーが発生しました、運営に報告してください。").color(NamedTextColor.RED)));
        }

        return true;
    }

    private Location parseCoordinate(String coordinate) {
        String[] parts = coordinate.split(" ");
        if (parts.length != 3) throw new IllegalArgumentException("Invalid coordinate format.");
        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double z = Double.parseDouble(parts[2]);
        return new Location(null, x, y, z);
    }
}