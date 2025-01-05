package bookstore_app;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Book {
    private final String name;
    private final double price;
    private boolean selected;

    //Constructor
    public Book(String name, double price) {
        this.name = name;
        this.price = price;
        this.selected = false;
    }

    //Getters
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public boolean isSelected() {
        return selected;
    }

    //Setters
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}