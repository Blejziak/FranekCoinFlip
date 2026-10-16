package dev.franek.coinflip.data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoinFlipGame implements Serializable {

    private static final long serialVersionUID = 1L;
    private UUID gameId = UUID.randomUUID();
    private UUID creatorUUID;
    private String creatorName;
    private UUID opponentUUID;
    private String opponentName;
    private double betAmount;
    private long creationTimestamp;
    private UUID winnerUUID;
    private double finalPot;
    private double commissionTaken;
    private GameStatus status;
    private LocalDateTime endTime;

    public CoinFlipGame(UUID creatorUUID, String creatorName, double betAmount) {
        this.creatorUUID = creatorUUID;
        this.creatorName = creatorName;
        this.betAmount = betAmount;
        this.creationTimestamp = System.currentTimeMillis();
        this.opponentUUID = null;
        this.opponentName = null;
        this.winnerUUID = null;
        this.finalPot = 0.0;
        this.commissionTaken = 0.0;
        this.status = GameStatus.PENDING;
        this.endTime = null;
    }

    public UUID getGameId() {
        return this.gameId;
    }

    public UUID getCreatorUUID() {
        return this.creatorUUID;
    }

    public String getCreatorName() {
        return this.creatorName;
    }

    public UUID getOpponentUUID() {
        return this.opponentUUID;
    }

    public String getOpponentName() {
        return this.opponentName;
    }

    public double getBetAmount() {
        return this.betAmount;
    }

    public long getCreationTimestamp() {
        return this.creationTimestamp;
    }

    public boolean isActive() {
        return this.status == GameStatus.PENDING || this.status == GameStatus.IN_PROGRESS;
    }

    public UUID getWinnerUUID() {
        return this.winnerUUID;
    }

    public double getCommissionTaken() {
        return this.commissionTaken;
    }

    public GameStatus getStatus() {
        return this.status;
    }

    public void setOpponent(UUID opponentUUID, String opponentName) {
        this.opponentUUID = opponentUUID;
        this.opponentName = opponentName;
        this.status = GameStatus.IN_PROGRESS;
    }

    public void setWinnerUUID(UUID winnerUUID) {
        this.winnerUUID = winnerUUID;
    }

    public void setFinalPot(double finalPot) {
        this.finalPot = finalPot;
    }

    public void setCommissionTaken(double commissionTaken) {
        this.commissionTaken = commissionTaken;
    }

    public void setResult(UUID winnerUUID, double finalPot, double commissionTaken, GameStatus status) {
        this.winnerUUID = winnerUUID;
        this.finalPot = finalPot;
        this.commissionTaken = commissionTaken;
        this.status = status;
        this.endTime = LocalDateTime.now();
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("gameId", this.gameId.toString());
        map.put("creatorUUID", this.creatorUUID.toString());
        map.put("creatorName", this.creatorName);
        map.put("betAmount", this.betAmount);
        map.put("creationTimestamp", this.creationTimestamp);
        map.put("status", this.status.name());
        if (this.opponentUUID != null) {
            map.put("opponentUUID", this.opponentUUID.toString());
        }
        if (this.opponentName != null) {
            map.put("opponentName", this.opponentName);
        }
        if (this.winnerUUID != null) {
            map.put("winnerUUID", this.winnerUUID.toString());
        }
        map.put("finalPot", this.finalPot);
        map.put("commissionTaken", this.commissionTaken);
        if (this.endTime != null) {
            map.put("endTime", this.endTime.toString());
        }
        return map;
    }

    public static CoinFlipGame deserialize(Map<String, Object> map) {
        UUID gameId = UUID.fromString((String) map.get("gameId"));
        UUID creatorUUID = UUID.fromString((String) map.get("creatorUUID"));
        String creatorName = (String) map.get("creatorName");
        double betAmount = ((Number) map.get("betAmount")).doubleValue();
        long creationTimestamp = ((Number) map.get("creationTimestamp")).longValue();
        GameStatus status = GameStatus.valueOf((String) map.get("status"));
        CoinFlipGame game = new CoinFlipGame(creatorUUID, creatorName, betAmount);
        game.gameId = gameId;
        game.creationTimestamp = creationTimestamp;
        game.status = status;
        if (map.containsKey("opponentUUID")) {
            game.setOpponent(UUID.fromString((String) map.get("opponentUUID")), (String) map.get("opponentName"));
        }
        if (map.containsKey("winnerUUID")) {
            game.setWinnerUUID(UUID.fromString((String) map.get("winnerUUID")));
        }
        if (map.containsKey("finalPot")) {
            game.setFinalPot(((Number) map.get("finalPot")).doubleValue());
        }
        if (map.containsKey("commissionTaken")) {
            game.setCommissionTaken(((Number) map.get("commissionTaken")).doubleValue());
        }
        if (map.containsKey("endTime")) {
            try {
                game.endTime = LocalDateTime.parse((String) map.get("endTime"));
            } catch (DateTimeParseException e) {
                game.endTime = null;
            }
        }
        return game;
    }

    public enum GameStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED_WON,
        COMPLETED_LOSE,
        CANCELLED
    }
}
