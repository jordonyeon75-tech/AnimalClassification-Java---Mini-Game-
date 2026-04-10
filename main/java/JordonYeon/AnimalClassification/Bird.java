package JordonYeon.AnimalClassification;

import javafx.scene.canvas.GraphicsContext;

public class Bird extends Animal {
    public Bird(GraphicsContext gc, double x, double y) {
    	super(gc, x, y, "Bird", "/JordonYeon/AnimalClassification/image/bird.png");
    }
}
