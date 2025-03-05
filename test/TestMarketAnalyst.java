package test;

import org.junit.jupiter.api.Test;
import src.GameController;

import static org.junit.jupiter.api.Assertions.*;

public class TestMarketAnalyst {

    @Test
    public void testMarketAnalyst() {
        GameController gameController = new GameController();
        gameController.predictNextMovement();
        assertTrue(true, "Market analyst prediction should run without errors");
    }
}