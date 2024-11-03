package src;

public abstract class Trade {
    protected double quantity;
    protected Coin coin;
    protected double entryPrice;
    protected int leverage;

    public Trade(Coin coin, double quantity, double entryPrice, int leverage) {
        this.coin = coin;
        this.quantity = quantity;
        this.entryPrice = entryPrice;
        this.leverage = leverage;
    }

    public abstract double calcGainLoss();

    public Coin getCoin() {
        return coin;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getEntryPrice() {
        return entryPrice;
    }
}