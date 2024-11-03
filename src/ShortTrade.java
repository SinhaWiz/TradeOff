package src;

public class ShortTrade extends Trade {
    public ShortTrade(Coin coin, double quantity, double sellPrice) {
        super(coin, quantity, sellPrice);
    }

    @Override
    public double calcGainLoss() {
        return quantity * (entryPrice - coin.getPrice());
    }
}