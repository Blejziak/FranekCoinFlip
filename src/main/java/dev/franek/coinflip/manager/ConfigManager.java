package dev.franek.coinflip.manager;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        reloadConfig();
    }

    public void reloadConfig() {
        this.plugin.reloadConfig();
        this.config = this.plugin.getConfig();
    }

    public String getGuiTitleMain() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("gui-titles.main", "CoinFlip"));
    }

    public String getGuiTitleActiveGames() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("gui-titles.active-games", "Aktywne Gry CoinFlip"));
    }

    public String getGuiTitleYourGames() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("gui-titles.your-games", "Twoje Gry CoinFlip"));
    }

    public String getGuiTitleCoinFlipAnimation() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("gui-titles.coin-flip-animation", "&bRzut Moneta!"));
    }

    public String getGuiTitleWinnerDisplay() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("gui-titles.winner-display", "&aZwyciezca!"));
    }

    public String getMainGuiActiveGamesName() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("main-gui.items.active-games.name", "&aAktywne Gry"));
    }

    public String[] getMainGuiActiveGamesLore() {
        return this.config.getStringList("main-gui.items.active-games.lore").stream()
                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                .toArray(String[]::new);
    }

    public int getMainGuiActiveGamesSlot() {
        return this.config.getInt("main-gui.items.active-games.slot", 10);
    }

    public String getMainGuiYourGamesName() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("main-gui.items.your-games.name", "&eTwoje Gry"));
    }

    public String[] getMainGuiYourGamesLore() {
        return this.config.getStringList("main-gui.items.your-games.lore").stream()
                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                .toArray(String[]::new);
    }

    public int getMainGuiYourGamesSlot() {
        return this.config.getInt("main-gui.items.your-games.slot", 13);
    }

    public String getMainGuiCreateGameName() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("main-gui.items.create-game.name", "&bStworz Gre"));
    }

    public String[] getMainGuiCreateGameLore() {
        return this.config.getStringList("main-gui.items.create-game.lore").stream()
                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                .toArray(String[]::new);
    }

    public int getMainGuiCreateGameSlot() {
        return this.config.getInt("main-gui.items.create-game.slot", 16);
    }

    public String getMainGuiNotificationsOnName() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("main-gui.items.notifications.on-name", "&aPowiadomienia: WLACZONE"));
    }

    public String[] getMainGuiNotificationsOnLore() {
        return this.config.getStringList("main-gui.items.notifications.on-lore").stream()
                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                .toArray(String[]::new);
    }

    public String getMainGuiNotificationsOffName() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("main-gui.items.notifications.off-name", "&cPowiadomienia: WYLACZONE"));
    }

    public String[] getMainGuiNotificationsOffLore() {
        return this.config.getStringList("main-gui.items.notifications.off-lore").stream()
                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                .toArray(String[]::new);
    }

    public int getMainGuiNotificationsSlot() {
        return this.config.getInt("main-gui.items.notifications.slot", 22);
    }

    public String getActiveGamesGameItemName() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("active-games-gui.items.game-item.name", "&fGra gracza: &a%player_name%"));
    }

    public String[] getActiveGamesGameItemLore() {
        return this.config.getStringList("active-games-gui.items.game-item.lore").stream()
                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                .toArray(String[]::new);
    }

    public String getActiveGamesBackButtonName() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("active-games-gui.items.back-button.name", "&7Powrot"));
    }

    public String[] getActiveGamesBackButtonLore() {
        return this.config.getStringList("active-games-gui.items.back-button.lore").stream()
                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                .toArray(String[]::new);
    }

    public int getActiveGamesBackButtonOffset() {
        return this.config.getInt("active-games-gui.items.back-button.offset", 5);
    }

    public String getYourGamesGameItemPendingName() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("your-games-gui.items.game-item.pending-name", "&fTwoja gra: &eoczekuje"));
    }

    public String getYourGamesGameItemInProgressName() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("your-games-gui.items.game-item.in-progress-name", "&fTwoja gra: &6w trakcie"));
    }

    public String getYourGamesGameItemWonName() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("your-games-gui.items.game-item.won-name", "&fTwoja gra: &aWYGRANA"));
    }

    public String getYourGamesGameItemLostName() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("your-games-gui.items.game-item.lost-name", "&fTwoja gra: &cPRZEGRANA"));
    }

    public String getYourGamesGameItemCancelledName() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("your-games-gui.items.game-item.cancelled-name", "&fTwoja gra: &7ANULOWANA"));
    }

    public String[] getYourGamesGameItemLore() {
        return this.config.getStringList("your-games-gui.items.game-item.lore").stream()
                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                .toArray(String[]::new);
    }

    public String getYourGamesBackButtonName() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("your-games-gui.items.back-button.name", "&7Powrot"));
    }

    public String[] getYourGamesBackButtonLore() {
        return this.config.getStringList("your-games-gui.items.back-button.lore").stream()
                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                .toArray(String[]::new);
    }

    public int getYourGamesBackButtonOffset() {
        return this.config.getInt("your-games-gui.items.back-button.offset", 5);
    }

    public String getMessageInvalidAmount() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.invalid-amount", "&cNieprawidlowa kwota! Wpisz liczbe."));
    }

    public String getMessageAmountTooLow() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.amount-too-low", "&cKwota zakladu musi byc wieksza od zera."));
    }

    public String getMessageTooMuchMoney() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.too-much-money", "&cNie mozesz postawic wiecej niz 50% swojego majatku! (Posiadasz: &f%player_balance%&c)"));
    }

    public String getMessageNotEnoughMoney() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.not-enough-money", "&cNie masz wystarczajaco pieniedzy! Potrzebujesz &f%needed_amount%&c."));
    }

    public String getMessageGameCreated() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.game-created", "&aStworzono gre CoinFlip za &f%bet_amount%&a! Gra jest dostepna w 'Aktywnych Grach'."));
    }

    public String getMessageGameAlreadyInProgress() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.game-already-in-progress", "&cTa gra jest juz w trakcie lub zakonczona!"));
    }

    public String getMessageCannotJoinOwnGame() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.cannot-join-own-game", "&cNie mozesz dolaczyc do wlasnej gry!"));
    }

    public String getMessageNotEnoughMoneyToJoin() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.not-enough-money-to-join", "&cNie masz wystarczajaco pieniedzy, aby dolaczyc! Potrzebujesz &f%needed_amount%&c."));
    }

    public String getMessageGameJoined() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.game-joined", "&aDolaczyles do gry CoinFlip!"));
    }

    public String getMessageOpponentJoinedCreator() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.opponent-joined-creator", "&a%opponent_name% dolaczyl do Twojej gry CoinFlip!"));
    }

    public String getMessageGameNotFound() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.game-not-found", "&cTa gra juz nie istnieje lub zostala anulowana."));
    }

    public String getMessageGameIdentificationError() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.game-identification-error", "&cWystapil blad podczas identyfikacji gry. Skontaktuj sie z administracja."));
    }

    public String getMessageOpponentDisconnectedReturnStakeCreator() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.opponent-disconnected-return-stake-creator", "&cTwoj przeciwnik (&f%opponent_name%&c) wylogowal sie! Twoja stawka zostala zwrocona."));
    }

    public String getMessageOpponentDisconnectedReturnStakeJoiner() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.opponent-disconnected-return-stake-joiner", "&cTwoj przeciwnik (&f%creator_name%&c) wylogowal sie! Twoja stawka zostala zwrocona."));
    }

    public String getMessageCoinFlipEnded() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.coin-flip-ended", "&aRzut moneta zakonczony!"));
    }

    public String getMessageYouWon() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.you-won", "&6Gratulacje! Wygrales &f%winnings_amount%&6!"));
    }

    public String getMessageYouLost() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.you-lost", "&cNiestety, przegrales. Moze nastepnym razem!"));
    }

    public String getNotificationGameCreated() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("notifications.game-created", "&e[CoinFlip] &f%player_name%&c utworzyl gre o &f%bet_amount%&c! Wpisz &a/coinflip&c aby dolaczyc!"));
    }

    public String getNotificationGameWon() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("notifications.game-won", "&e[CoinFlip] &f%winner_name%&c wygral gre o &f%winnings_amount%&c!"));
    }

    public String getMessageNotificationsToggled() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.notifications-toggled", "&aPowiadomienia CoinFlip zostaly &f%status%&a."));
    }

    public long getAnimationDurationTicks() {
        return this.config.getLong("timings.animation-duration-seconds", 10L) * 20L;
    }

    public long getAnimationIntervalTicks() {
        return this.config.getLong("timings.animation-interval-ticks", 5L);
    }

    public long getWinnerDisplayDurationTicks() {
        return this.config.getLong("timings.winner-display-duration-seconds", 3L) * 20L;
    }

    public double getTaxPercentage() {
        return this.config.getDouble("game-settings.tax-percentage", 0.03);
    }

    public double getMaxBetPercentage() {
        return this.config.getDouble("game-settings.max-bet-percentage", 0.5);
    }

    public String getMainGuiCloseButtonName() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("main-gui.items.close-button.name", "&cZamknij!"));
    }

    public String[] getMainGuiCloseButtonLore() {
        return this.config.getStringList("main-gui.items.close-button.lore").stream()
                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                .toArray(String[]::new);
    }

    public int getMainGuiCloseButtonSlot() {
        return this.config.getInt("main-gui.items.close-button.slot", 31);
    }
}
