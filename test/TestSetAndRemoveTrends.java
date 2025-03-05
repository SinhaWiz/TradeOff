package test;

import org.junit.jupiter.api.Test;
import src.Coin;
import src.Market;

import static org.junit.jupiter.api.Assertions.*;

public class TestSetAndRemoveTrends {

    @Test
    public void testSetAndRemoveTrends() {
        Market market = new Market();
        market.simulateMarketMovement();
        for (Coin coin : market.getCoins()) {
            assertFalse(coin.isPossiblePositiveTrend() && coin.isPossibleNegativeTrend(),
                    "Coin should not have both positive and negative trends simultaneously");
        }
    }
}
