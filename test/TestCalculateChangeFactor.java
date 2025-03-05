package test;

import org.junit.jupiter.api.Test;
import src.Coin;
import src.Market;

import static org.junit.jupiter.api.Assertions.*;

public class TestCalculateChangeFactor {

    @Test
    public void testCalculateChangeFactor() {
        Market market = new Market();
        Coin coin = market.getCoins().getFirst();
        double changeFactor = market.calculateChangeFactor(coin);
        assertTrue(changeFactor > 0, "Change factor should be positive");
    }
}