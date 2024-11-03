package src;
import java.util.List;

public class MarketTableGenerator {
    public void generatePriceTable() {

    }

    public double getPriceChange(double oldPrice, double newPrice) {
        return ((newPrice - oldPrice) / oldPrice) * 100;
    }

    public void displayTable(List<Coin> coins) {
        System.out.println("\n=== Market Prices ===");
        System.out.println("Coin\tPrice\t\tTicker");
        System.out.println("--------------------------------");
        for (Coin coin : coins) {
            System.out.printf("%s\t$%.2f\t%s\n",
                    coin.getName(), coin.getPrice(), coin.getTicker());
        }
    }
}