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

}