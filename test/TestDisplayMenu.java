package test;

import org.junit.jupiter.api.Test;
import src.GameController;

import static org.junit.jupiter.api.Assertions.*;
public class TestDisplayMenu {

    @Test
    public void testDisplayMenu() {
        GameController gameController = new GameController();
        gameController.displayMenu();
        assertTrue(true, "Menu should be displayed without errors");
    }
}