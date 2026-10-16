package dev.franek.coinflip.handler;

import dev.franek.coinflip.FranekCoinFlip;
import dev.franek.coinflip.data.CoinFlipGame;
import dev.franek.coinflip.gui.ActiveGamesGUI;
import dev.franek.coinflip.gui.MainGUI;
import dev.franek.coinflip.gui.YourGamesGUI;
import dev.franek.coinflip.manager.CoinFlipManager;
import dev.franek.coinflip.manager.ConfigManager;
import dev.franek.coinflip.manager.EconomyManager;
import dev.franek.coinflip.manager.NotificationManager;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryClickHandler implements Listener {

    private final FranekCoinFlip plugin;
    private final CoinFlipManager coinFlipManager;
    private final NotificationManager notificationManager;
    private final ConfigManager configManager;
    private final Set<UUID> awaitingAmount = new HashSet<>();

    public InventoryClickHandler(FranekCoinFlip plugin, CoinFlipManager coinFlipManager, NotificationManager notificationManager, ConfigManager configManager) {
        this.plugin = plugin;
        this.coinFlipManager = coinFlipManager;
        this.notificationManager = notificationManager;
        this.configManager = configManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }
        String itemName = "";
        if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
            itemName = clickedItem.getItemMeta().getDisplayName();
        }
        if (title.equals(this.configManager.getGuiTitleMain())
                || title.equals(this.configManager.getGuiTitleActiveGames())
                || title.equals(this.configManager.getGuiTitleYourGames())
                || title.equals(this.configManager.getGuiTitleCoinFlipAnimation())
                || title.equals(this.configManager.getGuiTitleWinnerDisplay())) {
            event.setCancelled(true);
            if (title.equals(this.configManager.getGuiTitleMain())) {
                if (itemName.equals(this.configManager.getMainGuiActiveGamesName())) {
                    new ActiveGamesGUI(player, this.coinFlipManager, this.configManager).openInventory();
                } else if (itemName.equals(this.configManager.getMainGuiYourGamesName())) {
                    new YourGamesGUI(player, this.coinFlipManager, this.configManager).openInventory();
                } else if (itemName.equals(this.configManager.getMainGuiCreateGameName())) {
                    player.closeInventory();
                    this.awaitingAmount.add(player.getUniqueId());
                    player.sendMessage(ChatColor.YELLOW + ">>> " + ChatColor.WHITE + "Wpisz kwote zakladu na czacie (lub wpisz " + ChatColor.RED + "anuluj" + ChatColor.WHITE + "):");
                } else if (itemName.equals(this.configManager.getMainGuiCloseButtonName())) {
                    player.closeInventory();
                } else if (itemName.equals(this.configManager.getMainGuiNotificationsOnName()) || itemName.equals(this.configManager.getMainGuiNotificationsOffName())) {
                    this.notificationManager.toggleNotifications(player.getUniqueId());
                    String statusText = this.notificationManager.areNotificationsEnabled(player.getUniqueId())
                            ? ChatColor.translateAlternateColorCodes('&', "&aWLACZONE")
                            : ChatColor.translateAlternateColorCodes('&', "&cWYLACZONE");
                    String message = this.configManager.getMessageNotificationsToggled().replace("%status%", statusText);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    new MainGUI(player, this.notificationManager, this.configManager).openInventory();
                }
            } else if (title.equals(this.configManager.getGuiTitleActiveGames()) || title.equals(this.configManager.getGuiTitleYourGames())) {
                if (itemName.equals(this.configManager.getActiveGamesBackButtonName()) || itemName.equals(this.configManager.getYourGamesBackButtonName())) {
                    new MainGUI(player, this.notificationManager, this.configManager).openInventory();
                } else if (title.equals(this.configManager.getGuiTitleActiveGames())) {
                    handleGameJoin(player, clickedItem);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!this.awaitingAmount.contains(player.getUniqueId())) {
            return;
        }
        event.setCancelled(true);
        String message = event.getMessage();
        if (message.equalsIgnoreCase("anuluj")) {
            this.awaitingAmount.remove(player.getUniqueId());
            player.sendMessage(ChatColor.RED + "Tworzenie gry zostalo anulowane.");
            return;
        }
        double betAmount;
        try {
            betAmount = Double.parseDouble(message);
        } catch (NumberFormatException e) {
            player.sendMessage(this.configManager.getMessageInvalidAmount());
            return;
        }
        Bukkit.getScheduler().runTask(this.plugin, () -> {
            double playerBalance = EconomyManager.getMoney(player);
            if (betAmount <= 0.0) {
                player.sendMessage(this.configManager.getMessageAmountTooLow());
                return;
            }
            if (betAmount > playerBalance * this.configManager.getMaxBetPercentage()) {
                String msg = this.configManager.getMessageTooMuchMoney().replace("%player_balance%", String.format("%.2f", playerBalance));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                return;
            }
            if (playerBalance < betAmount) {
                String msg = this.configManager.getMessageNotEnoughMoney().replace("%needed_amount%", String.format("%.2f", betAmount));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                return;
            }
            this.awaitingAmount.remove(player.getUniqueId());
            CoinFlipGame game = new CoinFlipGame(player.getUniqueId(), player.getName(), betAmount);
            this.coinFlipManager.addGame(game);
            EconomyManager.removeMoney(player, betAmount);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.configManager.getMessageGameCreated().replace("%bet_amount%", String.format("%.2f", betAmount))));
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!this.notificationManager.areNotificationsEnabled(onlinePlayer.getUniqueId()) || onlinePlayer.getUniqueId().equals(player.getUniqueId())) {
                    continue;
                }
                String notificationMsg = this.configManager.getNotificationGameCreated()
                        .replace("%player_name%", player.getName())
                        .replace("%bet_amount%", String.format("%.2f", betAmount));
                onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', notificationMsg));
            }
            new MainGUI(player, this.notificationManager, this.configManager).openInventory();
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.awaitingAmount.remove(event.getPlayer().getUniqueId());
    }

    private void handleGameJoin(Player player, ItemStack clickedItem) {
        if (clickedItem.getType() == Material.PAPER && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasLore()) {
            List<String> lore = clickedItem.getItemMeta().getLore();
            UUID gameId = null;
            for (String line : lore) {
                if (!line.startsWith(ChatColor.BLACK + "GameID_Full:")) {
                    continue;
                }
                try {
                    gameId = UUID.fromString(ChatColor.stripColor(line).substring("GameID_Full:".length()));
                    break;
                } catch (IllegalArgumentException e) {
                    this.plugin.getLogger().warning("Blad UUID w lore gry: " + line);
                }
            }
            if (gameId != null) {
                Optional<CoinFlipGame> optionalGame = this.coinFlipManager.getGameById(gameId);
                if (optionalGame.isPresent()) {
                    CoinFlipGame game = optionalGame.get();
                    if (game.getStatus() != CoinFlipGame.GameStatus.PENDING) {
                        player.sendMessage(this.configManager.getMessageGameAlreadyInProgress());
                        new ActiveGamesGUI(player, this.coinFlipManager, this.configManager).openInventory();
                        return;
                    }
                    if (game.getCreatorUUID().equals(player.getUniqueId())) {
                        player.sendMessage(this.configManager.getMessageCannotJoinOwnGame());
                        return;
                    }
                    double betAmount = game.getBetAmount();
                    double playerBalance = EconomyManager.getMoney(player);
                    if (playerBalance < betAmount) {
                        String msg = this.configManager.getMessageNotEnoughMoneyToJoin().replace("%needed_amount%", String.format("%.2f", betAmount));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                        return;
                    }
                    game.setOpponent(player.getUniqueId(), player.getName());
                    EconomyManager.removeMoney(player, betAmount);
                    this.coinFlipManager.saveGames();
                    player.sendMessage(this.configManager.getMessageGameJoined());
                    Player creator = Bukkit.getPlayer(game.getCreatorUUID());
                    if (creator != null && creator.isOnline()) {
                        String messageCreator = this.configManager.getMessageOpponentJoinedCreator().replace("%opponent_name%", player.getName());
                        creator.sendMessage(ChatColor.translateAlternateColorCodes('&', messageCreator));
                    }
                    animateAndResolveGame(player, Bukkit.getPlayer(game.getCreatorUUID()), game);
                } else {
                    player.sendMessage(this.configManager.getMessageGameNotFound());
                    new ActiveGamesGUI(player, this.coinFlipManager, this.configManager).openInventory();
                }
            }
        }
    }

    private void animateAndResolveGame(final Player player1, final Player player2, final CoinFlipGame game) {
        player1.closeInventory();
        if (player2 != null && player2.isOnline()) {
            player2.closeInventory();
        }
        final Inventory animationInv = Bukkit.createInventory(null, 27, this.configManager.getGuiTitleCoinFlipAnimation());
        final ItemStack grayGlass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta grayMeta = grayGlass.getItemMeta();
        grayMeta.setDisplayName(" ");
        grayGlass.setItemMeta(grayMeta);
        for (int i = 0; i < animationInv.getSize(); ++i) {
            animationInv.setItem(i, grayGlass);
        }
        final ItemStack greenGlass = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta greenMeta = greenGlass.getItemMeta();
        greenMeta.setDisplayName(" ");
        greenGlass.setItemMeta(greenMeta);
        final ItemStack player1Head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta p1SkullMeta = (SkullMeta) player1Head.getItemMeta();
        p1SkullMeta.setOwningPlayer(player1);
        p1SkullMeta.setDisplayName(ChatColor.GREEN + player1.getName());
        player1Head.setItemMeta(p1SkullMeta);
        final ItemStack player2Head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta p2SkullMeta = (SkullMeta) player2Head.getItemMeta();
        if (player2 != null) {
            p2SkullMeta.setOwningPlayer(player2);
            p2SkullMeta.setDisplayName(ChatColor.GREEN + player2.getName());
        } else {
            p2SkullMeta.setDisplayName(ChatColor.RED + "Gracz Offline");
        }
        player2Head.setItemMeta(p2SkullMeta);
        player1.openInventory(animationInv);
        if (player2 != null && player2.isOnline()) {
            player2.openInventory(animationInv);
        }
        final long durationTicks = this.configManager.getAnimationDurationTicks();
        final long animationInterval = this.configManager.getAnimationIntervalTicks();
        new BukkitRunnable() {
            int currentTick = 0;
            boolean isGreen = false;

            @Override
            public void run() {
                if (!player1.isOnline() || (player2 != null && !player2.isOnline())) {
                    cancel();
                    if (player1.isOnline()) {
                        EconomyManager.addMoney(player1, game.getBetAmount());
                        player1.closeInventory();
                        player1.sendMessage(ChatColor.RED + "Przeciwnik wyszedl, zwrocono stawke.");
                    } else if (player2 != null && player2.isOnline()) {
                        EconomyManager.addMoney(player2, game.getBetAmount());
                        player2.closeInventory();
                        player2.sendMessage(ChatColor.RED + "Przeciwnik wyszedl, zwrocono stawke.");
                    }
                    game.setResult(null, 0.0, 0.0, CoinFlipGame.GameStatus.CANCELLED);
                    InventoryClickHandler.this.coinFlipManager.saveGames();
                    return;
                }
                if ((long) this.currentTick * animationInterval >= durationTicks) {
                    cancel();
                    displayWinnerGUI(player1, player2, game);
                    return;
                }
                for (int i = 0; i < animationInv.getSize(); ++i) {
                    animationInv.setItem(i, this.isGreen ? greenGlass : grayGlass);
                }
                animationInv.setItem(13, this.currentTick % 2 == 0 ? player1Head : player2Head);
                player1.updateInventory();
                if (player2.isOnline()) {
                    player2.updateInventory();
                }
                this.isGreen = !this.isGreen;
                ++this.currentTick;
            }
        }.runTaskTimer(this.plugin, 0L, animationInterval);
    }

    private void displayWinnerGUI(final Player player1, final Player player2, CoinFlipGame game) {
        Player winner = Math.random() < 0.5 ? player1 : player2;
        Player loser = winner.equals(player1) ? player2 : player1;
        double totalBet = game.getBetAmount() * 2.0;
        double taxAmount = totalBet * this.configManager.getTaxPercentage();
        double winnerGets = totalBet - taxAmount;
        EconomyManager.addMoney(winner, winnerGets);
        game.setResult(winner.getUniqueId(), winnerGets, taxAmount, CoinFlipGame.GameStatus.COMPLETED_WON);
        this.coinFlipManager.saveGames();
        Inventory winnerInv = Bukkit.createInventory(null, 27, this.configManager.getGuiTitleWinnerDisplay());
        ItemStack greenGlass = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta greenMeta = greenGlass.getItemMeta();
        greenMeta.setDisplayName(" ");
        greenGlass.setItemMeta(greenMeta);
        for (int i = 0; i < winnerInv.getSize(); ++i) {
            winnerInv.setItem(i, greenGlass);
        }
        ItemStack winnerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta winnerSkullMeta = (SkullMeta) winnerHead.getItemMeta();
        winnerSkullMeta.setOwningPlayer(winner);
        winnerSkullMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + winner.getName());
        winnerHead.setItemMeta(winnerSkullMeta);
        winnerInv.setItem(13, winnerHead);
        player1.openInventory(winnerInv);
        player2.openInventory(winnerInv);
        player1.sendMessage(this.configManager.getMessageCoinFlipEnded());
        player2.sendMessage(this.configManager.getMessageCoinFlipEnded());
        winner.sendMessage(ChatColor.translateAlternateColorCodes('&', this.configManager.getMessageYouWon().replace("%winnings_amount%", String.format("%.2f$", winnerGets))));
        loser.sendMessage(ChatColor.translateAlternateColorCodes('&', this.configManager.getMessageYouLost()));
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!this.notificationManager.areNotificationsEnabled(onlinePlayer.getUniqueId())
                    || onlinePlayer.getUniqueId().equals(player1.getUniqueId())
                    || onlinePlayer.getUniqueId().equals(player2.getUniqueId())) {
                continue;
            }
            String notificationMsg = this.configManager.getNotificationGameWon()
                    .replace("%winner_name%", winner.getName())
                    .replace("%winnings_amount%", String.format("%.2f$", winnerGets));
            onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', notificationMsg));
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player1.isOnline()) {
                    player1.closeInventory();
                    new MainGUI(player1, InventoryClickHandler.this.notificationManager, InventoryClickHandler.this.configManager).openInventory();
                }
                if (player2.isOnline()) {
                    player2.closeInventory();
                    new MainGUI(player2, InventoryClickHandler.this.notificationManager, InventoryClickHandler.this.configManager).openInventory();
                }
            }
        }.runTaskLater(this.plugin, this.configManager.getWinnerDisplayDurationTicks());
    }
}
