package src;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
//Done
public class PriceHistoryLoader {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Make this method public and static
    public static void init() {
        loadInitialPriceHistory();
    }

    private static void loadInitialPriceHistory() {
        String initialPricesFile = "initial_prices.txt";
        String gameStateFile = "game_state.txt";
        List<Map<String, Double>> priceHistory = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(initialPricesFile))) {
            Map<String, Double> currentPrices = new HashMap<>();
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("/")) continue;

                String[] parts = line.split(",");
                if (parts.length == 3) {
                    currentPrices.put(parts[1], Double.parseDouble(parts[2]));
                } else if (line.trim().isEmpty() && !currentPrices.isEmpty()) {
                    priceHistory.add(new HashMap<>(currentPrices));
                    currentPrices.clear();
                }
            }

            if (!currentPrices.isEmpty()) {
                priceHistory.add(currentPrices);
            }

            writeToGameState(priceHistory, gameStateFile);

        } catch (IOException e) {
            System.out.println("Error loading initial price history: " + e.getMessage());
        }
    }

    private static void writeToGameState(List<Map<String, Double>> priceHistory, String gameStateFile) {
        try (FileWriter writer = new FileWriter(gameStateFile)) {
            for (Map<String, Double> prices : priceHistory) {
                String timestamp = LocalDateTime.now().minusDays(priceHistory.size() - priceHistory.indexOf(prices))
                        .format(formatter);
                writer.write("\n=== Game State at " + timestamp + " ===\n");
                writer.write("Turns Remaining: 160\n");
                writer.write("Player Balance: $1000000.00\n");
                writer.write("Current Positions:\n");
                writer.write("Current Market Prices:\n");

                for (Map.Entry<String, Double> entry : prices.entrySet()) {
                    writer.write(String.format("%s: $%.2f\n", entry.getKey(), entry.getValue()));
                }
                writer.write("------------------------\n");
            }
        } catch (IOException e) {
            System.out.println("Error writing to game state file: " + e.getMessage());
        }
    }
}