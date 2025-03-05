package test;

import org.junit.jupiter.api.Test;
import src.GameController;

import static org.junit.jupiter.api.Assertions.*;

public class TestDisplayWelcomeMessage {

    @Test
    public void testDisplayWelcomeMessage() {
        GameController gameController = new GameController();
        gameController.displayWelcomeMessage();
        assertTrue(true, "Welcome message should be displayed without errors");
    }
}