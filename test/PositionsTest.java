package test;

import org.junit.jupiter.api.Test;
import src.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PositionsTest {
    @Test
    public void positionsInitTest() {
        PositionManager positionManager = new PositionManager();
        Market market = new Market();
        List<Coin> coins = market.getCoins();
        Coin coin = coins.get(0);
        positionManager.openLongPosition(coin, 1, coin.getPrice(), 1);
        List<Trade> positions = positionManager.getPositions();
        assertFalse(positions.isEmpty());
    }

    @Test
    public void positionsCloseTest() {
        PositionManager positionManager = new PositionManager();
        Market market = new Market();
        List<Coin> coins = market.getCoins();
        Coin coin = coins.get(0);
        positionManager.openLongPosition(coin, 1, coin.getPrice(), 1);
        positionManager.closePosition(0);
        List<Trade> positions = positionManager.getPositions();
        assertTrue(positions.isEmpty());
    }
}
