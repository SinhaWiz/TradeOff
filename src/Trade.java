package src;

public abstract class Trade {
    protected int quantity;
    protected Coin coin;

    public Trade(Coin coin, int quantity) {
        this.coin = coin;
        this.quantity = quantity;
    }

    public abstract void executeTrade();

    public double calcGainLoss() {
        return 0.0;
    }
}