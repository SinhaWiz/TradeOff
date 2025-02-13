//package src;
//public class CryptoBarGraph {
//    public static void main(String[] args) {
//        // Data for each day (cryptocurrency values)
//        String[] cryptocurrencies = {"Bitcoin", "Ethereum", "Binance", "Solana", "Ripple", "Cardano"};
//        double[][] prices = {
//                {50000, 3000, 400, 100, 0.65, 1.5},   // Day 1
//                {40000, 300, 40.0, 110, 2.65, 1.50},  // Day 2
//                {5000, 3000.7, 4020, 89, 5.65, 1.53}, // Day 3
//                {15000, 2300, 40, 100, 0.95, 1.55}    // Day 4
//        };
//
//        // Find the max and min values for scaling the bars
//        double[] maxValues = new double[cryptocurrencies.length];
//        double[] minValues = new double[cryptocurrencies.length];
//
//        for (int j = 0; j < cryptocurrencies.length; j++) {
//            double maxVal = Double.MIN_VALUE;
//            double minVal = Double.MAX_VALUE;
//            for (int i = 0; i < prices.length; i++) {
//                double price = prices[i][j];
//                if (price > maxVal) maxVal = price;
//                if (price < minVal) minVal = price;
//            }
//            maxValues[j] = maxVal;
//            minValues[j] = minVal;
//        }
//
//        // Print the bar graph for each cryptocurrency
//        for (int j = 0; j < cryptocurrencies.length; j++) {
//            System.out.println("\n" + cryptocurrencies[j]); // Print cryptocurrency name
//
//            // Loop through each day for the current cryptocurrency
//            for (int i = 0; i < prices.length; i++) {
//                double price = prices[i][j];
//
//                // Scale the price based on the difference between max and min value
//                double range = maxValues[j] - minValues[j];
//                if (range == 0) range = 1; // Prevent division by zero for flat values
//                int barLength = (int) (((price - minValues[j]) / range) * 50); // Scale to 50 bars max
//
//                // Ensure small values still have a minimal bar
//                if (barLength == 0 && price > 0) {
//                    barLength = 1;
//                }
//
//                // Print the day and its corresponding bar
//                System.out.print("day " + (i + 1) + ": ");
//                for (int k = 0; k < barLength; k++) {
//                    System.out.print("█");  // The bar representation
//                }
//                System.out.println(" " + price);  // Print the price at the end of the bar
//            }
//        }
//    }
//}
package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CryptoBarGraph {

    public static void generateGraph(String filePath, int numRecords) {
        List<Map<String, Double>> priceHistory = readPriceHistory(filePath, numRecords);

        if (priceHistory.isEmpty()) {
            System.out.println("No price history found in " + filePath);
            return;
        }

        // Extract cryptocurrency names and prices
        String[] cryptocurrencies = priceHistory.get(0).keySet().toArray(new String[0]);
        double[][] prices = new double[priceHistory.size()][cryptocurrencies.length];

        for (int i = 0; i < priceHistory.size(); i++) {
            Map<String, Double> dayPrices = priceHistory.get(i);
            for (int j = 0; j < cryptocurrencies.length; j++) {
                prices[i][j] = dayPrices.getOrDefault(cryptocurrencies[j], 0.0);
            }
        }

        // Generate and print the graph
        printBarGraph(cryptocurrencies, prices);
    }

    private static void printBarGraph(String[] cryptocurrencies, double[][] prices) {
        double[] maxValues = new double[cryptocurrencies.length];
        double[] minValues = new double[cryptocurrencies.length];

        // Calculate max/min values
        for (int j = 0; j < cryptocurrencies.length; j++) {
            double maxVal = Double.MIN_VALUE;
            double minVal = Double.MAX_VALUE;
            for (int i = 0; i < prices.length; i++) {
                double price = prices[i][j];
                if (price > maxVal) maxVal = price;
                if (price < minVal) minVal = price;
            }
            maxValues[j] = maxVal;
            minValues[j] = minVal;
        }

        // Print the graph
        for (int j = 0; j < cryptocurrencies.length; j++) {
            System.out.println("\n" + cryptocurrencies[j]);
            for (int i = 0; i < prices.length; i++) {
                double price = prices[i][j];
                double range = maxValues[j] - minValues[j];
                if (range == 0) range = 1;
                int barLength = (int) (((price - minValues[j]) / range) * 50);

                if (barLength == 0 && price > 0) barLength = 1;

                System.out.print("Day " + (i + 1) + ": ");
                for (int k = 0; k < barLength; k++) {
                    System.out.print("█");
                }
                System.out.println(" " + price);
            }
        }
    }

    private static List<Map<String, Double>> readPriceHistory(String filePath, int numRecords) {
        List<Map<String, Double>> priceHistory = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            Map<String, Double> currentDayPrices = new HashMap<>();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Current Market Prices:")) {
                    currentDayPrices = new HashMap<>();
                } else if (line.contains(":") && !line.startsWith("===")) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        String coinName = parts[0].trim();
                        double price = Double.parseDouble(parts[1].trim().replace("$", ""));
                        currentDayPrices.put(coinName, price);
                    }
                } else if (line.startsWith("------------------------")) {
                    if (!currentDayPrices.isEmpty()) {
                        priceHistory.add(currentDayPrices);
                        while (priceHistory.size() > numRecords) {
                            priceHistory.remove(0);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return priceHistory;
    }
}