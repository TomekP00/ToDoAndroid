package com.example.pum_todo;

public class TodoItem {
    private String id;
    private String title;
    private String description;
    private String done;

    public TodoItem(String id, String title, String description, String done) {
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

    public String isDone() {
        return done;
    }

    public void setDone(String done) {
        this.done = done;
    }

}
