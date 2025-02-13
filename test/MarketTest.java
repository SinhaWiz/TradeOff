package test;
import src.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
public class MarketTest {
    @Test
    public void marketInitTest() {
        Market market = new Market();
        List<Coin> coins = market.getCoins();
        boolean coinsEmpty = coins.isEmpty();
        assertFalse(coinsEmpty);
    }
    @Test
    public void marketSimulationTest() {
        Market market = new Market();
        List<Coin> coins = market.getCoins();
        double initialPrice = coins.get(0).getPrice();
        market.simulateMarketMovement();
        double finalPrice = coins.get(0).getPrice();
        assertNotEquals(initialPrice, finalPrice);
    }
}
