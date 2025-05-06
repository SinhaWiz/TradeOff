package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class TextFileReader {
    public void loadPreviousPrices(Map<String , Double> previousPrices) {
        try (BufferedReader reader = new BufferedReader(new FileReader("price_history.txt"))) {
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            String[] sections = content.toString().split("=== Game State at");
            if (sections.length > 2) {
                String secondLastSection = "=== Game State at" + sections[sections.length - 2];
                Scanner scanner = new Scanner(secondLastSection);
                boolean readingPrices = false;

                while (scanner.hasNextLine()) {
                    line = scanner.nextLine();

                    if (line.contains("Current Market Prices:")) {
                        readingPrices = true;
                        continue;
                    }

                    if (readingPrices && !line.contains("------------------------")) {
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

        }
    }
    public Map<String, Double> currentPriceReader(Map<String , Double> prices) {
        try (BufferedReader reader = new BufferedReader(new FileReader("game_state.txt"))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            String[] sections = content.toString().split("=== Game State at");
            if (sections.length > 0) {

                String lastSection = "=== Game State at" + sections[sections.length - 1];

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
