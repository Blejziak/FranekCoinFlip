package dev.franek.coinflip.gui;

import dev.franek.coinflip.data.CoinFlipGame;
import dev.franek.coinflip.manager.CoinFlipManager;
import dev.franek.coinflip.manager.ConfigManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class YourGamesGUI {

    private final Inventory inv;
    private final Player player;
    private final CoinFlipManager coinFlipManager;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM HH:mm");
    private final ConfigManager configManager;

    public YourGamesGUI(Player player, CoinFlipManager coinFlipManager, ConfigManager configManager) {
        this.configManager = configManager;
        this.player = player;
        this.coinFlipManager = coinFlipManager;
        this.inv = Bukkit.createInventory(null, 54, configManager.getGuiTitleYourGames());
        initializeItems();
    }

    private void initializeItems() {
        List<CoinFlipGame> playerGames = this.coinFlipManager.getPlayerHistoryGames(this.player.getUniqueId());
        int slot = 0;
        for (CoinFlipGame game : playerGames) {
            if (slot >= this.inv.getSize() - 9) {
                break;
            }
            ItemStack gameItem = createCoinFlipGameItem(game, this.player.getUniqueId());
            this.inv.setItem(slot, gameItem);
            ++slot;
        }
        ItemStack backButton = createGuiItem(Material.ARROW, this.configManager.getYourGamesBackButtonName(), this.configManager.getYourGamesBackButtonLore());
        this.inv.setItem(this.inv.getSize() - this.configManager.getYourGamesBackButtonOffset(), backButton);
    }

    private ItemStack createCoinFlipGameItem(CoinFlipGame game, UUID playerUUID) {
        String displayName;
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        Material displayMaterial;
        switch (game.getStatus()) {
            case PENDING -> {
                displayName = this.configManager.getYourGamesGameItemPendingName();
                displayMaterial = Material.CLOCK;
            }
            case IN_PROGRESS -> {
                displayName = this.configManager.getYourGamesGameItemInProgressName();
                displayMaterial = Material.COMPASS;
            }
            case COMPLETED_WON -> {
                displayName = this.configManager.getYourGamesGameItemWonName();
                displayMaterial = Material.GOLD_INGOT;
            }
            case COMPLETED_LOSE -> {
                displayName = this.configManager.getYourGamesGameItemLostName();
                displayMaterial = Material.IRON_INGOT;
            }
            case CANCELLED -> {
                displayName = this.configManager.getYourGamesGameItemCancelledName();
                displayMaterial = Material.REDSTONE_BLOCK;
            }
            default -> {
                displayName = ChatColor.WHITE + "Nieznany status";
                displayMaterial = Material.BARRIER;
            }
        }
        meta.setDisplayName(displayName);
        item.setType(displayMaterial);
        List<String> lore = new ArrayList<>();
        for (String line : this.configManager.getYourGamesGameItemLore()) {
            String processedLine = line;
            processedLine = processedLine.replace("%game_id_short%", game.getGameId().toString().substring(0, 8));
            processedLine = processedLine.replace("%bet_amount%", String.format("%.2f", game.getBetAmount()));
            processedLine = processedLine.replace("%date%", DATE_FORMAT.format(new Date(game.getCreationTimestamp())));
            processedLine = processedLine.replace("%commission_taken%", String.format("%.2f", game.getCommissionTaken()));
            String statusDisplayName = switch (game.getStatus()) {
                case PENDING -> "Oczekuje";
                case IN_PROGRESS -> "W trakcie";
                case COMPLETED_WON -> "Wygrana";
                case COMPLETED_LOSE -> "Przegrana";
                case CANCELLED -> "Anulowana";
            };
            processedLine = processedLine.replace("%status%", statusDisplayName);
            processedLine = game.getOpponentUUID() != null
                    ? processedLine.replace("%opponent_name%", game.getOpponentName())
                    : processedLine.replace("%opponent_name%", "Brak");
            if (game.getStatus() == CoinFlipGame.GameStatus.COMPLETED_WON || game.getStatus() == CoinFlipGame.GameStatus.COMPLETED_LOSE) {
                processedLine = processedLine.replace("%winner_name%", game.getWinnerUUID() != null ? Bukkit.getOfflinePlayer(game.getWinnerUUID()).getName() : "Nierozstrzygnieta");
            } else if (processedLine.contains("%winner_name%") || processedLine.contains("%winnings%") || processedLine.contains("%tax_amount%")) {
                continue;
            }
            lore.add(processedLine);
        }
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
