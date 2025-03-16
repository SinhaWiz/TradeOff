package test;

import org.junit.jupiter.api.Test;
import src.Market;

import static org.junit.jupiter.api.Assertions.*;

public class TestMarketInitialization {

    @Test
    public void testMarketInitialization() {
        Market market = new Market();
        assertNotNull(market.getCoins(), "Coins list should not be null");
        assertFalse(market.getCoins().isEmpty(), "Coins list should not be empty");
    }
}