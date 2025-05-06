package src;
import java.io.*;
import java.util.*;

public class MarketTableGenerator {
    private Map<String, Double> previousPrices = new HashMap<>();

    public void displayTable(List<Coin> coins) {
        displayTable(coins, true);
    }
    
    public void displayTable(List<Coin> coins, boolean updatePreviousPrices) {
        displayTableWithChange(coins, updatePreviousPrices);
    }

    public void displaySimplifiedTable(List<Coin> coins) {
        DisplayString.coinTableString();
        for (Coin coin : coins) {
            System.out.printf("|  %-12s  |    %-7s|%n",
                    coin.getName(),
                    coin.getTicker());
        }
        System.out.println("|________________|___________|");
    }

    private void displayTableWithChange(List<Coin> coins, boolean updatePreviousPrices) {
        Map<String, Double> currentPrices = new HashMap<>();
        for (Coin coin : coins) {
            currentPrices.put(coin.getTicker(), coin.getPrice());
        }
        DisplayString.marketTableString();
        for (Coin coin : coins) {
            String ticker = coin.getTicker();
            double price = coin.getPrice();
            String percentChange = "0.00%";
            if (previousPrices.containsKey(ticker)) {
                double prevPrice = previousPrices.get(ticker);
                double change = ((price - prevPrice) / prevPrice) * 100;
                percentChange = String.format(change>=0?"\u001B[32m+%.2f%%\u001B[0m":"\u001B[31m%.2f%%\u001B[0m", change);

            }
            System.out.printf("|%-16s|    %-6s |  $%-11.2f |  %-18s |%n",coin.getName(), ticker, price, percentChange);
        }
        System.out.println("|_________________________________________________________|");
        if (updatePreviousPrices) {
            previousPrices = currentPrices;
        }
    }

    private Map<String, Double> getCurrentPrices() {
        Map<String, Double> prices = new HashMap<>();
        TextFileReader tr = new TextFileReader();
        prices  = tr.currentPriceReader(prices);
        return prices;

    }
    public void loadPreviousPrices() {
        TextFileReader tr = new TextFileReader();
        tr.loadPreviousPrices(previousPrices);
    }
}