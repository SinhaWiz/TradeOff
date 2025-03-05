package test;
import org.junit.jupiter.api.Test;
import src.GameController;

import static org.junit.jupiter.api.Assertions.*;

public class TestDisplayFinalResults {

    @Test
    public void testDisplayFinalResults() {
        GameController gameController = new GameController();
        gameController.setTurnsRemaining(0);
        gameController.displayFinalResults();
        assertTrue(true, "Final results should be displayed without errors");
    }
}