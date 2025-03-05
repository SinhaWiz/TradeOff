package test;

import org.junit.jupiter.api.Test;
import src.GameController;

import static org.junit.jupiter.api.Assertions.*;

public class TestRandomPriceAlertEvent {

    @Test
    public void testRandomPriceAlertEvent() {
        GameController gameController = new GameController();
        gameController.randomPriceAlertEvent();
        assertTrue(true, "Random price alert event should run without errors");
    }
}