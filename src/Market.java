package src;

import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Market implements Serializable {
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

    public static double getSIGMA() {
        return SIGMA;
    }

    public static void setSIGMA(double SIGMA) {
        Market.SIGMA = SIGMA;
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
                if (random.nextDouble() < 0.6) {
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
            if (random.nextDouble() < 0.4) {
                coin.setPossiblePositiveTrend(false);
                coin.setPossibleNegativeTrend(false);
            }
        }
    }

    public double calculateChangeFactor(Coin coin) {
        double epsilon = random.nextGaussian();
        double changeFactor = 0;

        if (coin.isPossiblePositiveTrend()) {
            for (int i = 0; i < 50000; i++) {
                changeFactor = Math.exp((BASE_MU - 0.5 * SIGMA * SIGMA) * DT + SIGMA * epsilon * Math.sqrt(DT));
                if (changeFactor >= 1) break;
            }
        } else if (coin.isPossibleNegativeTrend()) {
            for (int i = 0; i < 50000; i++) {
                changeFactor = Math.exp((BASE_MU - 0.5 * SIGMA * SIGMA) * DT + SIGMA * epsilon * Math.sqrt(DT));
                if (changeFactor < 1) break;
            }
        } else {
            changeFactor = Math.exp((BASE_MU - 0.5 * SIGMA * SIGMA) * DT + SIGMA * epsilon * Math.sqrt(DT));
        }

        return changeFactor;
    }

    public void simulateMarketMovement() {
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

    public void setCoins(List<Coin> coins) {
        this.coins = coins;
    }

    public Map<Coin, Integer> predictNextMovements() {
        Map<Coin, Integer> predictions = new HashMap<>();

        for (Coin coin : coins) {
            int changeFactorFlag;
            if (coin.isPossiblePositiveTrend()) {
                changeFactorFlag = 1;  // Positive trend
            } else if (coin.isPossibleNegativeTrend()) {
                changeFactorFlag = 0;  // Negative trend
            } else {
                changeFactorFlag = -1;  // No clear trend
            }


            predictions.put(coin, changeFactorFlag);
        }

        return predictions;
    }

}