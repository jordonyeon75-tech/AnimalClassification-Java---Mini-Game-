package JordonYeon.AnimalClassification;

import javafx.scene.canvas.GraphicsContext;

public abstract class GameObject {
    protected GraphicsContext gc;
    protected double x, y;

    public GameObject(GraphicsContext gc, double x, double y) {
        this.gc = gc;
        this.x = x;
        this.y = y;
    }

    public abstract void update();

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}