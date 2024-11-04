package src;

import java.util.Map;

public class PriceData {
    private String timestamp;
    private Map<String, Double> prices;

    public PriceData(String timestamp, Map<String, Double> prices) {
        this.timestamp = timestamp;
        this.prices = prices;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Map<String, Double> getPrices() {
        return prices;
    }
}