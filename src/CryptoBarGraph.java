package src;

public class CryptoBarGraph {
    public static void main(String[] args) {
        // Data for each day (cryptocurrency values)
        String[] cryptocurrencies = {"Bitcoin", "Ethereum", "Binance", "Solana", "Ripple", "Cardano"};
        double[][] prices = {
                {50000, 3000, 400, 100, 0.65, 1.5},   // Day 1
                {40000, 300, 40.0, 110, 2.65, 1.50},  // Day 2
                {5000, 3000.7, 4020, 89, 5.65, 1.53}, // Day 3
                {15000, 2300, 40, 100, 0.95, 1.55}    // Day 4
        };

        // Find the max value for scaling the bars
        double maxValue = findMaxValue(prices);

        // Print the bar graph for each cryptocurrency
        for (int j = 0; j < cryptocurrencies.length; j++) {
            System.out.println("\n" + cryptocurrencies[j]); // Print cryptocurrency name

            // Loop through each day for the current cryptocurrency
            for (int i = 0; i < prices.length; i++) {
                double price = prices[i][j];
                // Scale the price to fit within 50 characters for the bar length
                int barLength = (int) ((price / maxValue) * 50); // Adjust 50 for bar length

                // Print the day and its corresponding bar
                System.out.print("day " + (i + 1) + ": ");
                for (int k = 0; k < barLength; k++) {
                    System.out.print("█");  // The bar representation
                }
                System.out.println(" " + price);  // Print the price at the end of the bar
            }
        }
    }

    // Helper method to find the maximum value in the dataset
    private static double findMaxValue(double[][] prices) {
        double maxValue = Double.MIN_VALUE;
        for (double[] dayPrices : prices) {
            for (double price : dayPrices) {
                if (price > maxValue) {
                    maxValue = price;
                }
            }
        }
        return maxValue;
    }
}
