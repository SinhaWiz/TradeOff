package src;

import java.io.*;
import java.util.*;

public class SaveManager {
    private static final String SAVE_FILE = "game_save.txt";

    public void saveGameState(GameState state) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(state);
            System.out.println("Game saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving game state: " + e.getMessage());
        }
    }

    public GameState loadGameState() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            GameState state = (GameState) ois.readObject();
            System.out.println("Game loaded successfully!");
            return state;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading game state: " + e.getMessage());
            return null;
        }
    }
}