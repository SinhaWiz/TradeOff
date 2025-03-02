package src;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
public class GameController {
    private Player player;
    private Market market;
    private PositionManager positions;
    private SaveManager saveManager;
    private MarketTableGenerator tableGenerator;
    private Scanner scanner;
    private int turnsRemaining;
    private static final int MAX_TURNS = 160;
    private Random gameRandom;
    private PriceHistoryLoader priceHistoryLoader;
    private int marketAnalystAttempts = 3;

    public GameController() {
        this.player = new Player(1000000); // Start with $1,000,000 (loan shark money)
        this.market = new Market();
        this.positions = new PositionManager();
        this.saveManager = new SaveManager();
        this.tableGenerator = new MarketTableGenerator();
        this.scanner = new Scanner(System.in);
        this.turnsRemaining = MAX_TURNS;
        this.gameRandom = new Random();
        PriceHistoryLoader.init();
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
                clearConsole();
                randomPriceAlertEvent();
                randomVolatilityEvent();
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
    }

    private void displayMenu() {

        System.out.println("\n     |$|$|$|$| |$|$|$|$| |$|$|$|$| |$|$|$|$| |$|$|$|$| MENU |$|$|$|$| |$|$|$|$| |$|$|$|$| |$|$|$|$| |$|$|$|$|");
        System.out.println("|+|+|+| 1.View Market        |+|+|+| 2.View Portfolio|+|+|+| 3.Close Position|+|+|+| 4.Open Long Position             |+|+|+|");
        System.out.println("|+|+|+| 5.Open Short Position|+|+|+| 6.Skip Turn     |+|+|+| 7.Skip a day    |+|+|+| 8.Market Analyst ($75000) (" + marketAnalystAttempts + "/3)  |+|+|+|");
        System.out.println("|+|+|+| 9.Statistics         |+|+|+|                                         |+|+|+| 10.Exit Game                     |+|+|+|");
        System.out.println("\nBalance: $" + String.format("%.2f", player.getBalance()));
    }

