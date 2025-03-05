package test;

import org.junit.jupiter.api.Test;
import src.GameController;

import static org.junit.jupiter.api.Assertions.*;

public class TestPlayerInitialization {

    @Test
    public void testPlayerInitialBalance() {
        GameController gameController = new GameController();
        assertEquals(1000000, gameController.getPlayer().getBalance(), "Player should start with $1,000,000");
    }
}
