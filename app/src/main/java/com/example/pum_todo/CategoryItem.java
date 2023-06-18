package com.example.pum_todo;

public class CategoryItem {
    private String id;
    private String name;

    public CategoryItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
