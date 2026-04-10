package JordonYeon.AnimalClassification;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class CategoryBox {
    private Rectangle rect;
    private String category;
    private boolean occupied;
    private Text label;

    public CategoryBox(double x, double y, String category, Color color) {
        this.rect = new Rectangle(x, y, 120, 120);
        this.category = category;
        this.occupied = false;
        this.rect.setFill(color);
        this.rect.setStroke(Color.BLACK);
        this.rect.setStrokeWidth(1);

        // Center the label below the box
        this.label = new Text(x, y + 120 + 20, category);
        this.label.setStyle("-fx-font-size: 14px; -fx-fill: black;"); // Reduced font size to 14px
        this.label.setTextAlignment(TextAlignment.CENTER);
        this.label.setWrappingWidth(120);
        this.label.setStroke(null);
        // Adjust the X position to center the label under the box
        this.label.setX(x + (120 - this.label.getBoundsInLocal().getWidth()) / 2);
    }

    public Rectangle getRect() {
        return rect;
    }

    public Text getLabel() {
        return label;
    }

    public String getCategory() {
        return category;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public boolean contains(double x, double y) {
        return rect.contains(x, y);
    }
}