package JordonYeon.AnimalClassification;

import javafx.scene.canvas.GraphicsContext;

public class Turtle extends Animal {
    public Turtle(GraphicsContext gc, double x, double y) {
    	super(gc, x, y, "Turtle", "/JordonYeon/AnimalClassification/image/turtle.png");
    }
}
