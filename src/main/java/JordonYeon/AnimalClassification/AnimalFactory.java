package JordonYeon.AnimalClassification;

import javafx.scene.canvas.GraphicsContext;

public class AnimalFactory {
    public static Animal createAnimal(String type, GraphicsContext gc, double x, double y) {
        switch (type) {
            case "Rabbit": return new Rabbit(gc, x, y);
            case "Turtle": return new Turtle(gc, x, y);
            case "Bird": return new Bird(gc, x, y);
            case "Dog": return new Dog(gc, x, y);
            case "Cat": return new Cat(gc, x, y);
            case "Lion": return new Lion(gc, x, y);
            case "Monkey": return new Monkey(gc, x, y);
            case "Cow": return new Cow(gc, x, y);
            case "Ox": return new Ox(gc, x, y);
            case "Lamb": return new Lamb(gc, x, y);
            case "Goat": return new Goat(gc, x, y);
            case "Crocodile": return new Crocodile(gc, x, y);
            case "Hippopotamus": return new Hippopotamus(gc, x, y);
            case "Elephant": return new Elephant(gc, x, y);
            case "Giraffe": return new Giraffe(gc, x, y);
            case "Rhinoceros": return new Rhinoceros(gc, x, y);
            case "Panda": return new Panda(gc, x, y);
            case "Tiger": return new Tiger(gc, x, y);
            case "Panther": return new Panther(gc, x, y);
            case "Zebra": return new Zebra(gc, x, y);
            case "Ostrich": return new Ostrich(gc, x, y);
            default: throw new IllegalArgumentException("Unknown animal type: " + type);
        }
    }
}