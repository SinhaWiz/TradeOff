package src;

import java.util.List;

public class MarketTableGenerator {
    public void displayTable(List<Coin> coins) {
        System.out.println("\n=== Current Market Prices ===");
        System.out.println(String.format("%-15s %-10s %-15s", "|    Coin", "|   Ticker  |", "    Price     |"));
        System.out.println("|___________________________________________|");

        for (Coin coin : coins) {
            System.out.printf("|%-15s %-10s $%-15.2f" +"|"+
                            "\n",
                    coin.getName(),
                    coin.getTicker(),
                    coin.getPrice());
        }
        System.out.println("|___________________________________________|");
    }
}
