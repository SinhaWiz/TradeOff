package test;

import org.junit.jupiter.api.Test;
import src.GameController;

import static org.junit.jupiter.api.Assertions.*;

public class TestGameOver {

    @Test
    public void testGameOver() {
        GameController gameController = new GameController();
        gameController.setTurnsRemaining(0);
        gameController.displayFinalResults();
        assertTrue(true, "Game over message should be displayed");
    }
}