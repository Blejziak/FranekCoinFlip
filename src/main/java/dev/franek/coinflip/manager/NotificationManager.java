package dev.franek.coinflip.manager;

import dev.franek.coinflip.FranekCoinFlip;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.configuration.file.YamlConfiguration;

public class NotificationManager {

    private final FranekCoinFlip plugin;
    private final Map<UUID, Boolean> playerNotificationSettings;
    private final File dataFile;

    public NotificationManager(FranekCoinFlip plugin) {
        this.plugin = plugin;
        this.playerNotificationSettings = new HashMap<>();
        this.dataFile = new File(plugin.getDataFolder(), "notification_settings.yml");
        loadNotificationSettings();
    }

    public boolean areNotificationsEnabled(UUID playerUUID) {
        return this.playerNotificationSettings.getOrDefault(playerUUID, true);
    }

    public void toggleNotifications(UUID playerUUID) {
        boolean currentStatus = areNotificationsEnabled(playerUUID);
        this.playerNotificationSettings.put(playerUUID, !currentStatus);
        saveNotificationSettings();
    }

    private void loadNotificationSettings() {
        if (!this.dataFile.exists()) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(this.dataFile);
        if (config.isConfigurationSection("settings")) {
            for (String uuidString : config.getConfigurationSection("settings").getKeys(false)) {
                try {
                    UUID playerUUID = UUID.fromString(uuidString);
                    boolean enabled = config.getBoolean("settings." + uuidString);
                    this.playerNotificationSettings.put(playerUUID, enabled);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }

    private void saveNotificationSettings() {
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<UUID, Boolean> entry : this.playerNotificationSettings.entrySet()) {
            config.set("settings." + entry.getKey().toString(), entry.getValue());
        }
        try {
            config.save(this.dataFile);
        } catch (IOException ignored) {
        }
    }
}
