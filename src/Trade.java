public abstract class Trade {
    protected double quantity;
    protected Coin coin;
    protected double entryPrice;

    public Trade(Coin coin, double quantity, double entryPrice) {
        this.coin = coin;
        this.quantity = quantity;
        this.entryPrice = entryPrice;
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
