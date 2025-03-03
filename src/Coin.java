package src;

import java.io.Serializable;
import java.util.Objects;

public class Coin implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String ticker;
    private double price;
    private boolean possiblePositiveTrend;
    private boolean possibleNegativeTrend;

    public Coin(String name, String ticker, double initialPrice) {
        this.name = name;
        this.ticker = ticker;
        this.price = initialPrice;
    }

    public double getPrice() {
        return price;
    }

    public void updatePrice(double newPrice) {
        this.price = newPrice;
    }

    public String getName() {
        return name;
    }

    public String getTicker() {
        return ticker;
    }

    public boolean isPossiblePositiveTrend() {
        return possiblePositiveTrend;
    }

    public void setPossiblePositiveTrend(boolean possiblePositiveTrend) {
        this.possiblePositiveTrend = possiblePositiveTrend;
    }

    public boolean isPossibleNegativeTrend() {
        return possibleNegativeTrend;
    }

    public void setPossibleNegativeTrend(boolean possibleNegativeTrend) {
        this.possibleNegativeTrend = possibleNegativeTrend;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coin coin = (Coin) o;
        return ticker.equals(coin.ticker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker);
    }
}

