package com.example.project.activities;

import java.util.Map;

public class Category {
    String name;
    Map<String, Item> items;

    public Category(){}

    public Category(String name, Map<String, Item> items) {
        this.name = name;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Item> getItems() {
        return items;
    }

    public void setItems(Map<String, Item> items) {
        this.items = items;
    }
}
