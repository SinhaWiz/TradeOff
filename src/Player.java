package src;

import java.util.HashMap;
import java.util.Map;

public class Player {
    private double balance;
    private Map<Coin, Double> portfolio;

    public Player(double initialBalance) {
        this.balance = initialBalance;
        this.portfolio = new HashMap<>();
    }

    public double getBalance() {
        return balance;
    }

    public Map<Coin, Double> getPortfolio() {
        return new HashMap<>(portfolio);
    }

    public void updateBalance(double amount) {
        this.balance += amount;
    }

    public void addToPortfolio(Coin coin, double amount) {
        portfolio.merge(coin, amount, Double::sum);
    }
}