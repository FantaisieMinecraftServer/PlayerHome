package moe.nmkmn.player_home;

import moe.nmkmn.player_home.commands.SethomeCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class PlayerHome extends JavaPlugin {
    public MiniMessage miniMessage = MiniMessage.miniMessage();
    public Component prefix = miniMessage.deserialize("<gray>[</gray><gradient:#44DAEA:#3163EB>PlayerHome</gradient><gray>]</gray> ");

    @Override
    public void onEnable() {
        Objects.requireNonNull(getCommand("sethome")).setExecutor(new SethomeCommand(this));
    }
}