    private boolean handleAction() {
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                clearConsole();
                tableGenerator.displayTable(market.getCoins());
                return false;
            case 2:
                clearConsole();
                displayPositions();
                return false;
            case 3:
                clearConsole();
                closePosition();
                return false;
            case 4:
                return openLongPosition() ;
            case 5:
                return openShortPosition();
            case 6:
                skipTurn();
                return true;
            case 7:
                skipDay();
                clearConsole();
                return false;
            case 8:
                predictNextMovement();
                return false;
            case 9:
                CryptoBarGraph.generateGraph("game_state.txt",5);
                return false;
            case 10:
                exitGame();
                return false;
            default:
                System.out.println("Invalid choice! Please try again.");
                return false;
        }
    }

    private void predictNextMovement() {
        if (marketAnalystAttempts > 0) {
            if (player.getBalance() >= 75000) {
                player.deductBalance(75000);  // Deduct balance for market analyst fee
                marketAnalystAttempts--;  // Reduce available attempts

                Map<Coin, Integer> predictions = market.predictNextMovements();

                System.out.println("Insider's Report:");
                for (Map.Entry<Coin, Integer> entry : predictions.entrySet()) {
                    Coin coin = entry.getKey();
                    int changeFactor = entry.getValue();

                    if (changeFactor == 1) {
                        System.out.println(coin.getTicker() + " may go up.");
                    } else if (changeFactor == 0) {
                        System.out.println(coin.getTicker() + " may go down.");
                    } else {
                        System.out.println(coin.getTicker() + " may go either way.");
                    }
                }

                System.out.println("Attempts left: " + marketAnalystAttempts);
            } else {
                System.out.println("Not enough balance ($30000 required) ");
            }
        } else {
            System.out.println("You have exceeded the maximum number of attempts to consult the market analyst.");
        }
    }




    private void displayPositions() {
        List<Trade> currentPositions = positions.getPositions();
        if (currentPositions.isEmpty()) {
            System.out.println("No positions open.");
            return;
        }

        System.out.println("\n=== Current Positions ===");
        for (int i = 0; i < currentPositions.size(); i++) {
            Trade trade = currentPositions.get(i);
            if (trade instanceof LongTrade) {
                System.out.printf("%d. %dX Long: %.4f %s at $%.2f (P/L: $%.2f)\n",
                        i + 1,
                        trade.getLeverage(),
                        trade.getQuantity(),
                        trade.getCoin().getTicker(),
                        trade.getEntryPrice(),
                        trade.calcGainLoss());
            } else if (trade instanceof ShortTrade) {
                System.out.printf("%d. %dX Short: %.4f %s at $%.2f (P/L: $%.2f)\n",
                        i + 1,
                        trade.getLeverage(),
                        trade.getQuantity(),
                        trade.getCoin().getTicker(),
                        trade.getEntryPrice(),
                        trade.calcGainLoss());
            }
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
            System.out.println("Select leverage: 1. 2x   2. 5x   3. 10x   4. 25x   5. 50x   6. No Leverage");
            int leverage = 0;
            int leverageOption = scanner.nextInt();
            switch(leverageOption) {
                case 1:
                    leverage = 2;
                    break;
                case 2:
                    leverage = 5;
                    break;
                case 3:
                    leverage = 10;
                    break;
                case 4:
                    leverage = 25;
                    break;
                case 5:
                    leverage = 50;
                    break;
                case 6:
                    leverage = 1;
                    break;
                default:
                    System.out.println("Invalid choice! Select Again.");
                    leverageOption = scanner.nextInt();
            }

            System.out.print("Enter quantity: ");
            double quantity = scanner.nextDouble();
            if (quantity <= 0) {
                System.out.println("Quantity must be positive!");
                return false;
            }

            Coin coin = selectedCoin.get();
            double tradingAmount = coin.getPrice() * quantity;

            if (tradingAmount <= player.getBalance()) {
                positions.openLongPosition(coin, quantity, coin.getPrice(), leverage);
                player.updateBalance(-tradingAmount);
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
            System.out.println("Select leverage: 1. 2x   2. 5x   3. 10x   4. 25x   5. 50x   6. No Leverage");
            int leverage = 0;
            int leverageOption = scanner.nextInt();
            switch(leverageOption) {
                case 1:
                    leverage = 2;
                    break;
                case 2:
                    leverage = 5;
                    break;
                case 3:
                    leverage = 10;
                    break;
                case 4:
                    leverage = 25;
                    break;
                case 5:
                    leverage = 50;
                    break;
                case 6:
                    leverage = 1;
                    break;
                default:
                    System.out.println("Invalid choice! Select Again.");
                    leverageOption = scanner.nextInt();
            }

            System.out.print("Enter quantity: ");
            double quantity = scanner.nextDouble();
            if (quantity <= 0) {
                System.out.println("Quantity must be positive!");
                return false;
            }

            Coin coin = selectedCoin.get();
            double collateral = coin.getPrice() * quantity * 0.5 * leverage; // 50% collateral required
            double tradingAmount = coin.getPrice() * quantity;

            if (collateral <= player.getBalance()) {
                positions.openShortPosition(coin, quantity, coin.getPrice(), leverage);
                player.updateBalance(-tradingAmount);
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
        List<Trade> currentPositions = positions.getPositions();
        if (currentPositions.isEmpty()) {
            System.out.println("No positions to close!");
            return;
        }
        System.out.println("\nCurrent Positions:");
        for (int i = 0; i < currentPositions.size(); i++) {
            Trade trade = currentPositions.get(i);
            if (trade instanceof LongTrade) {
                System.out.printf("%d. %dX Long: %.4f %s at $%.2f (P/L: $%.2f)\n",
                        i + 1,
                        trade.getLeverage(),
                        trade.getQuantity(),
                        trade.getCoin().getTicker(),
                        trade.getEntryPrice(),
                        trade.calcGainLoss());
            } else if (trade instanceof ShortTrade) {
                System.out.printf("%d. %dX Short: %.4f %s at $%.2f (P/L: $%.2f)\n",
                        i + 1,
                        trade.getLeverage(),
                        trade.getQuantity(),
                        trade.getCoin().getTicker(),
                        trade.getEntryPrice(),
                        trade.calcGainLoss());
            }
        }

        System.out.print("Enter position number to close (1-" + currentPositions.size() + "): ");
        int choice = scanner.nextInt() - 1;
        if (choice >= 0 && choice < currentPositions.size()) {
            Trade trade = currentPositions.get(choice);
            double closingAmount = trade.calcGainLoss() + (trade.getEntryPrice() * trade.getQuantity());
            player.updateBalance(closingAmount);
            positions.closePosition(choice);
            System.out.printf("Position closed. Profit/Loss: $%.2f\n", trade.calcGainLoss());
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
        if (turnsRemaining%16 == 0) {
            for (int  i = 0; i < 16; i++) {
                turnsRemaining--;
                market.simulateMarketMovement();
                updatePositions();
                saveGameState();
            }
        } else {
          int turnsToSkip = turnsRemaining%16;
            for (int  i = 0; i < turnsToSkip; i++) {
                turnsRemaining--;
                market.simulateMarketMovement();
                updatePositions();
                saveGameState();
            }
        }
        System.out.println("Day skipped. Market will update and affect your current positions.");
    }

    private void updatePositions() {
        List<Trade> listOfPositions = positions.getPositions();
        Iterator<Trade> positionsIterator = listOfPositions.iterator();
        while (positionsIterator.hasNext()) {
            Trade trade = positionsIterator.next();
            // Update unrealized P/L
            double gainLoss = trade.calcGainLoss();
            // Check for liquidation in short positions
            if (trade instanceof ShortTrade && gainLoss < -(player.getBalance()/2)) {
                System.out.println("WARNING: Short position liquidated due to insufficient funds!");
                player.updateBalance(gainLoss + (trade.quantity * trade.entryPrice));
                positionsIterator.remove();
                // positions.closePosition(positions.getPositions().indexOf(trade));
            } else if (trade instanceof LongTrade && gainLoss < -(player.getBalance()/2)) {
                System.out.println("WARNING: Long position liquidated due to insufficient funds!");
                // positions.closePosition(positions.getPositions().indexOf(trade));
                player.updateBalance(gainLoss + (trade.quantity * trade.entryPrice));
                positionsIterator.remove();
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

            for (Trade trade : positions.getPositions()) {
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

    private void randomPriceAlertEvent() {
        if (gameRandom.nextDouble() < 0.15 && turnsRemaining != 0) {
            priceAlertEvent();
        }
    }

    private void priceAlertEvent() {
        List<Coin> coins = market.getCoins();
        List<Coin> negativeCoins = new ArrayList<>();
        List<Coin> positiveCoins = new ArrayList<>();

        for (Coin coin : coins) {
            if (coin.isPossibleNegativeTrend()) {
                negativeCoins.add(coin);
            } else if (coin.isPossiblePositiveTrend()) {
                positiveCoins.add(coin);
            }
        }

        if (!negativeCoins.isEmpty() || !positiveCoins.isEmpty()) {
            System.out.println("|$||$||$||$||$| BREAKING NEWS |$||$||$||$||$|");
        }

        if (!negativeCoins.isEmpty()) {
            Coin negativeCoin = negativeCoins.get(gameRandom.nextInt(negativeCoins.size()));
            if (gameRandom.nextBoolean()) {
                System.out.println("DOOM! GLOOM! AND SOME MORE! " + negativeCoin.getTicker() + " WILL SURELY CRASH ANYTIME NOW! GRAB YOUR POPCORN AND WATCH THE WORLD BURN!"); // good event
            } else {
                System.out.println("ALERT! ALERT! ALERT! " + negativeCoin.getTicker() + " may just SKYROCKET and GO TO THE MOON! DON'T MISS OUT ON AN OPPORTUNITY OF A LIFETIME!"); // scam event
            }
        }

        if (!positiveCoins.isEmpty()) {
            Coin positiveCoin = positiveCoins.get(gameRandom.nextInt(positiveCoins.size()));
            if (!gameRandom.nextBoolean()) {
                System.out.println("DOOM! GLOOM! AND SOME MORE! " + positiveCoin.getTicker() + " WILL SURELY CRASH ANYTIME NOW! GRAB YOUR POPCORN AND WATCH THE WORLD BURN!"); // scam event
            } else {
                System.out.println("ALERT! ALERT! ALERT! " + positiveCoin.getTicker() + " may just SKYROCKET and GO TO THE MOON! DON'T MISS OUT ON AN OPPORTUNITY OF A LIFETIME!"); // good event
            }
        }
    }

    private void randomVolatilityEvent() {
        if (gameRandom.nextDouble() < 0.05 && turnsRemaining != 0) {
            market.incrementVolatility();
            System.out.println("MARKET ALERT: Due to recent developments, expect more volatility when trading.");
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

    private void clearConsole(){
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
