package dev.franek.coinflip.manager;

import dev.franek.coinflip.FranekCoinFlip;
import dev.franek.coinflip.data.CoinFlipGame;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.configuration.file.YamlConfiguration;

public class CoinFlipManager {

    private final FranekCoinFlip plugin;
    private final List<CoinFlipGame> allGames;
    private final File dataFile;
    private final ConfigManager configManager;

    public CoinFlipManager(FranekCoinFlip plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.allGames = new ArrayList<>();
        this.dataFile = new File(plugin.getDataFolder(), "games.yml");
        loadGames();
        cancelStalePendingGames();
        cleanUpOldGames();
    }

    public void addGame(CoinFlipGame game) {
        this.allGames.add(game);
        saveGames();
    }

    public List<CoinFlipGame> getActiveGames() {
        return this.allGames.stream()
                .filter(game -> game.getStatus() == CoinFlipGame.GameStatus.PENDING || game.getStatus() == CoinFlipGame.GameStatus.IN_PROGRESS)
                .collect(Collectors.toList());
    }

    public List<CoinFlipGame> getPlayerHistoryGames(UUID playerUUID) {
        return this.allGames.stream()
                .filter(game -> (game.getCreatorUUID().equals(playerUUID) || (game.getOpponentUUID() != null && game.getOpponentUUID().equals(playerUUID)))
                        && (game.getStatus() == CoinFlipGame.GameStatus.COMPLETED_WON || game.getStatus() == CoinFlipGame.GameStatus.COMPLETED_LOSE || game.getStatus() == CoinFlipGame.GameStatus.CANCELLED))
                .collect(Collectors.toList());
    }

    public Optional<CoinFlipGame> getGameById(UUID gameId) {
        return this.allGames.stream().filter(game -> game.getGameId().equals(gameId)).findFirst();
    }

    @SuppressWarnings("unchecked")
    private void loadGames() {
        if (!this.dataFile.exists()) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(this.dataFile);
        List<Map<?, ?>> gameMaps = config.getMapList("games");
        for (Map<?, ?> objMap : gameMaps) {
            try {
                this.allGames.add(CoinFlipGame.deserialize((Map<String, Object>) objMap));
            } catch (ClassCastException | IllegalArgumentException e) {
                this.plugin.getLogger().severe("Blad podczas deserializacji gry z YAML: " + e.getMessage());
            }
        }
    }

    private void cancelStalePendingGames() {
        boolean changed = false;
        for (CoinFlipGame game : this.allGames) {
            if (game.getStatus() != CoinFlipGame.GameStatus.PENDING && game.getStatus() != CoinFlipGame.GameStatus.IN_PROGRESS) {
                continue;
            }
            game.setResult(null, 0.0, 0.0, CoinFlipGame.GameStatus.CANCELLED);
            changed = true;
        }
        if (changed) {
            saveGames();
        }
    }

    public void saveGames() {
        YamlConfiguration config = new YamlConfiguration();
        List<Map<String, Object>> serializableGames = this.allGames.stream()
                .filter(game -> game.getStatus() == CoinFlipGame.GameStatus.COMPLETED_WON || game.getStatus() == CoinFlipGame.GameStatus.COMPLETED_LOSE || game.getStatus() == CoinFlipGame.GameStatus.CANCELLED)
                .map(CoinFlipGame::serialize)
                .collect(Collectors.toList());
        config.set("games", serializableGames);
        try {
            config.save(this.dataFile);
        } catch (IOException e) {
            this.plugin.getLogger().severe("Nie udalo sie zapisac gier do pliku " + this.dataFile.getName() + ": " + e.getMessage());
        }
    }

    public void cleanUpOldGames() {
        long cleanupThresholdMillis = 604800000L;
        long cutoffTime = System.currentTimeMillis() - cleanupThresholdMillis;
        Iterator<CoinFlipGame> iterator = this.allGames.iterator();
        int removedCount = 0;
        while (iterator.hasNext()) {
            CoinFlipGame game = iterator.next();
            if ((game.getStatus() != CoinFlipGame.GameStatus.COMPLETED_WON && game.getStatus() != CoinFlipGame.GameStatus.COMPLETED_LOSE && game.getStatus() != CoinFlipGame.GameStatus.CANCELLED)
                    || game.getCreationTimestamp() >= cutoffTime) {
                continue;
            }
            iterator.remove();
            ++removedCount;
        }
        if (removedCount > 0) {
            saveGames();
            this.plugin.getLogger().info("Usunieto " + removedCount + " starych gier CoinFlip z historii.");
        }
    }
}
