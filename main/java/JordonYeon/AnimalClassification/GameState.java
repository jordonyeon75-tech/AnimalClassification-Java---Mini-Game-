package JordonYeon.AnimalClassification;

import javafx.application.Platform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState {
    private static GameState instance;
    private int score = 0;
    private int level = 1;
    private List<Animal> animals = new ArrayList<>();
    private Map<Animal, Boolean> placedAnimals = new HashMap<>();
    private AnimalClassificationGame game;

    private GameState() {}

    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    public void setGameInstance(AnimalClassificationGame game) {
        this.game = game;
    }

    public AnimalClassificationGame getGameInstance() {
        return game;
    }

    public void addAnimal(Animal animal) {
        animals.add(animal);
        placedAnimals.put(animal, false);
    }

    public void setPlaced(Animal animal, boolean placed) {
        placedAnimals.put(animal, placed);
    }

    public boolean isPlaced(Animal animal) {
        return placedAnimals.getOrDefault(animal, false);
    }

    public void increaseScore() {
        score++;
        System.out.println("Score increased to: " + score + ", Level: " + level + ", Total animals in level: " + animals.size());
        if (score >= animals.size()) {
            System.out.println("All animals placed, scheduling level-up dialog... (Score: " + score + ")");
            if (game == null) {
                System.err.println("Error: Game instance is null in GameState. Cannot show level-up dialog.");
            } else {
                Platform.runLater(() -> {
                    System.out.println("Executing showLevelUpDialog on JavaFX thread...");
                    game.showLevelUpDialog();
                });
            }
        }
    }

    public int getScore() {
        return score;
    }

    public void resetScore() {
        score = 0;
        System.out.println("Score reset to: " + score);
    }

    public void resetAnimals() {
        animals.clear();
        placedAnimals.clear();
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}