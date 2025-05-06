package src;

import java.io.Serializable;

public class TradeRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type; // "LONG" or "SHORT"
    private String coinTicker;
    private double quantity;
    private double entryPrice;

    public TradeRecord(String type, String ticker, double qty, double price) {
        this.type = type;
        this.coinTicker = ticker;
        this.quantity = qty;
        this.entryPrice = price;
    }
    // Getters
    public String getType() {
        return type;
    }
    public String getCoinTicker() {
        return coinTicker;
    }
    public double getQuantity() {
        return quantity;
    }
    public double getEntryPrice() {
        return entryPrice;
    }
}