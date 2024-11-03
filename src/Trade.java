package src;

public abstract class Trade {
    protected double quantity;
    protected Coin coin;

    public Trade(Coin coin, double quantity) {
        this.coin = coin;
        this.quantity = quantity;
    }

    public abstract void executeTrade();

    public double calcGainLoss() {
        return 0.0;
    }
}