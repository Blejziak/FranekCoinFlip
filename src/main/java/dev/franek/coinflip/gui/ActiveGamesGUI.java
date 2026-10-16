package dev.franek.coinflip.gui;

import dev.franek.coinflip.data.CoinFlipGame;
import dev.franek.coinflip.manager.CoinFlipManager;
import dev.franek.coinflip.manager.ConfigManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ActiveGamesGUI {

    private final Inventory inv;
    private final Player player;
    private final CoinFlipManager coinFlipManager;
    private final ConfigManager configManager;

    public ActiveGamesGUI(Player player, CoinFlipManager coinFlipManager, ConfigManager configManager) {
        this.player = player;
        this.coinFlipManager = coinFlipManager;
        this.configManager = configManager;
        int numGames = coinFlipManager.getActiveGames().size();
        int rows = numGames / 9 + 1;
        if (rows > 6) {
            rows = 6;
        }
        int inventorySize = rows * 9;
        if (inventorySize < 54 && inventorySize <= 49) {
            inventorySize = 54;
        }
        this.inv = Bukkit.createInventory(null, inventorySize, configManager.getGuiTitleActiveGames());
        initializeItems();
    }

    private void initializeItems() {
        List<CoinFlipGame> activeGames = this.coinFlipManager.getActiveGames();
        int slot = 0;
        for (CoinFlipGame game : activeGames) {
            if (slot >= this.inv.getSize() - 9) {
                break;
            }
            ItemStack gameItem = createCoinFlipGameItem(game);
            this.inv.setItem(slot, gameItem);
            ++slot;
        }
        ItemStack backButton = createGuiItem(Material.ARROW, this.configManager.getActiveGamesBackButtonName(), this.configManager.getActiveGamesBackButtonLore());
        this.inv.setItem(this.inv.getSize() - this.configManager.getActiveGamesBackButtonOffset(), backButton);
    }

    private ItemStack createCoinFlipGameItem(CoinFlipGame game) {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        String itemName = this.configManager.getActiveGamesGameItemName().replace("%player_name%", game.getCreatorName());
        meta.setDisplayName(itemName);
        List<String> lore = new ArrayList<>();
        for (String line : this.configManager.getActiveGamesGameItemLore()) {
            lore.add(line.replace("%bet_amount%", String.format("%.2f", game.getBetAmount())));
        }
        lore.add(ChatColor.BLACK + "GameID_Full:" + game.getGameId().toString());
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
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
