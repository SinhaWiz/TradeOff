package src;

import java.time.LocalDateTime;

public abstract class Trade {
    protected double quantity;
    protected Coin coin;
    protected LocalDateTime tradeTime;
    protected boolean isOpen;

    public Trade(Coin coin, double quantity) {
        this.coin = coin;
        this.quantity = quantity;
        this.tradeTime = LocalDateTime.now();
        this.isOpen = true;
    }

    public abstract void executeTrade();

    public abstract double calcGainLoss();

    public abstract void closeTrade();

    public boolean isOpen() {
        return isOpen;
    }

    public LocalDateTime getTradeTime() {
        return tradeTime;
    }
}



