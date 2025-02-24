package src;

import java.io.*;
import java.util.*;

public class MarketTableGenerator {
    public void displayTable(List<Coin> coins) {
        Map<String, Double> currentPrices = getCurrentPrices();

        // Use a Set to track processed coins and avoid duplicates
        Set<String> processedCoins = new HashSet<>();

        System.out.println("\n=== Current Market Prices ===");
        System.out.println(String.format("%-15s %-10s %-15s", "|    Coin", "|   Ticker  |", "    Price     |"));
        System.out.println("|___________________________________________|");

        for (Coin coin : coins) {
            String ticker = coin.getTicker();
            // Only process each coin once
            if (!processedCoins.contains(ticker)) {
                double price = currentPrices.getOrDefault(ticker, coin.getPrice());
                System.out.printf("|%-15s %-10s $%-15.2f" + "|" + "\n",
                        coin.getName(),
                        ticker,
                        price);
                processedCoins.add(ticker);
            }
        }
        System.out.println("|___________________________________________|");
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
}