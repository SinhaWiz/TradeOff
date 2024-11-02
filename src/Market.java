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
            System.out.println("Error loading initial prices. Using default values.");
            initializeDefaultCoins();
        }
    }

    private void initializeDefaultCoins() {
        coins.add(new Coin("Bitcoin", "BTC", 50000));
        coins.add(new Coin("Ethereum", "ETH", 3000));
        coins.add(new Coin("Dogecoin", "DOGE", 0.15));
    }

    public void simulateMarketMovement() {
        for (Coin coin : coins) {
            double change = (random.nextDouble() - 0.5) * 0.1; // -5% to +5% change
            double newPrice = coin.getPrice() * (1 + change);
            coin.updatePrice(newPrice);
        }
        savePriceHistory();
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
        return coins;
    }

    // ... (rest of the Market class methods remain the same)
}