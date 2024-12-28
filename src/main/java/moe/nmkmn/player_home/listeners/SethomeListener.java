package moe.nmkmn.player_home.listeners;

import moe.nmkmn.player_home.PlayerHome;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.List;

public class SethomeListener implements Listener {
    private final PlayerHome plugin;

    public SethomeListener(PlayerHome plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().title().equals(Component.text("ホーム設定"))) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!player.getWorld().getName().equals("railway")) {
            player.sendMessage(plugin.prefix.append(Component.text("この機能は Railway のみ実行可能です。")));
            return;
        }

        event.setCancelled(true);
        int slot = event.getRawSlot();

        switch (slot) {
            case 2 -> handleSetHome(player, "1");
            case 4 -> handleSetHome(player, "2");
            case 6 -> handleSetHome(player, "3");
        }
    }

    private void handleSetHome(Player player, String homeNumber) {
        try {
            String coordinate = plugin.databaseManager.getCoordinate(player, homeNumber);

            if ("not_found".equals(coordinate)) {
                plugin.databaseManager.setCoordinate(player, homeNumber);
                player.sendMessage(plugin.prefix.append(Component.text("ホーム " + homeNumber + " を設定しました。").color(NamedTextColor.GREEN)));
            } else {
                showConfirmationInventory(player, homeNumber, coordinate);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to access database: " + e.getMessage());
            player.sendMessage(plugin.prefix.append(Component.text("内部サーバーエラーが発生しました、運営に報告してください。").color(NamedTextColor.RED)));
        }
    }

    private void showConfirmationInventory(Player player, String homeNumber, String currentCoordinate) {
        Inventory confirmationInventory = Bukkit.createInventory(null, 9, Component.text("ホーム地点の置き換え"));

        ItemStack yesItem = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta yesMeta = yesItem.getItemMeta();
        yesMeta.displayName(Component.text("はい").color(NamedTextColor.GREEN));
        yesMeta.lore(List.of(Component.text("現在の座標に置き換えます。").color(NamedTextColor.WHITE)));
        yesItem.setItemMeta(yesMeta);

        ItemStack noItem = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta noMeta = noItem.getItemMeta();
        noMeta.displayName(Component.text("いいえ").color(NamedTextColor.GREEN));
        noMeta.lore(List.of(Component.text("座標を置き換えません。").color(NamedTextColor.WHITE)));
        noItem.setItemMeta(noMeta);

        confirmationInventory.setItem(3, yesItem);
        confirmationInventory.setItem(5, noItem);

        player.openInventory(confirmationInventory);

        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onConfirmationClick(InventoryClickEvent confirmationEvent) {
                if (!confirmationEvent.getView().title().equals(Component.text("ホーム地点の置き換え"))) return;
                if (!(confirmationEvent.getWhoClicked() instanceof Player confirmationPlayer)) return;
                if (!confirmationPlayer.equals(player)) return;

                confirmationEvent.setCancelled(true);

                int confirmationSlot = confirmationEvent.getRawSlot();
                switch (confirmationSlot) {
                    case 3 -> {
                        try {
                            plugin.databaseManager.setCoordinate(player, homeNumber);
                            player.sendMessage(plugin.prefix.append(Component.text("ホーム " + homeNumber + " の座標を更新しました。").color(NamedTextColor.GREEN)));
                        } catch (SQLException e) {
                            plugin.getLogger().severe("Failed to update coordinate: " + e.getMessage());
                            player.sendMessage(plugin.prefix.append(Component.text("内部サーバーエラーが発生しました、運営に報告してください。").color(NamedTextColor.RED)));
                        }
                        confirmationPlayer.closeInventory();
                    }
                    case 5 -> {
                        confirmationPlayer.sendMessage(plugin.prefix.append(Component.text("ホーム " + homeNumber + " の変更をキャンセルしました。").color(NamedTextColor.YELLOW)));
                        confirmationPlayer.closeInventory();
                    }
                }

                InventoryClickEvent.getHandlerList().unregister(this);
            }
        }, plugin);
    }
}
