package com.example.project.activities;

public class Item {
    String name;
    String imagePath;
    int quantity;

    public Item(){}

    public Item(String name, String imagePath, int quantity) {
        this.name = name;
        this.imagePath = imagePath;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
