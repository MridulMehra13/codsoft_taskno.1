package com.example.newtodo;

public class Task {
    private String id;
    private String title;
    private boolean isUrgent;
    private String date;
    private String time;

    public Task() {
        // Default constructor required by Firebase for deserialization
    }

    public Task(String id, String title, boolean isUrgent, String date, String time) {
        this.id = id;
        this.title = title;
        this.isUrgent = isUrgent;
        this.date = date;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isUrgent() {
        return isUrgent;
    }

    public void setUrgent(boolean urgent) {
        isUrgent = urgent;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
