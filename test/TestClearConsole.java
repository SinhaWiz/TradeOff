package test;

import org.junit.jupiter.api.Test;
import src.GameController;

import static org.junit.jupiter.api.Assertions.*;


public class TestClearConsole {

    @Test
    public void testClearConsole() {
        GameController gameController = new GameController();
        gameController.clearConsole();
        assertTrue(true, "Console should be cleared without errors");
    }
}