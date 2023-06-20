package com.example.pum_todo;

public class TodoItem {
    private String id;
    private String title;
    private String description;
    private int done;

    public TodoItem(String id, String title, String description, int done) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.done = done;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int isDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }
}
