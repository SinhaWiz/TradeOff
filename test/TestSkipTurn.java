package test;
import org.junit.jupiter.api.Test;
import src.GameController;
import static org.junit.jupiter.api.Assertions.*;
public class TestSkipTurn {
    @Test
    public void testSkipTurn() {
        GameController gameController = new GameController();
        int initialTurns = gameController.getTurnsRemaining();
        gameController.skipTurn();
        assertEquals(initialTurns - 1, gameController.getTurnsRemaining(), "Turns should decrease by 1 after skipping a turn");
    }

}
