package src;

public class ShortTrade extends Trade {
    public ShortTrade(Coin coin, double quantity, double entryPrice, int leverage) {
        super(coin, quantity, entryPrice, leverage);
    }

    @Override
    public double calcGainLoss() {
        return quantity * (entryPrice - coin.getPrice()) * leverage;
    }
}