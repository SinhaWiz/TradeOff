package src;

public class LongTrade extends Trade {
    public LongTrade(Coin coin, double quantity, double entryPrice, int leverage) {
        super(coin, quantity, entryPrice, leverage);
    }

    @Override
    public double calcGainLoss() {
        return quantity * (coin.getPrice() - entryPrice) * leverage;
    }
}
