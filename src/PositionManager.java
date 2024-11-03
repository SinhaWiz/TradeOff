package src;

import java.util.ArrayList;
import java.util.List;

public class PositionManager {
    int index;
    private List<Trade> positions;

    public PositionManager() {
        this.positions = new ArrayList<>();
    }

    public void openLongPosition(Coin coin, double quantity, double price) {
//        positions.add(new LongTrade(coin, quantity, price));
    }

    public void openShortPosition(Coin coin, double quantity, double price) {
        positions.add(new ShortTrade(coin, quantity, price));
    }

    public void closePosition() {
        if (index >= 0 && index < positions.size()) {
            positions.remove(index);
        }
    }

    public List<Trade> viewCurrentPositions() {
        return new ArrayList<>(positions);
    }

    public void openShortPosition() {
    }
}
