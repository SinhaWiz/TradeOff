package src;
import java.io.*;
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
    private static final String SAVE_FILE = "game_save.dat";
    private Random gameRandom;
    private PriceHistoryLoader priceHistoryLoader;
    private int marketAnalystAttempts = 3;

    public GameController() {
        this.player = new Player(1000000); // Start with $1,000,000 (loan shark money)
        this.market = new Market();
        this.positions = new PositionManager();
        this.saveManager = new SaveManager();
        this.tableGenerator = new MarketTableGenerator();
        this.tableGenerator.loadPreviousPrices(); // Load previous prices for percentage change calculation
        this.scanner = new Scanner(System.in);
        this.turnsRemaining = MAX_TURNS;
        this.gameRandom = new Random();
        PriceHistoryLoader.init();

        // Ensure price_history.txt exists
        try {
            new FileWriter("price_history.txt", true).close();
        } catch (IOException e) {
            System.out.println("Error creating price history file.");
        }
    }

    public void displayStartMenu() {
        displayWelcomeMessage();
        System.out.println("1. Start New Game");
        System.out.println("2. Load Game");
        System.out.println("3. Exit");

        System.out.print("Enter your choice: ");
        while (true) {
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    clearConsole();
                    startGame();
                    break;
                case 2:
                    clearConsole();
                    loadGameStartMenu();
                    break;
                case 3:
                    exitGame();
                    break;
                default:
                    System.out.println("Invalid option. Please enter again.");
                    System.out.print("Enter your choice: ");
            }
        }
    }

    public void startGame() {
        while (turnsRemaining > 0) {
            System.out.println("\nTurns remaining: " + turnsRemaining);
            displayMenu();
            boolean turnUsed = handleAction();
            if(turnUsed) {
                completeTurn();
                clearConsole();
                randomPriceAlertEvent();
                randomVolatilityEvent();
            }
        }
        displayFinalResults();
    }


    private void savePriceHistory() {
        try (FileWriter writer = new FileWriter("price_history.txt", true)) {
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
            System.out.println("Error saving price history.");
        }
    }

    public void displayWelcomeMessage() {
        System.out.println("\n|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$| Welcome to TradeOff |$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|$|\n");
        System.out.println("You are broke and borrowed $1,000,000 from Loan Sharks.");
        System.out.println("You have to pay them $3,000,000 within 10 days.");
        System.out.println("If you don't, the consequences will be beyond your imagination.");
        System.out.println("Because of this short deadline, you have opted for cryptocurrency trading to earn this money ASAP.\n");
        System.out.println("You have " + MAX_TURNS + " turns to make your fortune.");
    }

    public void displayMenu() {
        System.out.println("\n     |$|$|$|$| |$|$|$|$| |$|$|$|$| |$|$|$|$| |$|$|$|$| MENU |$|$|$|$| |$|$|$|$| |$|$|$|$| |$|$|$|$| |$|$|$|$|");
        System.out.println("|+|+|+| 1.View Market         |+|+|+| 2.View Portfolio |+|+|+| 3.Close Position       |+|+|+| 4.Open Long Position             |+|+|+|");
        System.out.println("|+|+|+| 5.Open Short Position |+|+|+| 6.Skip Turn      |+|+|+| 7.Skip a Day           |+|+|+| 8.Black Market Analyst  (" + marketAnalystAttempts + "/3)    |+|+|+|");
        System.out.println("|+|+|+| 9.Statistics          |+|+|+| 10.Save Game     |+|+|+| 11.Load Game           |+|+|+| 12.Exit Game                     |+|+|+|");
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
                return openLongPosition();
            case 5:
                return openShortPosition();
            case 6:
                clearConsole();
                skipTurn();
                return false;
            case 7:
                clearConsole();
                skipDay();
                return false;
            case 8:
                clearConsole();
                predictNextMovement();
                return false;
            case 9:
                clearConsole();
                CryptoBarGraph.generateGraph("price_history.txt", 5);
                return false;
            case 10:
                clearConsole();
                saveGame();
                return false;
            case 11:
                clearConsole();
                loadGameMidGame();
                return false;
            case 12:
                exitGame();
                return false;
            default:
                System.out.println("Invalid choice! Please try again.");
                return false;
        }
    }


    public void predictNextMovement() {

        if (marketAnalystAttempts > 0) {
            int currentPrice = 75000;
            if(marketAnalystAttempts == 3) {
                currentPrice = currentPrice ;
            } else if (marketAnalystAttempts == 2) {
                currentPrice = 100000;
            } else if(marketAnalystAttempts == 1) {
                currentPrice = 150000 ;
            }
            if (player.getBalance() >= currentPrice) {
                System.out.println("WARNING: Consulting the black market analyst is a dangerous and costly activity!");
                System.out.println("His consulting cost is $" + currentPrice);
                System.out.println("If you get caught, you will be sent to jail and 20 turns will be skipped.");
                System.out.println("Do you want to proceed? (y/n)");
                Scanner scanner = new Scanner(System.in);
                String decision = scanner.next().toLowerCase();

                if (decision.equals("y")) {
                    player.deductBalance(currentPrice);
                    marketAnalystAttempts--;

                    if (gameRandom.nextDouble() < 0.1) {
                        System.out.println("Oh no! You have been caught by the authorities!");
                        System.out.println("You will be in jail for 20 turns. The market will continue to move during this time.");
                        System.out.println("Or You Can Bribe the officers to stay out of jail by paying : $"+currentPrice*2);
                        System.out.println(" Press 0 to bribe , Press 1 to go to jail");

                        int decision2StayOut = scanner.nextInt();
                        if (decision2StayOut == 1) {
                            for (int i = 0; i < 20; i++) {
                                completeTurn();
                            }
                        } else if(decision2StayOut == 0) {
                            player.deductBalance(currentPrice*2);
                        }
                        System.out.println("You are now out of jail. Be careful next time!");
                    } else {
                        Map<Coin, Integer> predictions = market.predictNextMovements();
                        System.out.println("Analyst's Report:");
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
                    }
                } else {
                    System.out.println("You decided not to consult the market analyst. Wise choice!");
                }
            } else {
                System.out.println("Not enough balance ($" + currentPrice + " required)");
            }
        } else {
            System.out.println("You have exceeded the maximum number of attempts to consult the market analyst.");
        }
    }




    public void displayPositions() {
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

    public boolean openLongPosition() {
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

    private void completeTurn() {
        turnsRemaining--;
        market.simulateMarketMovement();
        updatePositions();
        savePriceHistory(); // Ensure this is called to log price history
        saveGameState();
    }

    public void skipTurn() {
        completeTurn();
        System.out.println("Turn skipped. Market will update and affect your current positions.");
    }

    public void skipDay() {
        if (turnsRemaining%16 == 0) {
            for (int  i = 0; i < 16; i++) {
                completeTurn();
            }
        } else {
          int turnsToSkip = turnsRemaining%16;
            for (int  i = 0; i < turnsToSkip; i++) {
                completeTurn();
            }
        }
        System.out.println("Day skipped. Market will update and affect your current positions.");
    }

    public void updatePositions() {
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

    public void randomPriceAlertEvent() {
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

    public void randomVolatilityEvent() {
        if (gameRandom.nextDouble() < 0.05 && turnsRemaining != 0) {
            market.incrementVolatility();
            System.out.println("MARKET ALERT: Due to recent developments, expect more volatility when trading.");
        }
    }

    public void displayFinalResults() {
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
            writer.write("Goal Achieved: " + (finalBalance >= 3000000 ? "Yes" : "No") + "\n");
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

    public void clearConsole(){
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

    public void saveGame() {
        if (player != null) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
                out.writeDouble(player.getBalance());
                out.writeObject(positions.getPositions());
                out.writeInt(getTurnsRemaining());
                out.writeObject(market.getCoins());
                out.writeInt(marketAnalystAttempts);
                System.out.println("Game saved successfully!");
            } catch (IOException e) {
                System.out.println("Error saving the game: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No player data to save.");
        }
    }

    public void loadGameMidGame() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            double balance = in.readDouble();
            @SuppressWarnings("unchecked")
            List<Trade> positionsLoaded = (List<Trade>) in.readObject();
            int loadedTurns = in.readInt();
            List<Coin> coinsLoaded = (List<Coin>) in.readObject();
            player = new Player(balance);
            positions.setPositions(positionsLoaded);
            setTurnsRemaining(loadedTurns);
            market.setCoins(coinsLoaded);
            marketAnalystAttempts=in.readInt();
            System.out.println("Game loaded successfully!");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading the game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadGameStartMenu() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            double balance = in.readDouble();
            @SuppressWarnings("unchecked")
            List<Trade> positionsLoaded = (List<Trade>) in.readObject();
            int savedTurns = in.readInt();
            List<Coin> coinsLoaded = (List<Coin>) in.readObject();
            player = new Player(balance);
            positions.setPositions(positionsLoaded);
            setTurnsRemaining(savedTurns);
            market.setCoins(coinsLoaded);
            marketAnalystAttempts=in.readInt();
            System.out.println("Game loaded successfully!");
            startGame();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading the game: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    public void setTurnsRemaining(int turnsRemaining) {
        this.turnsRemaining = turnsRemaining;
    }

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public Player getPlayer() {
        return player;
    }
}
