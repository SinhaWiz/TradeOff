package src;

import java.util.ArrayList;
import java.util.List;

public class PositionManager {
    private List<Trade> positions;

    public PositionManager() {
        this.positions = new ArrayList<>();
    }

    public void openLongPosition(Coin coin, double quantity, double price, int leverage) {
        positions.add(new LongTrade(coin, quantity, price, leverage));
    }

    public void openShortPosition(Coin coin, double quantity, double price, int leverage) {
        positions.add(new ShortTrade(coin, quantity, price, leverage));
    }

    public void closePosition(int index) {
        if (index >= 0 && index < positions.size()) {
            positions.remove(index);
        }
    }

    public void setPositions(List<Trade> positions) {
        this.positions = positions;
    }

    public List<Trade> getPositions() {
        return positions;
    }
}