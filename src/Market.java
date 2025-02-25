package src;

import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Market {
    private List<Coin> coins;
    private Random random;
    private static final String INITIAL_PRICES_FILE = "initial_prices.txt";
    private static final String PRICE_HISTORY_FILE = "price_history.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final double BASE_MU = 0.0005;  // Stronger daily drift
    private static double SIGMA = 0.65; // High volatility factor
    private static final double DT = 1.0 / 252; // Time step (1 day in trading)

    public Market() {
        this.coins = new ArrayList<>();
        this.random = new Random();
        loadInitialPrices();
    }

    private void loadInitialPrices() {
        try (BufferedReader reader = new BufferedReader(new FileReader(INITIAL_PRICES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    coins.add(new Coin(parts[0], parts[1], Double.parseDouble(parts[2])));
                }
            }
        } catch (IOException e) {
            System.out.println("Using default coin values...");
            initializeDefaultCoins();
        }
    }

    private void initializeDefaultCoins() {
        coins.add(new Coin("Bitcoin", "BTC", 50000));
        coins.add(new Coin("Ethereum", "ETH", 3000));
        coins.add(new Coin("Binance Coin", "BNB", 400));
        coins.add(new Coin("Cardano", "ADA", 1.5));
        coins.add(new Coin("Solana", "SOL", 100));
        coins.add(new Coin("Dogecoin", "DOGE", 0.15));
    }

    private void setTrends() {
        for (Coin coin : coins) {
            if (!coin.isPossibleNegativeTrend() && !coin.isPossiblePositiveTrend()) {
                if (random.nextDouble() < 0.2) {
                    if (random.nextBoolean()) {
                        coin.setPossiblePositiveTrend(true);
                    } else {
                        coin.setPossibleNegativeTrend(true);
                    }
                }
            }
        }
    }

    private void removeTrends() {
        for (Coin coin : coins) {
            if (random.nextDouble() < 0.2) {
                coin.setPossiblePositiveTrend(false);
                coin.setPossibleNegativeTrend(false);
            }
        }
    }

    private double calculateChangeFactor(Coin coin) {
        double epsilon = random.nextGaussian();
        double changeFactor = 0;

        if (coin.isPossiblePositiveTrend()) {
            for (int i = 0; i < 50; i++) {
                changeFactor = Math.exp((BASE_MU - 0.5 * SIGMA * SIGMA) * DT + SIGMA * epsilon * Math.sqrt(DT));
                if (changeFactor >= 1) break;
            }
        } else if (coin.isPossibleNegativeTrend()) {
            for (int i = 0; i < 50; i++) {
                changeFactor = Math.exp((BASE_MU - 0.5 * SIGMA * SIGMA) * DT + SIGMA * epsilon * Math.sqrt(DT));
                if (changeFactor < 1) break;
            }
        } else {
            changeFactor = Math.exp((BASE_MU - 0.5 * SIGMA * SIGMA) * DT + SIGMA * epsilon * Math.sqrt(DT));
        }

        return changeFactor;
    }

    public void simulateMarketMovement(){
        setTrends();
        for (Coin coin : coins) {
            double changeFactor = calculateChangeFactor(coin);
            double newPrice = coin.getPrice() * changeFactor;
            newPrice = Math.max(0.0001, newPrice);
            coin.updatePrice(newPrice);
        }
        removeTrends();
    }

    public void incrementVolatility() {
        SIGMA = SIGMA + 0.05;
    }

//    public void simulateMarketMovement() {
//        for (Coin coin : coins) {
//            // More realistic market movement simulation
//            double volatility = getVolatility(coin);
//            double change = (random.nextGaussian() * volatility);
//            double newPrice = coin.getPrice() * (1 + change);
//            // Ensure price doesn't go below 0
//            newPrice = Math.max(0.01, newPrice);
//            coin.updatePrice(newPrice);
//        }
//        savePriceHistory();
//    }
//
//    private double getVolatility(Coin coin) {
//        // Different coins have different volatility levels
//        switch (coin.getTicker()) {
//            case "BTC":
//                return 0.05; // 5% volatility
//            case "ETH":
//                return 0.07;
//            case "BNB":
//                return 0.08;
//            case "ADA":
//                return 0.10;
//            case "SOL":
//                return 0.12;
//            case "DOGE":
//                return 0.15; // Highest volatility
//            default:
//                return 0.10;
//        }
//    }

    private void savePriceHistory() {
        try (FileWriter writer = new FileWriter(PRICE_HISTORY_FILE, true)) {
            String timestamp = LocalDateTime.now().format(formatter);
            writer.write(timestamp + "\n");
            for (Coin coin : coins) {
                writer.write(String.format("%s,%s,%.2f\n",
                        coin.getName(), coin.getTicker(), coin.getPrice()));
            }
            writer.write("------------------------\n");
        } catch (IOException e) {
            System.out.println("Error saving price history.");
        }
    }

    public List<Coin> getCoins() {
        return new ArrayList<>(coins);
    }

    public Map<Coin, Integer> predictNextMovements() {
        Map<Coin, Integer> predictions = new HashMap<>();

        for (Coin coin : coins) {
            int changeFactorFlag;

            if (coin.isPossiblePositiveTrend()) {
                changeFactorFlag = 1;
            } else if (coin.isPossibleNegativeTrend()) {
                changeFactorFlag = 0;
            } else {
                changeFactorFlag = -1;
            }

            predictions.put(coin, changeFactorFlag);
        }

        return predictions;
    }

/*
    public Map<Coin, Double> predictNextMovements() {
        Map<Coin, Double> predictions = new HashMap<>();

        for (Coin coin : coins) {
            double volatility = getVolatility(coin);
            double change = (random.nextGaussian() * volatility); 
            predictions.put(coin, change);
        }

        return predictions;
    }
*/

}