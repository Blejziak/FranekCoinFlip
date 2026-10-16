package dev.franek.coinflip.commands;

import dev.franek.coinflip.FranekCoinFlip;
import dev.franek.coinflip.gui.MainGUI;
import dev.franek.coinflip.manager.ConfigManager;
import dev.franek.coinflip.manager.NotificationManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoinFlipCommand implements CommandExecutor {

    private final FranekCoinFlip plugin;
    private final NotificationManager nm;
    private final ConfigManager cm;

    public CoinFlipCommand(FranekCoinFlip plugin, NotificationManager nm, ConfigManager cm) {
        this.plugin = plugin;
        this.nm = nm;
        this.cm = cm;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Komenda tylko dla graczy. Plugin made by " + FranekCoinFlip.AUTHOR);
            return true;
        }
        Player player = (Player) sender;
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (player.hasPermission("franek.coinflip") || player.isOp()) {
                this.cm.reloadConfig();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aPomyslnie przeladowano config &2(FranekCoinFlip - made by " + FranekCoinFlip.AUTHOR + ")"));
                return true;
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cBrak uprawnien &4(franek.coinflip)"));
            return true;
        }
        new MainGUI(player, this.nm, this.cm).openInventory();
        return true;
    }
}
