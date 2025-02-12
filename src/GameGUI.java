package src;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import javax.swing.border.EmptyBorder;

public class GameGUI extends JFrame {
    private Player player;
    private Market market;
    private PositionManager positions;
    private SaveManager saveManager;
    private int turnsRemaining;
    private static final int MAX_TURNS = 160;

    // GUI Components
    private JLabel balanceLabel;
    private JLabel turnsLabel;
    private JTable marketTable;
    private JTable positionsTable;
    private DefaultTableModel marketTableModel;
    private DefaultTableModel positionsTableModel;
    private JButton viewMarketBtn;
    private JButton viewPortfolioBtn;
    private JButton openLongBtn;
    private JButton openShortBtn;
    private JButton closePositionBtn;
    private JButton skipTurnBtn;
    private JButton skipDayBtn;
    private JPanel mainPanel;

    public GameGUI() {

        this.player = new Player(1000000);
        this.market = new Market();
        this.positions = new PositionManager();
        this.saveManager = new SaveManager();
        this.turnsRemaining = MAX_TURNS;


        setTitle("TradeOff - Crypto Trading Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);


        initializeComponents();
        setupLayout();
        updateDisplay();

        showWelcomeMessage();
    }

    private void initializeComponents() {
        // Initialize labels
        balanceLabel = new JLabel("Balance: $1,000,000.00");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        turnsLabel = new JLabel("Turns Remaining: " + turnsRemaining);
        turnsLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Initialize tables
        initializeMarketTable();
        initializePositionsTable();

        // Initialize buttons
        viewMarketBtn = createStyledButton("View Market");
        viewPortfolioBtn = createStyledButton("View Portfolio");
        openLongBtn = createStyledButton("Open Long Position");
        openShortBtn = createStyledButton("Open Short Position");
        closePositionBtn = createStyledButton("Close Position");
        skipTurnBtn = createStyledButton("Skip Turn");
        skipDayBtn = createStyledButton("Skip Day");

        // Add button listeners
        addButtonListeners();
    }
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(51, 153, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }
    private void initializeMarketTable() {
        String[] marketColumns = {"Coin", "Ticker", "Price"};
        marketTableModel = new DefaultTableModel(marketColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        marketTable = new JTable(marketTableModel);
        marketTable.setFont(new Font("Arial", Font.PLAIN, 14));
        marketTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
    }

    private void initializePositionsTable() {
        String[] positionColumns = {"Type", "Coin", "Quantity", "Entry Price", "Current P/L", "Leverage"};
        positionsTableModel = new DefaultTableModel(positionColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        positionsTable = new JTable(positionsTableModel);
        positionsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        positionsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
    }
    private void setupLayout() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top panel for status information
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        topPanel.add(balanceLabel);
        topPanel.add(turnsLabel);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel for tables
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centerPanel.add(new JScrollPane(marketTable));
        centerPanel.add(new JScrollPane(positionsTable));
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        buttonPanel.add(viewMarketBtn);
        buttonPanel.add(viewPortfolioBtn);
        buttonPanel.add(openLongBtn);
        buttonPanel.add(openShortBtn);
        buttonPanel.add(closePositionBtn);
        buttonPanel.add(skipTurnBtn);
        buttonPanel.add(skipDayBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }
    private void addButtonListeners() {
        viewMarketBtn.addActionListener(e -> updateMarketDisplay());
        viewPortfolioBtn.addActionListener(e -> updatePositionsDisplay());
        openLongBtn.addActionListener(e -> openLongPosition());
        openShortBtn.addActionListener(e -> openShortPosition());
        closePositionBtn.addActionListener(e -> closePosition());
        skipTurnBtn.addActionListener(e -> skipTurn());
        skipDayBtn.addActionListener(e -> skipDay());
    }
    private void updateDisplay() {
        updateMarketDisplay();
        updatePositionsDisplay();
        balanceLabel.setText(String.format("Balance: $%.2f", player.getBalance()));
        turnsLabel.setText("Turns Remaining: " + turnsRemaining);
    }

    private void updateMarketDisplay() {
        marketTableModel.setRowCount(0);
        for (Coin coin : market.getCoins()) {
            marketTableModel.addRow(new Object[]{
                    coin.getName(),
                    coin.getTicker(),
                    String.format("$%.2f", coin.getPrice())
            });
        }
    }

    private void updatePositionsDisplay() {
        positionsTableModel.setRowCount(0);
        for (Trade trade : positions.getPositions()) {
            positionsTableModel.addRow(new Object[]{
                    trade instanceof LongTrade ? "Long" : "Short",
                    trade.getCoin().getTicker(),
                    String.format("%.4f", trade.getQuantity()),
                    String.format("$%.2f", trade.getEntryPrice()),
                    String.format("$%.2f", trade.calcGainLoss()),
                    trade.getLeverage() + "x"
            });
        }
    }

    private void openLongPosition() {
        String ticker = showInputDialog("Enter coin ticker:");
        if (ticker == null) return;

        Optional<Coin> selectedCoin = market.getCoins().stream()
                .filter(c -> c.getTicker().equalsIgnoreCase(ticker))
                .findFirst();

        if (selectedCoin.isPresent()) {
            int leverage = showLeverageDialog();
            if (leverage == -1) return;

            String quantityStr = showInputDialog("Enter quantity:");
            if (quantityStr == null) return;

            try {
                double quantity = Double.parseDouble(quantityStr);
                Coin coin = selectedCoin.get();
                double tradingAmount = coin.getPrice() * quantity;

                if (tradingAmount <= player.getBalance()) {
                    positions.openLongPosition(coin, quantity, coin.getPrice(), leverage);
                    player.updateBalance(-tradingAmount);
                    player.addToPortfolio(coin, quantity);
                    showMessage("Long position opened successfully!");
                    updateDisplay();
                } else {
                    showError("Insufficient funds!");
                }
            } catch (NumberFormatException e) {
                showError("Invalid quantity!");
            }
        } else {
            showError("Invalid coin ticker!");
        }
    }

    private void openShortPosition() {
        String ticker = showInputDialog("Enter coin ticker:");
        if (ticker == null) return;

        Optional<Coin> selectedCoin = market.getCoins().stream()
                .filter(c -> c.getTicker().equalsIgnoreCase(ticker))
                .findFirst();

        if (selectedCoin.isPresent()) {
            int leverage = showLeverageDialog();
            if (leverage == -1) return;

            String quantityStr = showInputDialog("Enter quantity:");
            if (quantityStr == null) return;

            try {
                double quantity = Double.parseDouble(quantityStr);
                Coin coin = selectedCoin.get();
                double collateral = coin.getPrice() * quantity * 0.5 * leverage;

                if (collateral <= player.getBalance()) {
                    positions.openShortPosition(coin, quantity, coin.getPrice(), leverage);
                    player.updateBalance(-collateral);
                    showMessage("Short position opened successfully!");
                    updateDisplay();
                } else {
                    showError("Insufficient collateral!");
                }
            } catch (NumberFormatException e) {
                showError("Invalid quantity!");
            }
        } else {
            showError("Invalid coin ticker!");
        }
    }

    private void closePosition() {
        if (positions.getPositions().isEmpty()) {
            showError("No positions to close!");
            return;
        }

        int selectedRow = positionsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a position to close!");
            return;
        }

        Trade trade = positions.getPositions().get(selectedRow);
        double closingAmount = trade.calcGainLoss() + (trade.getEntryPrice() * trade.getQuantity());
        player.updateBalance(closingAmount);
        positions.closePosition(selectedRow);
        showMessage(String.format("Position closed. Profit/Loss: $%.2f", trade.calcGainLoss()));
        updateDisplay();
    }


}