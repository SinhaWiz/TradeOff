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
        this.balance = Math.max(0, this.balance + amount);
    }

    public void deductBalance(double amount) {
        if (balance >= amount) {
            this.balance -= amount;
        } else {
            System.out.println("Not enough balance!");
        }
    }

    public void addToPortfolio(Coin coin, double amount) {
        if (amount > 0) {
            portfolio.merge(coin, amount, Double::sum);
        }
    }

    public void removeFromPortfolio(Coin coin, double amount) {
        double currentAmount = portfolio.getOrDefault(coin, 0.0);

        if (currentAmount <= 0) {
            System.out.println("You don't own this coin.");
            return;
        }

        if (amount >= currentAmount) {
            portfolio.remove(coin);
        } else {
            portfolio.put(coin, currentAmount - amount);
        }
    }
}
