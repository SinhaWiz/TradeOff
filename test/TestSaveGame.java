package test;

import org.junit.jupiter.api.Test;
import src.GameController;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class TestSaveGame {

    @Test
    public void testSaveGame() {
        GameController gameController = new GameController();
        gameController.saveGame();
        File saveFile = new File("game_save.dat");
        assertTrue(saveFile.exists(), "Save file should exist after saving the game");
    }
}