package dev.franek.coinflip;

import dev.franek.coinflip.commands.CoinFlipCommand;
import dev.franek.coinflip.handler.InventoryClickHandler;
import dev.franek.coinflip.manager.CoinFlipManager;
import dev.franek.coinflip.manager.ConfigManager;
import dev.franek.coinflip.manager.EconomyManager;
import dev.franek.coinflip.manager.NotificationManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class FranekCoinFlip extends JavaPlugin {

    public static final String AUTHOR = new String(new char[]{'f', 'r', 'a', 'n', 'e', 'k', 'k'});
    public static final String GITHUB = "https://github.com/Blejziak";

    private CoinFlipManager coinFlipManager;
    private NotificationManager notificationManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        EconomyManager.setupEconomy();
        saveDefaultConfig();
        this.configManager = new ConfigManager(this);
        this.notificationManager = new NotificationManager(this);
        this.coinFlipManager = new CoinFlipManager(this, this.configManager);
        getCommand("coinflip").setExecutor(new CoinFlipCommand(this, this.notificationManager, this.configManager));
        getServer().getPluginManager().registerEvents(new InventoryClickHandler(this, this.coinFlipManager, this.notificationManager, this.configManager), this);
        checkAuthor();
        getLogger().info("Plugin " + getName() + " zaladowany pomyslnie. Made by " + AUTHOR + " (" + GITHUB + ")");
    }

    public void checkAuthor() {
        String author = getDescription().getAuthors().isEmpty() ? "" : getDescription().getAuthors().get(0);
        if (!AUTHOR.equalsIgnoreCase(author)) {
            Bukkit.getConsoleSender().sendMessage("\u00a78[\u00a7dFranekCoinFlip\u00a78] \u00a77Plugin made by \u00a7d" + AUTHOR + " \u00a78(\u00a7f" + GITHUB + "\u00a78)");
        }
    }

    @Override
    public void onDisable() {
        if (this.coinFlipManager != null) {
            this.coinFlipManager.saveGames();
            this.coinFlipManager.cleanUpOldGames();
        }
    }
}
