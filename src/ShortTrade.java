package src;

public class ShortTrade extends Trade {
    private double sellPrice;

    public ShortTrade(Coin coin, double quantity, double sellPrice) {
        super(coin, quantity);
        this.sellPrice = sellPrice;
    }

    @Override
    public void executeTrade() {
        // Implementation for executing a short trade
    }

    @Override
    public double calcGainLoss() {
        return quantity * (sellPrice - coin.getPrice());
    }
}