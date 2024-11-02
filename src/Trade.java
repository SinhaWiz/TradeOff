package src;

public class Trade {
    private double quantity;
    private double coinPrice;
    private final double tradingAmount = quantity * coinPrice;

    public double getQuantity() {
        return quantity;
    }

    public double getCoinPrice() {
        return coinPrice;
    }

    public double getTradingAmount() {
        return tradingAmount;
    }

    public double longTrade(double initialAmount, double percentageChange, double leverage) {
        double leveragedChange = percentageChange * leverage;
        return initialAmount * (1 + leveragedChange / 100);
    }

    public double shortTrade(double initialAmount, double percentageChange, double leverage) {
        double leveragedChange = percentageChange * leverage;
        return initialAmount * (1 - leveragedChange / 100);
    }
}
