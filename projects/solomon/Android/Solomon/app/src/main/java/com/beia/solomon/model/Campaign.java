package com.beia.solomon.model;

import java.util.List;

public class Campaign {
    private long id;
    private String title;
    private String description;
    private Category category;
    private String startDate;
    private String endDate;
    private String image;
    private User user;

    public Campaign() {
    }

    public Campaign(long id, String title, String description, Category category, String startDate, String endDate, String image, User user) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.image = image;
        this.user = user;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Campaign{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category=" + category +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", user=" + user +
                '}';
    }
}