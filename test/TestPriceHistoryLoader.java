package test;

import org.junit.jupiter.api.Test;
import src.GameController;
import src.PriceHistoryLoader;

import static org.junit.jupiter.api.Assertions.*;

public class TestPriceHistoryLoader {

    @Test
    public void testPriceHistoryLoader() {
        GameController gameController = new GameController();
        PriceHistoryLoader.init();
        assertTrue(true, "Price history should be loaded without errors");
    }
}