package src;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class GameController {
    private static Player player;
    private static Market market;
    private static PositionManager positions;
    private SaveManager saveManager;
    private static MarketTableGenerator tableGenerator;
    private static Scanner scanner;
    private static int turnsRemaining;
    private static final int MAX_TURNS = 10;

    public GameController() {
        this.player = new Player(100000); // Start with $100,000
        this.market = new Market();
        this.positions = new PositionManager();
        this.saveManager = new SaveManager();
        this.tableGenerator = new MarketTableGenerator();
        this.scanner = new Scanner(System.in);
        this.turnsRemaining = MAX_TURNS;
    }

    public static void startGame() {
        System.out.println("Welcome to Crypto Trading Game!");
        System.out.println("You have " + MAX_TURNS + " turns to make your fortune.");
        tableGenerator.displayTable(market.getCoins());

        while (turnsRemaining > 0) {
            System.out.println("\nTurns remaining: " + turnsRemaining);
            displayMenu();
            handleAction();
            market.simulateMarketMovement();
            saveGameState();
            turnsRemaining--;
        }

        displayFinalResults();
    }

    private static void displayMenu() {
        System.out.println();
        System.out.println();
        System.out.println("|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|         Welcome to TradeOff         |$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$| \n");
        System.out.println();
        System.out.println();
        System.out.println("You are broke . Now you have taken $1 million from Loan Sharks. " + "\n"+
                "You have to pay them $5 million dollars within 10 days ."+"\n"+ "If you don't the consequences will be beyond your imagination" +"\n"+
                "Because of this short deadline you have chosen using crypto currency to earn these money ASAP");
        System.out.println("1. View Market");
        System.out.println("2. View Portfolio");
        System.out.println("3. Open Long Position");
        System.out.println("4. Open Short Position");
        System.out.println("5. Close Position");
        System.out.println("6. Skip Turn");
        System.out.println("Balance: $" + String.format("%.2f", player.getBalance()));
    }

    private static void handleAction() {
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                tableGenerator.displayTable(market.getCoins());
                break;
            case 2:
                displayPortfolio();
                break;
            case 3:
                openLongPosition();
                break;
            case 4:
                positions.openShortPosition();
                break;
            case 5:
                positions.closePosition();
                break;
            case 6:
                skipTurn();
                break;
            default:
                System.out.println("Invalid choice! Turn skipped.");
                skipTurn();
        }
    }

    private static void displayPortfolio() {
    }

    private static void skipTurn() {
        System.out.println("Turn skipped. Market will update and affect your current positions.");
        // Existing positions will be affected by market movement
    }

    private static void saveGameState() {
        try (FileWriter writer = new FileWriter("game_state.txt", true)) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write("\n=== Game State at " + timestamp + " ===\n");
            writer.write("Turns Remaining: " + turnsRemaining + "\n");
            writer.write("Player Balance: $" + String.format("%.2f", player.getBalance()) + "\n");
            writer.write("Current Positions:\n");

            for (Trade trade : positions.viewCurrentPositions()) {
                writer.write(String.format("%s position: %d %s at %.2f\n",
                        trade.getClass().getSimpleName(),
                        trade.quantity,
                        trade.coin.getTicker(),
                        trade.coin.getPrice()));
            }

            writer.write("Current Market Prices:\n");
            for (Coin coin : market.getCoins()) {
                writer.write(String.format("%s (%s): $%.2f\n",
                        coin.getName(),
                        coin.getTicker(),
                        coin.getPrice()));
            }
            writer.write("------------------------\n");
        } catch (IOException e) {
            System.out.println("Error saving game state.");
        }
    }

    private static void displayFinalResults() {
        System.out.println("\n=== Game Over ===");
        System.out.println("Final Balance: $" + String.format("%.2f", player.getBalance()));
        System.out.println("Final Portfolio:");
        displayPortfolio();

        try (FileWriter writer = new FileWriter("final_results.txt")) {
            writer.write("=== Final Game Results ===\n");
            writer.write("Final Balance: $" + String.format("%.2f", player.getBalance()) + "\n");
            writer.write("Initial Balance: $100,000.00\n");
            writer.write("Profit/Loss: $" + String.format("%.2f", player.getBalance() - 100000) + "\n");
            writer.write("Final Portfolio:\n");
            for (Map.Entry<Coin, Double> entry : player.getPortfolio().entrySet()) {
                writer.write(String.format("%s: %.4f (Value: $%.2f)\n",
                        entry.getKey().getTicker(),
                        entry.getValue(),
                        entry.getValue() * entry.getKey().getPrice()));
            }
        } catch (IOException e) {
            System.out.println("Error saving final results.");
        }
    }

    private static void openLongPosition() {
        System.out.println("\nAvailable coins:");
        tableGenerator.displayTable(market.getCoins());

        System.out.print("Enter coin ticker: ");
        String ticker = scanner.next().toUpperCase();

        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();

        Optional<Coin> selectedCoin = market.getCoins().stream()
                .filter(c -> c.getTicker().equals(ticker))
                .findFirst();

        if (selectedCoin.isPresent()) {
            Coin coin = selectedCoin.get();
            double totalCost = coin.getPrice() * quantity;

            if (totalCost <= player.getBalance()) {
                positions.openLongPosition(coin, quantity, coin.getPrice());
                player.updateBalance(-totalCost);
                System.out.println("Long position opened successfully!");
            } else {
                System.out.println("Insufficient funds!");
            }
        } else {
            System.out.println("Invalid coin ticker!");
        }
    }
}