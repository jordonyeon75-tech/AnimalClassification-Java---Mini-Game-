package JordonYeon.AnimalClassification;

import javafx.animation.*;
import javafx.animation.KeyFrame;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.sound.sampled.*; // for audio playback

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnimalClassificationGame extends Application {
    private static GameState gameState = GameState.getInstance();
    private List<Animal> animals;
    private List<CategoryBox> categoryBoxes;
    private Label feedbackLabel;
    private Text levelLabel;
    private Label timerLabel;
    private GraphicsContext gc;
    private Pane gamePane;
    private static final double SCENE_WIDTH = 1600;
    private static final double SCENE_HEIGHT = 1200;
    private Animal draggedAnimal;
    private ImageView draggedImageView;
    private Timeline timer;
    private int timeLeft = 0;
    private double volume = 50.0;
    private boolean isMuted = false;
    private Stage primaryStage;
    private VBox root;
    private Clip audioClip;
    private FloatControl gainControl;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.root = new VBox(10);
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT, Color.LIGHTPINK);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Animal Classification Challenge");
        primaryStage.setMaximized(true);

        // Initialize music using javax.sound.sampled
        try {
            // Load the WAV file from resources
            String musicPath = getClass().getResource("/JordonYeon/AnimalClassification/image/LittlerootTown.wav").getPath();
            File audioFile = new File(musicPath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);
            audioClip.loop(Clip.LOOP_CONTINUOUSLY); // Loop indefinitely

            // Set up volume control
            gainControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
            setVolume(volume); // Set initial volume (50%)
            if (isMuted) {
                setMute(true);
            } else {
                audioClip.start();
            }
            System.out.println("Music started: LittlerootTown.wav");
        } catch (Exception e) {
            System.err.println("Error loading music: " + e.getMessage());
        }

        showWelcomePage(root, primaryStage);

        // Stop music when closing the application
        primaryStage.setOnCloseRequest(event -> {
            if (audioClip != null) {
                audioClip.stop();
                audioClip.close();
                System.out.println("Music stopped on application close");
            }
        });

        primaryStage.show();
    }

    private void setVolume(double volume) {
        if (gainControl != null) {
            // Map volume (0-100) to gain (-30.0 dB to 0.0 dB)
            float minGain = gainControl.getMinimum(); // Typically -80.0 dB
            float maxGain = 0.0f; // 0.0 dB is max (no reduction)
            float gainRange = maxGain - minGain;
            float gain = minGain + (float) (volume / 100.0) * gainRange;
            // Limit gain to avoid distortion (cap at 0.0 dB)
            gain = Math.min(gain, maxGain);
            gainControl.setValue(gain);
            System.out.println("Volume set to: " + volume + " (Gain: " + gain + " dB)");
        }
    }

    private void setMute(boolean mute) {
        if (audioClip != null) {
            if (mute) {
                gainControl.setValue(gainControl.getMinimum()); // Mute by setting gain to minimum
            } else {
                setVolume(volume); // Restore volume
            }
            System.out.println("Music mute: " + mute);
        }
    }

    private void showWelcomePage(VBox root, Stage stage) {
        if (root == null) {
            System.err.println("Error: root VBox is null in showWelcomePage");
            return;
        }

        root.getChildren().clear();

        Pane welcomePane = new Pane();
        welcomePane.setPrefSize(SCENE_WIDTH, SCENE_HEIGHT);
        welcomePane.setStyle("-fx-background-color: LIGHTYELLOW;");

        Canvas canvas = new Canvas(SCENE_WIDTH, SCENE_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        drawBackgroundPattern(gc);
        drawDecorativeShapes(gc);

        Text title = new Text("Animals Classification Challenge 🐾🐘🐔🐸");
        title.setStyle("-fx-font-size: 40px; -fx-font-family: 'Comic Sans MS'; -fx-font-weight: bold;");
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.RED), new Stop(1, Color.BLUE));
        title.setFill(gradient);
        title.setStroke(Color.BLACK);
        title.setStrokeWidth(2);
        title.setX((SCENE_WIDTH - title.getBoundsInLocal().getWidth()) / 3.5);
        title.setY(SCENE_HEIGHT / 3);

        Timeline titleAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0.5), new KeyValue(title.scaleXProperty(), 1.1)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(title.scaleYProperty(), 1.1)),
                new KeyFrame(Duration.seconds(1), new KeyValue(title.scaleXProperty(), 1.0)),
                new KeyFrame(Duration.seconds(1), new KeyValue(title.scaleYProperty(), 1.0))
        );
        titleAnimation.setCycleCount(Timeline.INDEFINITE);
        titleAnimation.play();

        drawRainbowArc(gc, SCENE_WIDTH / 2, SCENE_HEIGHT / 3 - 50, 300, 100);

        Button startButton = new Button("Start");
        startButton.setStyle("-fx-font-size: 28px; -fx-font-family: 'Comic Sans MS'; -fx-background-color: LIME; " +
                "-fx-text-fill: black; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 2, 2);");
        startButton.setPrefWidth(250);
        startButton.setPrefHeight(80);
        startButton.setLayoutX((SCENE_WIDTH - 250) / 2);
        startButton.setLayoutY(SCENE_HEIGHT / 2.5);
        startButton.setOnAction(e -> showGameScreen(root, stage));
        startButton.setOnMouseEntered(e -> startButton.setStyle("-fx-font-size: 28px; -fx-font-family: 'Comic Sans MS'; " +
                "-fx-background-color: YELLOW; -fx-text-fill: black; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 2, 2);"));
        startButton.setOnMouseExited(e -> startButton.setStyle("-fx-font-size: 28px; -fx-font-family: 'Comic Sans MS'; " +
                "-fx-background-color: LIME; -fx-text-fill: black; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 2, 2);"));

        Button settingsButton = new Button("Settings");
        settingsButton.setStyle("-fx-font-size: 28px; -fx-font-family: 'Comic Sans MS'; -fx-background-color: ORANGE; " +
                "-fx-text-fill: black; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 2, 2);");
        settingsButton.setPrefWidth(250);
        settingsButton.setPrefHeight(80);
        settingsButton.setLayoutX((SCENE_WIDTH - 250) / 2);
        settingsButton.setLayoutY(SCENE_HEIGHT / 2);
        settingsButton.setOnAction(e -> showSettingsDialog());
        settingsButton.setOnMouseEntered(e -> settingsButton.setStyle("-fx-font-size: 28px; -fx-font-family: 'Comic Sans MS'; " +
                "-fx-background-color: PINK; -fx-text-fill: black; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 2, 2);"));
        settingsButton.setOnMouseExited(e -> settingsButton.setStyle("-fx-font-size: 28px; -fx-font-family: 'Comic Sans MS'; " +
                "-fx-background-color: ORANGE; -fx-text-fill: black; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 2, 2);"));

        welcomePane.getChildren().addAll(canvas, title, startButton, settingsButton);
        root.getChildren().add(welcomePane);
    }

    private void drawBackgroundPattern(GraphicsContext gc) {
        gc.setFill(Color.LIGHTGRAY.deriveColor(0, 1, 1, 0.3));
        for (int x = 0; x < SCENE_WIDTH; x += 100) {
            for (int y = 0; y < SCENE_HEIGHT; y += 100) {
                drawPawPrint(gc, x, y, 20);
            }
        }
    }

    private void drawPawPrint(GraphicsContext gc, double x, double y, double size) {
        gc.fillOval(x, y, size, size);
        double toeSize = size / 3;
        gc.fillOval(x + size / 2 - toeSize / 2, y - toeSize, toeSize, toeSize);
        gc.fillOval(x, y - toeSize / 2, toeSize, toeSize);
        gc.fillOval(x + size - toeSize, y - toeSize / 2, toeSize, toeSize);
        gc.fillOval(x + size / 2 - toeSize / 2, y + size - toeSize, toeSize, toeSize);
    }

    private void drawDecorativeShapes(GraphicsContext gc) {
        gc.setFill(Color.RED);
        drawStar(gc, SCENE_WIDTH / 4, SCENE_HEIGHT / 4, 20);
        gc.setFill(Color.BLUE);
        drawStar(gc, 3 * SCENE_WIDTH / 4, SCENE_HEIGHT / 4, 20);
        gc.setFill(Color.YELLOW);
        drawStar(gc, SCENE_WIDTH / 2 - 150, SCENE_HEIGHT / 5, 15);
        gc.setFill(Color.GREEN);
        drawStar(gc, SCENE_WIDTH / 2 + 150, SCENE_HEIGHT / 5, 15);

        gc.setFill(Color.PURPLE);
        gc.fillOval(50, SCENE_HEIGHT - 100, 50, 50);
        gc.setFill(Color.PINK);
        gc.fillOval(SCENE_WIDTH - 100, SCENE_HEIGHT - 100, 50, 50);
    }

    private void drawStar(GraphicsContext gc, double centerX, double centerY, double size) {
        double[] xPoints = new double[10];
        double[] yPoints = new double[10];
        for (int i = 0; i < 10; i++) {
            double angle = Math.PI / 2 + (i * 2 * Math.PI / 10);
            double radius = (i % 2 == 0) ? size : size / 2;
            xPoints[i] = centerX + radius * Math.cos(angle);
            yPoints[i] = centerY - radius * Math.sin(angle);
        }
        gc.fillPolygon(xPoints, yPoints, 10);
    }

    private void drawRainbowArc(GraphicsContext gc, double centerX, double centerY, double width, double height) {
        Color[] rainbowColors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE};
        double arcWidth = width / rainbowColors.length;
        for (int i = 0; i < rainbowColors.length; i++) {
            gc.setStroke(rainbowColors[i]);
            gc.setLineWidth(arcWidth);
            gc.strokeArc(centerX - width / 2 + i * arcWidth / 2, centerY - height, width - i * arcWidth, height * 2, 0, 180, javafx.scene.shape.ArcType.OPEN);
        }
    }

    private void showSettingsDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Settings");
        dialog.setHeaderText("Adjust Game Settings");
        dialog.getDialogPane().setStyle("-fx-background-color: LIGHTCYAN; -fx-font-family: 'Comic Sans MS'; -fx-border-color: PINK; -fx-border-width: 3;");

        Label decoLabel = new Label("✨ Settings for Fun! 🐼✨");
        decoLabel.setStyle("-fx-font-size: 16px; -fx-font-family: 'Comic Sans MS'; -fx-text-fill: PURPLE;");

        Label volumeLabel = new Label("Volume: 🎵");
        volumeLabel.setStyle("-fx-font-size: 20px; -fx-font-family: 'Comic Sans MS'; -fx-text-fill: DARKBLUE;");
        Slider volumeSlider = new Slider(0, 100, volume);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setMajorTickUnit(25);
        volumeSlider.setMinorTickCount(5);
        volumeSlider.setBlockIncrement(10);
        volumeSlider.setStyle("-fx-control-inner-background: LIGHTGREEN; -fx-accent: YELLOW;");
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
        	volume = newVal.doubleValue();
            setVolume(volume);
        });
        Timeline sliderAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0.5), new KeyValue(volumeSlider.opacityProperty(), 0.7)),
                new KeyFrame(Duration.seconds(1), new KeyValue(volumeSlider.opacityProperty(), 1.0))
        );
        sliderAnimation.setCycleCount(Timeline.INDEFINITE);
        sliderAnimation.play();

        CheckBox muteCheckBox = new CheckBox("Mute Music 🔇");
        muteCheckBox.setStyle("-fx-font-size: 20px; -fx-font-family: 'Comic Sans MS'; -fx-text-fill: DARKBLUE;");
        muteCheckBox.setSelected(isMuted);
        muteCheckBox.setOnAction(e -> {
        	isMuted = muteCheckBox.isSelected();
            setMute(isMuted);
        });

        VBox settingsContent = new VBox(10, decoLabel, volumeLabel, volumeSlider, muteCheckBox);
        settingsContent.setStyle("-fx-padding: 20px; -fx-background-color: LIGHTCYAN;");

        dialog.getDialogPane().setContent(settingsContent);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Button closeButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.setStyle("-fx-font-family: 'Comic Sans MS'; -fx-background-color: linear-gradient(to bottom, PINK, LIGHTPINK); " +
                "-fx-text-fill: black; -fx-background-radius: 10;");

        dialog.showAndWait();
    }

    private void showGameScreen(VBox root, Stage stage) {
        if (root == null) {
            System.err.println("Error: root VBox is null in showGameScreen");
            return;
        }

        root.getChildren().clear();

        gamePane = new Pane();
        Canvas canvas = new Canvas(SCENE_WIDTH, SCENE_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        gamePane.getChildren().add(canvas);

        gameState.setGameInstance(this);
        System.out.println("Game instance set in GameState: " + (gameState.getGameInstance() != null ? "Success" : "Failed"));

        levelLabel = new Text(10, 30, "Level: " + gameState.getLevel());
        levelLabel.setStyle("-fx-font-size: 20px; -fx-fill: black;");
        gamePane.getChildren().add(levelLabel);

        timerLabel = new Label("Time Left: 0s");
        timerLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: black;");
        timerLabel.setLayoutX(SCENE_WIDTH - 200);
        timerLabel.setLayoutY(10);
        gamePane.getChildren().add(timerLabel);

        // Add Exit button
        Button exitButton = new Button("Exit 🐾");
        exitButton.setStyle("-fx-font-size: 20px; -fx-font-family: 'Comic Sans MS'; -fx-background-color: #DDA0DD; " +
                "-fx-text-fill: black; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 2, 2);");
        exitButton.setPrefWidth(120);
        exitButton.setPrefHeight(50);
        exitButton.setLayoutX(SCENE_WIDTH - 150);
        exitButton.setLayoutY(50);
        exitButton.setOnAction(e -> showExitConfirmationDialog());
        exitButton.setOnMouseEntered(e -> exitButton.setStyle("-fx-font-size: 20px; -fx-font-family: 'Comic Sans MS'; " +
                "-fx-background-color: #E0FFFF; -fx-text-fill: black; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 2, 2);"));
        exitButton.setOnMouseExited(e -> exitButton.setStyle("-fx-font-size: 20px; -fx-font-family: 'Comic Sans MS'; " +
                "-fx-background-color: #DDA0DD; -fx-text-fill: black; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 2, 2);"));
        gamePane.getChildren().add(exitButton);
        System.out.println("Exit button added at (" + (SCENE_WIDTH - 150) + ", 50) with color #DDA0DD");

        initializeGame();

        feedbackLabel = new Label("Drag animals to their categories! Score: " + gameState.getScore());
        feedbackLabel.setStyle("-fx-font-size: 20px;");

        Button resetButton = new Button("Reset Animals");
        resetButton.setOnAction(e -> resetGame());

        VBox bottomPane = new VBox(10, feedbackLabel, resetButton);
        bottomPane.setAlignment(javafx.geometry.Pos.CENTER);
        bottomPane.setPrefWidth(SCENE_WIDTH);
        bottomPane.setStyle("-fx-padding: 20px;");
        root.getChildren().addAll(gamePane, bottomPane);

        setupDragAndDrop();

        startTimer();
        redraw();
    }

    private void showExitConfirmationDialog() {
        // Pause the timer
        if (timer != null) {
            timer.pause();
        }

        // Create confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Game? 😺");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("Your progress will be lost. Exit to the welcome page or continue playing?");

        ButtonType exitButton = new ButtonType("Exit");
        ButtonType cancelButton = new ButtonType("Cancel");
        alert.getButtonTypes().setAll(exitButton, cancelButton);

        alert.getDialogPane().setStyle("-fx-background-color: LIGHTCYAN; -fx-font-family: 'Comic Sans MS'; -fx-border-color: YELLOW; -fx-border-width: 3;");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == exitButton) {
                // Reset game state and go to welcome page
                gameState.setLevel(1);
                gameState.resetScore();
                gameState.resetAnimals();
                if (timer != null) {
                    timer.stop();
                }
                showWelcomePage(root, primaryStage);
            } else if (result.get() == cancelButton) {
                // Resume the timer
                if (timer != null) {
                    timer.play();
                }
            }
        }
    }

    private void initializeGame() {
        if (gamePane == null) {
            System.err.println("Error: gamePane is null in initializeGame");
            return;
        }

        gamePane.getChildren().clear();
        gamePane.getChildren().add(gc.getCanvas());

        gamePane.getChildren().add(levelLabel);
        gamePane.getChildren().add(timerLabel);
        levelLabel.setText("Level: " + gameState.getLevel());

        // Re-add Exit button
        Button exitButton = new Button("Exit 🐾");
        exitButton.setStyle("-fx-font-size: 20px; -fx-font-family: 'Comic Sans MS'; -fx-background-color: #DDA0DD; " +
                "-fx-text-fill: black; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 2, 2);");
        exitButton.setPrefWidth(120);
        exitButton.setPrefHeight(50);
        exitButton.setLayoutX(SCENE_WIDTH - 150);
        exitButton.setLayoutY(50);
        exitButton.setOnAction(e -> showExitConfirmationDialog());
        exitButton.setOnMouseEntered(e -> exitButton.setStyle("-fx-font-size: 20px; -fx-font-family: 'Comic Sans MS'; " +
                "-fx-background-color: #E0FFFF; -fx-text-fill: black; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 2, 2);"));
        exitButton.setOnMouseExited(e -> exitButton.setStyle("-fx-font-size: 20px; -fx-font-family: 'Comic Sans MS'; " +
                "-fx-background-color: #DDA0DD; -fx-text-fill: black; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 2, 2);"));
        gamePane.getChildren().add(exitButton);
        System.out.println("Exit button re-added in initializeGame at (" + (SCENE_WIDTH - 150) + ", 50) with color #DDA0DD");

        animals = new ArrayList<>();
        categoryBoxes = new ArrayList<>();

        int level = gameState.getLevel();
        List<String> animalTypes;
        List<Color> boxColors;

        if (level == 1) {
            animalTypes = List.of("Rabbit", "Turtle", "Bird");
            boxColors = List.of(Color.LIGHTBLUE, Color.LIGHTGREEN, Color.LIGHTYELLOW);
        } else if (level == 2) {
            animalTypes = List.of("Rabbit", "Turtle", "Bird", "Dog", "Cat");
            boxColors = List.of(Color.LIGHTBLUE, Color.LIGHTGREEN, Color.LIGHTYELLOW, Color.LIGHTGRAY, Color.PEACHPUFF);
        } else if (level == 3) {
            animalTypes = List.of("Lion", "Monkey", "Cow", "Ox", "Lamb", "Goat");
            boxColors = List.of(Color.LIGHTCORAL, Color.LIGHTSALMON, Color.LIGHTCYAN, Color.LIGHTSTEELBLUE, Color.LIGHTPINK, Color.LIGHTGOLDENRODYELLOW);
        } else {
            animalTypes = List.of("Crocodile", "Hippopotamus", "Elephant", "Giraffe", "Rhinoceros", "Panda", "Tiger", "Panther", "Zebra", "Ostrich");
            boxColors = List.of(Color.LIGHTCORAL, Color.LIGHTSALMON, Color.LIGHTCYAN, Color.LIGHTSTEELBLUE, Color.LIGHTPINK,
                    Color.LIGHTGOLDENRODYELLOW, Color.LIGHTSEAGREEN, Color.LIGHTSKYBLUE, Color.LIGHTGREEN, Color.LIGHTGRAY);
        }

        // Create a shuffled copy of animalTypes for the animals only
        List<String> shuffledAnimalTypes = new ArrayList<>(animalTypes);
        Collections.shuffle(shuffledAnimalTypes);

        if (level == 4) {
            double boxWidth = 120;
            double totalBoxWidth = 5 * boxWidth;
            double totalSpacing = SCENE_WIDTH - totalBoxWidth - 200;
            double boxSpacingX = totalSpacing / 4;
            double boxStartX = 100;
            double boxStartY = 100;

            // Use the unshuffled animalTypes for category boxes
            for (int i = 0; i < animalTypes.size(); i++) {
                int row = i / 5;
                int col = i % 5;
                double boxX = boxStartX + col * (boxWidth + boxSpacingX);
                double boxY = boxStartY + row * (boxWidth + 50);
                CategoryBox box = new CategoryBox(boxX, boxY, animalTypes.get(i), boxColors.get(i));
                categoryBoxes.add(box);
            }

            double animalStartY = boxStartY + 2 * (boxWidth + 50) + 50;

            double animalSpacingX = boxSpacingX + boxWidth;
            // Use the shuffled list for animal positions
            for (int i = 0; i < shuffledAnimalTypes.size(); i++) {
                int row = i / 5;
                int col = i % 5;
                double animalX = boxStartX + col * (boxWidth + boxSpacingX) + 20;
                double animalY = animalStartY + row * 100;
                Animal animal = AnimalFactory.createAnimal(shuffledAnimalTypes.get(i), gc, animalX, animalY);
                animals.add(animal);
                gameState.addAnimal(animal);
                System.out.println("Created animal: " + animal.getCategory() + " at position (" + animalX + ", " + animalY + ")");
            }
            System.out.println("Total animals created for Level 4: " + animals.size());
        } else {
            double animalSpacing = SCENE_WIDTH / (animalTypes.size() + 1);
            double boxSpacing = SCENE_WIDTH / (animalTypes.size() + 1);

            // Use the unshuffled animalTypes for category boxes
            for (int i = 0; i < animalTypes.size(); i++) {
                double boxX = (i + 1) * boxSpacing - 60;
                CategoryBox box = new CategoryBox(boxX, 350, animalTypes.get(i), boxColors.get(i));
                categoryBoxes.add(box);
            }

            // Use the shuffled list for animal positions
            for (int i = 0; i < shuffledAnimalTypes.size(); i++) {
                double animalX = (i + 1) * animalSpacing - 40;
                Animal animal = AnimalFactory.createAnimal(shuffledAnimalTypes.get(i), gc, animalX, 80);
                animals.add(animal);
                gameState.addAnimal(animal);
            }
        }

        gamePane.getChildren().addAll(categoryBoxes.stream().map(CategoryBox::getRect).collect(Collectors.toList()));
        gamePane.getChildren().addAll(categoryBoxes.stream().map(CategoryBox::getLabel).collect(Collectors.toList()));
    }

    private void startTimer() {
        if (timer != null) {
            timer.stop();
        }

        int level = gameState.getLevel();
        switch (level) {
            case 1:
                timeLeft = 15;
                break;
            case 2:
                timeLeft = 20;
                break;
            case 3:
                timeLeft = 30;
                break;
            case 4:
                timeLeft = 40;
                break;
            default:
                timeLeft = 15;
        }

        timerLabel.setText("Time Left: " + timeLeft + "s");
        timerLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: black;");

        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeLeft--;
            timerLabel.setText("Time Left: " + timeLeft + "s");

            if (timeLeft <= 10) {
                timerLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: red;");
            }

            if (timeLeft <= 0) {
                timer.stop();
                Platform.runLater(this::showGameOverDialog);
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void showGameOverDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Over! 😔");
        alert.setHeaderText("Time's up!");
        alert.setContentText("You ran out of time on Level " + gameState.getLevel() + ". Would you like to try again?");

        ButtonType tryAgainButton = new ButtonType("Try Again");
        ButtonType cancelButton = new ButtonType("Cancel");
        alert.getButtonTypes().setAll(tryAgainButton, cancelButton);

        alert.getDialogPane().setStyle("-fx-background-color: LIGHTYELLOW; -fx-font-family: 'Comic Sans MS'; -fx-border-color: PINK; -fx-border-width: 3;");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == tryAgainButton) {
                gameState.resetScore();
                gameState.resetAnimals();
                categoryBoxes.forEach(box -> box.setOccupied(false));
                initializeGame();
                feedbackLabel.setText("Drag animals to their categories! Score: " + gameState.getScore());
                feedbackLabel.setTextFill(Color.BLACK);
                startTimer();
                redraw();
            } else if (result.get() == cancelButton) {
                gameState.setLevel(1);
                gameState.resetScore();
                gameState.resetAnimals();
                showWelcomePage(root, primaryStage);
            }
        }
    }

    public void showLevelUpDialog() {
        System.out.println("showLevelUpDialog called for Level: " + gameState.getLevel());
        timer.stop();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        int currentLevel = gameState.getLevel();
        int nextLevel = currentLevel + 1;

        if (currentLevel >= 4) {
            alert.setTitle("You’re Awesome! 🎉");
            alert.setHeaderText("Congratulations! You’ve completed all levels!");
            alert.setContentText("You earned 4 stars! ⭐⭐⭐⭐ Want to play again?");

            ButtonType playAgainButton = new ButtonType("Play Again");
            ButtonType cancelButton = new ButtonType("Cancel");
            alert.getButtonTypes().setAll(playAgainButton, cancelButton);

            alert.getDialogPane().setStyle("-fx-background-color: LIGHTCYAN; -fx-font-family: 'Comic Sans MS'; -fx-border-color: YELLOW; -fx-border-width: 3;");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == playAgainButton) {
                    gameState.setLevel(1);
                    gameState.resetScore();
                    gameState.resetAnimals();
                    categoryBoxes.forEach(box -> box.setOccupied(false));
                    initializeGame();
                    feedbackLabel.setText("Drag animals to their categories! Score: " + gameState.getScore());
                    feedbackLabel.setTextFill(Color.BLACK);
                    startTimer();
                    redraw();
                } else if (result.get() == cancelButton) {
                    showResultDialog(currentLevel);
                }
            }
        } else {
            alert.setTitle("Level Up! 🥳");
            alert.setHeaderText("You completed Level " + currentLevel + "!");
            alert.setContentText("You earned " + currentLevel + " star(s)! ⭐".repeat(currentLevel) + "\nWould you like to proceed to Level " + nextLevel + "?");

            ButtonType continueButton = new ButtonType("Continue");
            ButtonType cancelButton = new ButtonType("Cancel");
            alert.getButtonTypes().setAll(continueButton, cancelButton);

            alert.getDialogPane().setStyle("-fx-background-color: LIGHTCYAN; -fx-font-family: 'Comic Sans MS'; -fx-border-color: YELLOW; -fx-border-width: 3;");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == continueButton) {
                    gameState.setLevel(nextLevel);
                    gameState.resetScore();
                    gameState.resetAnimals();
                    categoryBoxes.forEach(box -> box.setOccupied(false));
                    initializeGame();
                    feedbackLabel.setText("Drag animals to their categories! Score: " + gameState.getScore());
                    feedbackLabel.setTextFill(Color.BLACK);
                    startTimer();
                    redraw();
                } else if (result.get() == cancelButton) {
                    showResultDialog(currentLevel);
                }
            }
        }
    }

    private void showResultDialog(int levelsCompleted) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Your Results! 🌟");
        dialog.setHeaderText("Great Job!");
        dialog.getDialogPane().setStyle("-fx-background-color: LIGHTPINK; -fx-font-family: 'Comic Sans MS'; -fx-border-color: BLUE; -fx-border-width: 3;");

        String stars = "⭐".repeat(levelsCompleted);
        Label resultLabel = new Label("You earned " + levelsCompleted + " star(s)!\n" + stars);
        resultLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: GOLD; -fx-font-weight: bold;");

        String message = levelsCompleted == 4 ? "You’re a Super Star! 🌈" : "Keep going, you’re amazing! 🐾";
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: PURPLE;");

        Timeline starAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0.5), new KeyValue(resultLabel.scaleXProperty(), 1.2)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(resultLabel.scaleYProperty(), 1.2)),
                new KeyFrame(Duration.seconds(1), new KeyValue(resultLabel.scaleXProperty(), 1.0)),
                new KeyFrame(Duration.seconds(1), new KeyValue(resultLabel.scaleYProperty(), 1.0))
        );
        starAnimation.setCycleCount(Timeline.INDEFINITE);
        starAnimation.play();

        VBox content = new VBox(10, resultLabel, messageLabel);
        content.setStyle("-fx-padding: 20px; -fx-alignment: center;");

        dialog.getDialogPane().setContent(content);

        ButtonType continueButton = new ButtonType(levelsCompleted < 4 ? "Continue" : "Play Again");
        ButtonType exitButton = new ButtonType("Exit");
        dialog.getDialogPane().getButtonTypes().addAll(continueButton, exitButton);

        Button continueBtn = (Button) dialog.getDialogPane().lookupButton(continueButton);
        continueBtn.setStyle("-fx-font-family: 'Comic Sans MS'; -fx-background-color: LIME; -fx-text-fill: black; -fx-background-radius: 10;");
        Button exitBtn = (Button) dialog.getDialogPane().lookupButton(exitButton);
        exitBtn.setStyle("-fx-font-family: 'Comic Sans MS'; -fx-background-color: ORANGE; -fx-text-fill: black; -fx-background-radius: 10;");

        continueBtn.setOnAction(event -> {
            try {
                if (levelsCompleted < 4) {
                    gameState.setLevel(levelsCompleted + 1);
                } else {
                    gameState.setLevel(1);
                }
                gameState.resetScore();
                gameState.resetAnimals();
                categoryBoxes.forEach(box -> box.setOccupied(false));
                initializeGame();
                feedbackLabel.setText("Drag animals to their categories! Score: " + gameState.getScore());
                feedbackLabel.setTextFill(Color.BLACK);
                startTimer();
                redraw();
                dialog.setResult(null);
                dialog.close();
            } catch (Exception e) {
                System.err.println("Error in continue button handler: " + e.getMessage());
                e.printStackTrace();
                dialog.setResult(null);
                dialog.close();
            }
        });

        exitBtn.setOnAction(event -> {
            try {
                gameState.setLevel(1);
                gameState.resetScore();
                gameState.resetAnimals();
                showWelcomePage(root, primaryStage);
                dialog.setResult(null);
                dialog.close();
            } catch (Exception e) {
                System.err.println("Error in exit button handler: " + e.getMessage());
                e.printStackTrace();
                dialog.setResult(null);
                dialog.close();
            }
        });

        dialog.showAndWait();
    }

    private void resetGame() {
        // Shuffle the animals list to randomize their positions when resetting
        Collections.shuffle(animals);
        for (Animal animal : animals) {
            resetPosition(animal);
        }
        gameState.resetAnimals();
        gameState.resetScore();
        categoryBoxes.forEach(box -> {
            box.setOccupied(false);
            box.getRect().setStroke(Color.BLACK);
            box.getRect().setStrokeWidth(1);
        });
        feedbackLabel.setText("Drag animals to their categories! Score: " + gameState.getScore());
        feedbackLabel.setTextFill(Color.BLACK);
        for (Animal animal : animals) {
            gameState.addAnimal(animal);
        }
        redraw();
    }

    private void resetPosition(Animal animal) {
        int level = gameState.getLevel();
        int index = animals.indexOf(animal); // Use the shuffled index
        if (level == 4) {
            double boxWidth = 120;
            double totalBoxWidth = 5 * boxWidth;
            double totalSpacing = SCENE_WIDTH - totalBoxWidth - 200;
            double boxSpacingX = totalSpacing / 4;
            double boxStartX = 100;
            double boxStartY = 100;

            double animalStartY = boxStartY + 2 * (boxWidth + 50) + 50;

            int row = index / 5;
            int col = index % 5;
            double animalX = boxStartX + col * (boxWidth + boxSpacingX) + 20;
            double animalY = animalStartY + row * 100;
            animal.setPosition(animalX, animalY);
        } else {
            double animalSpacing = SCENE_WIDTH / (animals.size() + 1);
            double animalX = (index + 1) * animalSpacing - 40;
            animal.setPosition(animalX, 80);
        }
    }

    private void setupDragAndDrop() {
        gamePane.setOnMousePressed(event -> {
            for (Animal animal : animals) {
                if (gameState.isPlaced(animal)) continue;

                if (event.getX() >= animal.x && event.getX() <= animal.x + 80 &&
                        event.getY() >= animal.y && event.getY() <= animal.y + 80) {

                    draggedAnimal = animal;

                    draggedImageView = new ImageView(animal.getImage());
                    draggedImageView.setFitWidth(80);
                    draggedImageView.setFitHeight(80);
                    draggedImageView.setX(animal.x);
                    draggedImageView.setY(animal.y);
                    gamePane.getChildren().add(draggedImageView);

                    gamePane.setOnMouseDragged(dragEvent -> {
                        if (!gameState.isPlaced(animal)) {
                            animal.setPosition(dragEvent.getX() - 40, dragEvent.getY() - 40);
                            draggedImageView.setX(dragEvent.getX() - 40);
                            draggedImageView.setY(dragEvent.getY() - 40);
                            redraw();
                        }
                    });

                    gamePane.setOnMouseReleased(releaseEvent -> {
                        if (gameState.isPlaced(animal)) return;

                        boolean placedCorrectly = false;
                        for (CategoryBox box : categoryBoxes) {
                            if (!box.isOccupied() && box.contains(releaseEvent.getX(), releaseEvent.getY())) {
                                if (box.getCategory().equals(animal.getCategory())) {
                                    animal.setPosition(box.getRect().getX() + 20, box.getRect().getY() + 20);
                                    gameState.setPlaced(animal, true);
                                    box.setOccupied(true);
                                    gameState.increaseScore();
                                    feedbackLabel.setText("Correct! Score: " + gameState.getScore());
                                    feedbackLabel.setTextFill(Color.GREEN);
                                    box.getRect().setStroke(Color.GREEN);
                                    box.getRect().setStrokeWidth(3);
                                    placedCorrectly = true;
                                }
                                break;
                            }
                        }
                        if (!placedCorrectly) {
                            feedbackLabel.setText("Wrong! Try again.");
                            feedbackLabel.setTextFill(Color.RED);
                            resetPosition(animal);
                        }
                        gamePane.getChildren().remove(draggedImageView);
                        draggedImageView = null;
                        draggedAnimal = null;
                        redraw();
                    });

                    return;
                }
            }
        });
    }

    private void redraw() {
        gc.clearRect(0, 0, SCENE_WIDTH, SCENE_HEIGHT);
        gc.setFill(Color.LIGHTPINK);
        gc.fillRect(0, 0, SCENE_WIDTH, SCENE_HEIGHT);

        for (Animal animal : animals) {
            if (animal != draggedAnimal) {
                animal.update();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}