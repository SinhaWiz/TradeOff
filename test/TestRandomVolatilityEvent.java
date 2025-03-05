package test;

import org.junit.jupiter.api.Test;
import src.GameController;

import static org.junit.jupiter.api.Assertions.*;

public class TestRandomVolatilityEvent {

    @Test
    public void testRandomVolatilityEvent() {
        GameController gameController = new GameController();
        gameController.randomVolatilityEvent();
        assertTrue(true, "Random volatility event should run without errors");
    }
}