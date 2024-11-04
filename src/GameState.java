package src;
import java.io.Serializable;
import java.util.*;
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private double playerBalance;
    private Map<String, Double> portfolio;
    private List<TradeRecord> openPositions;
    private int turnsRemaining;

    public GameState(double balance, Map<String, Double> portfolio,
                     List<TradeRecord> positions, int turns) {
        this.playerBalance = balance;
        this.portfolio = portfolio;
        this.openPositions = positions;
        this.turnsRemaining = turns;
    }

    public double getPlayerBalance() {
        return playerBalance;
    }
    public Map<String, Double> getPortfolio() {
        return portfolio;
    }
    public List<TradeRecord> getOpenPositions() {
        return openPositions;
    }
    public int getTurnsRemaining() {
        return turnsRemaining;
    }
}