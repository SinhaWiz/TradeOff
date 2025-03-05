package test;

import org.junit.jupiter.api.Test;
import src.Market;

import static org.junit.jupiter.api.Assertions.*;

public class TestIncrementVolatility {

    @Test
    public void testIncrementVolatility() {
        Market market = new Market();
        double initialVolatility = Market.getSIGMA();
        market.incrementVolatility();
        double newVolatility = Market.getSIGMA();
        assertEquals(initialVolatility + 0.05, newVolatility, "Volatility should increase by 0.05");
    }
}