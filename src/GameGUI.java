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

}