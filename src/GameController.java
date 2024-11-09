package src;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class GameController {
    private Player player;
    private Market market;
    private PositionManager positions;
    private SaveManager saveManager;
    private MarketTableGenerator tableGenerator;
    private Scanner scanner;
    private int turnsRemaining;
    private static final int MAX_TURNS = 160;

    public GameController() {
        this.player = new Player(1000000); // Start with $1,000,000 (loan shark money)
        this.market = new Market();
        this.positions = new PositionManager();
        this.saveManager = new SaveManager();
        this.tableGenerator = new MarketTableGenerator();
        this.scanner = new Scanner(System.in);
        this.turnsRemaining = MAX_TURNS;
    }

    public void startGame() {
        displayWelcomeMessage();
        while (turnsRemaining > 0) {
            System.out.println("\nTurns remaining: " + turnsRemaining);
            displayMenu();
            boolean turnUsed = handleAction();
            if(turnUsed) {
                market.simulateMarketMovement();
                updatePositions();
                saveGameState();
                turnsRemaining--;
            }
        }
        displayFinalResults();
    }

    private void displayWelcomeMessage() {
        System.out.println("\n|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$| Welcome to TradeOff |$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|\n");
        System.out.println("You are broke. Now you have taken $1 million from Loan Sharks.");
        System.out.println("You have to pay them $5 million dollars within 10 days.");
        System.out.println("If you don't, the consequences will be beyond your imagination.");
        System.out.println("Because of this short deadline, you have chosen using cryptocurrency to earn these money ASAP.\n");
        System.out.println("You have " + MAX_TURNS + " turns to make your fortune.");
        tableGenerator.displayTable(market.getCoins());
    }

    private void displayMenu() {
        System.out.println("\n=== MENU ===");
        System.out.println("1. View Market");
        System.out.println("2. View Portfolio");
        System.out.println("3. Open Long Position");
        System.out.println("4. Open Short Position");
        System.out.println("5. Close Position");
        System.out.println("6. Skip Turn");
        System.out.println("7. Skip a day");
        System.out.println("8. Exit");
        System.out.println("\nBalance: $" + String.format("%.2f", player.getBalance()));
    }

    private boolean handleAction() {
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                tableGenerator.displayTable(market.getCoins());
                return false;
            case 2:
                displayPortfolio();
                return false;
            case 3:
                return openLongPosition();
            case 4:
                return openShortPosition();
            case 5:
                closePosition();
                return false;
            case 6:
                skipTurn();
                return true;
            case 7:
                skipDay();
                return false;
            case 8:
                exitGame();
                return false;
            default:
                System.out.println("Invalid choice!Please try again.");
                return false;
        }
    }

    private void displayPortfolio() {
        System.out.println("\n=== Current Portfolio ===");
        Map<Coin, Double> portfolio = player.getPortfolio();
        if (portfolio.isEmpty()) {
            System.out.println("No positions open.");
            return;
        }

        for (Map.Entry<Coin, Double> entry : portfolio.entrySet()) {
            Coin coin = entry.getKey();
            double quantity = entry.getValue();
            double currentValue = quantity * coin.getPrice();
            System.out.printf("%s: %.4f coins (Current Value: $%.2f)\n",
                    coin.getTicker(), quantity, currentValue);
        }
    }

    private boolean openLongPosition() {
        System.out.println("\nAvailable coins:");
        tableGenerator.displayTable(market.getCoins());

        System.out.print("Enter coin ticker: ");
        String ticker = scanner.next().toUpperCase();

        Optional<Coin> selectedCoin = market.getCoins().stream()
                .filter(c -> c.getTicker().equals(ticker))
                .findFirst();

        if (selectedCoin.isPresent()) {
            System.out.print("Enter quantity: ");
            double quantity = scanner.nextDouble();
            if (quantity <= 0) {
                System.out.println("Quantity must be positive!");
                return false;
            }

            Coin coin = selectedCoin.get();
            double totalCost = coin.getPrice() * quantity;

            if (totalCost <= player.getBalance()) {
                positions.openLongPosition(coin, quantity, coin.getPrice());
                player.updateBalance(-totalCost);
                player.addToPortfolio(coin, quantity);
                System.out.println("Long position opened successfully!");
                return true;
            } else {
                System.out.println("Insufficient funds!");
                return false;
            }
        } else {
            System.out.println("Invalid coin ticker!");
            return false;
        }
    }

    private boolean openShortPosition() {
        System.out.println("\nAvailable coins:");
        tableGenerator.displayTable(market.getCoins());

        System.out.print("Enter coin ticker: ");
        String ticker = scanner.next().toUpperCase();

        Optional<Coin> selectedCoin = market.getCoins().stream()
                .filter(c -> c.getTicker().equals(ticker))
                .findFirst();

        if (selectedCoin.isPresent()) {
            System.out.print("Enter quantity: ");
            double quantity = scanner.nextDouble();
            if (quantity <= 0) {
                System.out.println("Quantity must be positive!");
                return false;
            }

            Coin coin = selectedCoin.get();
            double collateral = coin.getPrice() * quantity * 0.5; // 50% collateral required

            if (collateral <= player.getBalance()) {
                positions.openShortPosition(coin, quantity, coin.getPrice());
                player.updateBalance(-collateral);
                System.out.println("Short position opened successfully!");
                return true;
            } else {
                System.out.println("Insufficient collateral!");
                return false;
            }
        } else {
            System.out.println("Invalid coin ticker!");
            return false;
        }
    }

    private void closePosition() {
        List<Trade> currentPositions = positions.viewCurrentPositions();
        if (currentPositions.isEmpty()) {
            System.out.println("No positions to close!");
            return;
        }

        System.out.println("\nCurrent Positions:");
        for (int i = 0; i < currentPositions.size(); i++) {
            Trade trade = currentPositions.get(i);
            System.out.printf("%d. %s position: %.4f %s at $%.2f (P/L: $%.2f)\n",
                    i + 1,
                    trade.getClass().getSimpleName(),
                    trade.getQuantity(),
                    trade.getCoin().getTicker(),
                    trade.getEntryPrice(),
                    trade.calcGainLoss());
        }

        System.out.print("Enter position number to close (1-" + currentPositions.size() + "): ");
        int choice = scanner.nextInt() - 1;

        if (choice >= 0 && choice < currentPositions.size()) {
            Trade trade = currentPositions.get(choice);
            double gainLoss = trade.calcGainLoss();
            player.updateBalance(gainLoss);

            if (trade instanceof LongTrade) {
                player.removeFromPortfolio(trade.getCoin(), trade.getQuantity());
            } else if (trade instanceof ShortTrade) {

                player.updateBalance(trade.getCoin().getPrice() * trade.getQuantity() * 0.5);
            }

            positions.closePosition(choice);
            System.out.printf("Position closed. Profit/Loss: $%.2f\n", gainLoss);
        } else {
            System.out.println("Invalid position number!");
        }
    }
    private void exitGame(){
        System.out.println("\nExiting game...................................");
        System.exit(0);
    }
    private void skipTurn() {
        System.out.println("Turn skipped. Market will update and affect your current positions.");
    }
    private void skipDay() {
        if(turnsRemaining%16 == 0  ){
            turnsRemaining-=16;
        }
        else {
            turnsRemaining -= turnsRemaining % 16;
        }
        System.out.println("Day skipped. Market will update and affect your current positions.");
    }
    private void updatePositions() {
        for (Trade trade : positions.viewCurrentPositions()) {
            // Update unrealized P/L
            double gainLoss = trade.calcGainLoss();
            // Check for liquidation in short positions
            if (trade instanceof ShortTrade && gainLoss < -player.getBalance()) {
                System.out.println("WARNING: Short position liquidated due to insufficient funds!");
                positions.closePosition(positions.viewCurrentPositions().indexOf(trade));
                player.updateBalance(gainLoss);
            }
        }
    }

    private void saveGameState() {
        try (FileWriter writer = new FileWriter("game_state.txt", true)) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write("\n=== Game State at " + timestamp + " ===\n");
            writer.write("Turns Remaining: " + turnsRemaining + "\n");
            writer.write("Player Balance: $" + String.format("%.2f", player.getBalance()) + "\n");
            writer.write("Current Positions:\n");

            for (Trade trade : positions.viewCurrentPositions()) {
                writer.write(String.format("%s position: %.4f %s at $%.2f (P/L: $%.2f)\n",
                        trade.getClass().getSimpleName(),
                        trade.getQuantity(),
                        trade.getCoin().getTicker(),
                        trade.getEntryPrice(),
                        trade.calcGainLoss()));
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

    private void displayFinalResults() {
        double finalBalance = player.getBalance();
        System.out.println("\n=== Game Over ===");
        System.out.println("Final Balance: $" + String.format("%.2f", finalBalance));
        System.out.println("Initial Balance: $1,000,000.00");
        System.out.println("Profit/Loss: $" + String.format("%.2f", finalBalance - 1000000));

        if (finalBalance >= 5000000) {
            System.out.println("\nCongratulations! You've paid off the loan sharks and made a profit!");
        } else {
            System.out.println("\nGame Over! The loan sharks are coming for you...");
        }

        System.out.println("\nFinal Portfolio:");
        displayPortfolio();

        try (FileWriter writer = new FileWriter("final_results.txt")) {
            writer.write("=== Final Game Results ===\n");
            writer.write("Final Balance: $" + String.format("%.2f", finalBalance) + "\n");
            writer.write("Initial Balance: $1,000,000.00\n");
            writer.write("Profit/Loss: $" + String.format("%.2f", finalBalance - 1000000) + "\n");
            writer.write("Goal Achieved: " + (finalBalance >= 5000000 ? "Yes" : "No") + "\n");
            writer.write("\nFinal Portfolio:\n");
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
}
