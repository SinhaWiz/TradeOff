package src;

import java.io.*;
import java.util.*;

public class MarketTableGenerator {
    private Map<String, Double> previousPrices = new HashMap<>();

    public void displayTable(List<Coin> coins) {
        // Get current prices
        Map<String, Double> currentPrices = new HashMap<>();
        for (Coin coin : coins) {
            currentPrices.put(coin.getTicker(), coin.getPrice());
        }

        System.out.println("\n=== Current Market Prices ===");
        System.out.println();
        System.out.println("________________________________________________________");
        System.out.println(String.format("%-15s %-10s %-15s %-10s", "|    Coin", "|   Ticker  |", "   Price    |", "Change   |"));
        System.out.println("|______________________________________________________|");

        for (Coin coin : coins) {
            String ticker = coin.getTicker();
            double price = coin.getPrice();
            
            // Calculate percentage change
            String percentChange = "0.00%";
            if (previousPrices.containsKey(ticker)) {
                double prevPrice = previousPrices.get(ticker);
                double change = ((price - prevPrice) / prevPrice) * 100;


  //ahgefgagf
                //
                // Format with color and sign
                if (change >= 0) {
                    percentChange = String.format("\u001B[32m+%.2f%%\u001B[0m", change); // Green for positive
                } else {
                    percentChange = String.format("\u001B[31m%.2f%%\u001B[0m", change); // Red for negative
                }
            }
            
            System.out.printf("|%-14s |    %-6s |  $%-10.2f| %-8s" + "     | " + "\n",
                    coin.getName(),
                    ticker,
                    price,
                    percentChange);
        }
        System.out.println("|______________________________________________________|");
        
        // Store current prices as previous prices for next turn
        previousPrices = currentPrices;
    }

    private Map<String, Double> getCurrentPrices() {
        Map<String, Double> prices = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("game_state.txt"))) {
            StringBuilder content = new StringBuilder();
            String line;

            // Read the entire file
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            // Find the last game state section
            String[] sections = content.toString().split("=== Game State at");
            if (sections.length > 0) {
                // Get the last section
                String lastSection = "=== Game State at" + sections[sections.length - 1];

                // Process the last section
                Scanner scanner = new Scanner(lastSection);
                boolean readingPrices = false;

                while (scanner.hasNextLine()) {
                    line = scanner.nextLine();

                    if (line.contains("Current Market Prices:")) {
                        readingPrices = true;
                        continue;
                    }

                    if (readingPrices && !line.contains("------------------------")) {
                        // Parse price line (e.g., "BTC: $50000.00")
                        String[] parts = line.trim().split(":|\\$");
                        if (parts.length >= 2) {
                            String ticker = parts[0].trim();
                            try {
                                double price = Double.parseDouble(parts[parts.length-1].trim());
                                prices.put(ticker, price);
                            } catch (NumberFormatException e) {
                                // Skip invalid price formats
                            }
                        }
                    }

                    if (line.contains("------------------------")) {
                        break;
                    }
                }
                scanner.close();
            }
        } catch (IOException e) {
            System.out.println("Error reading game state: " + e.getMessage());
        }

        return prices;
    }
    
    // Method to load previous prices from price history file
    public void loadPreviousPrices() {
        try (BufferedReader reader = new BufferedReader(new FileReader("price_history.txt"))) {
            StringBuilder content = new StringBuilder();
            String line;
            
            // Read the entire file
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            
            // Find the second-to-last game state section if it exists
            String[] sections = content.toString().split("=== Game State at");
            if (sections.length > 2) {
                // Get the second-to-last section
                String secondLastSection = "=== Game State at" + sections[sections.length - 2];
                
                // Process the section
                Scanner scanner = new Scanner(secondLastSection);
                boolean readingPrices = false;
                
                while (scanner.hasNextLine()) {
                    line = scanner.nextLine();
                    
                    if (line.contains("Current Market Prices:")) {
                        readingPrices = true;
                        continue;
                    }
                    
                    if (readingPrices && !line.contains("------------------------")) {
                        // Parse price line (e.g., "Bitcoin (BTC): $50000.00")
                        String[] parts = line.split("\\(|\\):|\\$");
                        if (parts.length >= 3) {
                            String ticker = parts[1].trim();
                            try {
                                double price = Double.parseDouble(parts[parts.length-1].trim());
                                previousPrices.put(ticker, price);
                            } catch (NumberFormatException e) {
                                // Skip invalid price formats
                            }
                        }
                    }
                    
                    if (line.contains("------------------------")) {
                        break;
                    }
                }
                scanner.close();
            }
        } catch (IOException e) {
            // If there's an error or no previous prices, we'll just use empty map
            // which will result in 0.00% change for the first turn
        }
    }
}