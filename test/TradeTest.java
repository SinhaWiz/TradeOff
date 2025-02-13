package test;

import src.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TradeTest {
    @Test
    public void longProfitTest() {
        Coin coin = new Coin("Bitcoin", "BTC", 50000);
        Trade trade = new LongTrade(coin, 1, coin.getPrice(), 1);
        coin.updatePrice(51000);
        int expectedGainLoss = 1000;
        assertEquals(expectedGainLoss, trade.calcGainLoss());
    }

    @Test
    public void longLossTest() {
        Coin coin = new Coin("Bitcoin", "BTC", 50000);
        Trade trade = new LongTrade(coin, 1, coin.getPrice(), 1);
        coin.updatePrice(49000);
        int expectedGainLoss = -1000;
        assertEquals(expectedGainLoss, trade.calcGainLoss());
    }

    @Test
    public void shortProfitTest() {
        Coin coin = new Coin("Bitcoin", "BTC", 50000);
        Trade trade = new ShortTrade(coin, 1, coin.getPrice(), 1);
        coin.updatePrice(49000);
        int expectedGainLoss = 1000;
        assertEquals(expectedGainLoss, trade.calcGainLoss());
    }

    @Test
    public void shortLossTest() {
        Coin coin = new Coin("Bitcoin", "BTC", 50000);
        Trade trade = new ShortTrade(coin, 1, coin.getPrice(), 1);
        coin.updatePrice(51000);
        int expectedGainLoss = -1000;
        assertEquals(expectedGainLoss, trade.calcGainLoss());
    }
}
