package moe.nmkmn.player_home.commands;

import moe.nmkmn.player_home.PlayerHome;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class SethomeCommand implements CommandExecutor {
    public final PlayerHome plugin;

    public SethomeCommand(PlayerHome plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("このコマンドはプレイヤーのみ実行可能です。").color(NamedTextColor.RED));
            return true;
        }

        Inventory inventory = Bukkit.createInventory(null, 9, Component.text("ホーム設定"));

        ItemStack homeOneStack = new ItemStack(Material.RED_CONCRETE);
        ItemMeta homeOneMeta = homeOneStack.getItemMeta();
        homeOneMeta.displayName(Component.text("ホーム 1 に設定").color(NamedTextColor.GREEN));
        homeOneStack.setItemMeta(homeOneMeta);

        ItemStack homeTwoStack = new ItemStack(Material.BLUE_CONCRETE);
        ItemMeta homeTwoMeta = homeTwoStack.getItemMeta();
        homeTwoMeta.displayName(Component.text("ホーム 2 に設定").color(NamedTextColor.GREEN));
        homeTwoStack.setItemMeta(homeTwoMeta);

        ItemStack homeThreeStack = new ItemStack(Material.GREEN_CONCRETE);
        ItemMeta homeThreesMeta = homeThreeStack.getItemMeta();
        homeThreesMeta.displayName(Component.text("ホーム 3 に設定").color(NamedTextColor.GREEN));
        homeThreeStack.setItemMeta(homeThreesMeta);

        inventory.setItem(2, homeOneStack);
        inventory.setItem(4, homeTwoStack);
        inventory.setItem(6, homeThreeStack);

        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1f, 0.8f);
        player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1f, 0.4f);

        player.openInventory(inventory);
        return true;
    }
}
