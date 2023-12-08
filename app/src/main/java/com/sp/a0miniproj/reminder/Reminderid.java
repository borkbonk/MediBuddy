package com.sp.a0miniproj.reminder;

public class Reminderid {
    private long id;
    private String title;
    private String description;
    private long timestamp;

    public Reminderid() {
        // Empty constructor required for SQLite
    }

    public Reminderid(String title, String description, long timestamp) {
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
