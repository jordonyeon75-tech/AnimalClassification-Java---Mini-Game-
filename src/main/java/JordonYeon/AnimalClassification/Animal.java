package JordonYeon.AnimalClassification;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class Animal extends GameObject {
    protected String category;
    protected String imagePath;
    protected Image image;
    protected boolean placed; // Add field to track placement

    public Animal(GraphicsContext gc, double x, double y, String category, String imagePath) {
        super(gc, x, y);
        this.category = category;
        this.imagePath = imagePath;
        this.placed = false; // Initialize placed to false
        loadImage();
    }

    protected void loadImage() {
        try {
            image = new Image(getClass().getResourceAsStream(imagePath));
        } catch (Exception e) {
            System.err.println("Failed to load image from path: " + imagePath);
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        if (image != null) {
            gc.drawImage(image, x, y, 80, 80);
        }
    }

    public String getCategory() {
        return category;
    }

    public Image getImage() { // Add method to return the image
        return image;
    }

    public void setPlaced(boolean placed) { // Add method to set placed status
        this.placed = placed;
    }

    public boolean isPlaced() { // Add method to check placed status
        return placed;
    }
}