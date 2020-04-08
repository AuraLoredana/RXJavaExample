package com.example.rxjavaexample;

public class Task {
    private String description;
    private boolean isComplete;
    private int priority;

    Task(String description, boolean isComplete, int priority) {
        this.description = description;
        this.isComplete = isComplete;
        this.priority = priority;
    }

    String getDescription() {
        return description;
    }

    public boolean IsComplete() {
        return isComplete;
    }

    public int getPriority() {
        return priority;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    boolean isComplete() {
        return isComplete;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
