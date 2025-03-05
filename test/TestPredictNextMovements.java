package test;

import org.junit.jupiter.api.Test;
import src.Coin;
import src.Market;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestPredictNextMovements {

    @Test
    public void testPredictNextMovements() {
        Market market = new Market();
        Map<Coin, Integer> predictions = market.predictNextMovements();
        assertNotNull(predictions, "Predictions map should not be null");
        assertFalse(predictions.isEmpty(), "Predictions map should not be empty");
    }
}