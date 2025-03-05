package test;
import org.junit.jupiter.api.Test;
import src.GameController;
import static org.junit.jupiter.api.Assertions.*;
public class TestSkipDay {
        @Test
        public void testSkipDay() {
            GameController gameController = new GameController();
            int initialTurns = gameController.getTurnsRemaining();
            gameController.skipDay();
            assertTrue(gameController.getTurnsRemaining() < initialTurns, "Turns should decrease after skipping a day");
        }
    }
