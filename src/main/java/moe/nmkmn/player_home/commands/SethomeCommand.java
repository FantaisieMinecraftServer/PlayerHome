package moe.nmkmn.player_home.commands;

import moe.nmkmn.player_home.PlayerHome;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SethomeCommand implements CommandExecutor {
    public final PlayerHome plugin;

    public SethomeCommand(PlayerHome plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("このコマンドはプレイヤーのみ実行できます。").color(NamedTextColor.RED));
            return true;
        }

        player.sendMessage(plugin.prefix.append(Component.text("まだ作ってないよ")));
        return true;
    }
}