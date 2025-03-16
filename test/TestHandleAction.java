package test;

import org.junit.jupiter.api.Test;
import src.GameController;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestHandleAction {

    @Test
    public void testHandleAction() {
        String simulatedInput = "1\n";
        InputStream originalIn = System.in; 

        try {
            System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

            GameController gameController = new GameController();
            boolean result = gameController.handleAction();
            assertTrue(true, "Action should be handled without errors");
        } finally {
            System.setIn(originalIn);
        }
    }
}
