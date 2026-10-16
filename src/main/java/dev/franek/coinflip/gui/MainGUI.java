package dev.franek.coinflip.gui;

import dev.franek.coinflip.manager.ConfigManager;
import dev.franek.coinflip.manager.NotificationManager;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MainGUI {

    private final Inventory inv;
    private final Player player;
    private final NotificationManager notificationManager;
    private final ConfigManager configManager;

    public MainGUI(Player player, NotificationManager notificationManager, ConfigManager configManager) {
        this.player = player;
        this.notificationManager = notificationManager;
        this.configManager = configManager;
        this.inv = Bukkit.createInventory(null, 36, configManager.getGuiTitleMain());
        initializeItems();
    }

    private void initializeItems() {
        ItemStack activeGames = createGuiItem(Material.CHEST, this.configManager.getMainGuiActiveGamesName(), this.configManager.getMainGuiActiveGamesLore());
        this.inv.setItem(this.configManager.getMainGuiActiveGamesSlot(), activeGames);
        ItemStack yourGames = createGuiItem(Material.HOPPER, this.configManager.getMainGuiYourGamesName(), this.configManager.getMainGuiYourGamesLore());
        this.inv.setItem(this.configManager.getMainGuiYourGamesSlot(), yourGames);
        ItemStack createGame = createGuiItem(Material.LIME_WOOL, this.configManager.getMainGuiCreateGameName(), this.configManager.getMainGuiCreateGameLore());
        this.inv.setItem(this.configManager.getMainGuiCreateGameSlot(), createGame);
        boolean notificationsEnabled = this.notificationManager.areNotificationsEnabled(this.player.getUniqueId());
        ItemStack notificationsToggle = notificationsEnabled
                ? createGuiItem(Material.LIME_WOOL, this.configManager.getMainGuiNotificationsOnName(), this.configManager.getMainGuiNotificationsOnLore())
                : createGuiItem(Material.RED_WOOL, this.configManager.getMainGuiNotificationsOffName(), this.configManager.getMainGuiNotificationsOffLore());
        ItemStack closeButton = createGuiItem(Material.BARRIER, this.configManager.getMainGuiCloseButtonName(), this.configManager.getMainGuiCloseButtonLore());
        this.inv.setItem(this.configManager.getMainGuiCloseButtonSlot(), closeButton);
        this.inv.setItem(this.configManager.getMainGuiNotificationsSlot(), notificationsToggle);
    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    public void openInventory() {
        this.player.openInventory(this.inv);
    }
}
