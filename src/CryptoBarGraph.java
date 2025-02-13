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

        // Find the max and min values for scaling the bars
        double[] maxValues = new double[cryptocurrencies.length];
        double[] minValues = new double[cryptocurrencies.length];

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

        // Print the bar graph for each cryptocurrency
        for (int j = 0; j < cryptocurrencies.length; j++) {
            System.out.println("\n" + cryptocurrencies[j]); // Print cryptocurrency name

            // Loop through each day for the current cryptocurrency
            for (int i = 0; i < prices.length; i++) {
                double price = prices[i][j];

                // Scale the price based on the difference between max and min value
                double range = maxValues[j] - minValues[j];
                if (range == 0) range = 1; // Prevent division by zero for flat values
                int barLength = (int) (((price - minValues[j]) / range) * 50); // Scale to 50 bars max

                // Ensure small values still have a minimal bar
                if (barLength == 0 && price > 0) {
                    barLength = 1;
                }

                // Print the day and its corresponding bar
                System.out.print("day " + (i + 1) + ": ");
                for (int k = 0; k < barLength; k++) {
                    System.out.print("█");  // The bar representation
                }
                System.out.println(" " + price);  // Print the price at the end of the bar
            }
        }
    }
}
