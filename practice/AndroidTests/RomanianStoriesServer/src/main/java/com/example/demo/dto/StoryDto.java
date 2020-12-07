package com.example.demo.dto;

import lombok.Builder;

@Builder
public class StoryDto {
    private long id;
    private String title;
    private String category;
    private String description;
    private String link;
    private String pictureUrl;
    private AuthorDto author;

    public StoryDto() {
    }

    public StoryDto(long id, String title, String category, String description, String link, String pictureUrl, AuthorDto author) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.link = link;
        this.pictureUrl = pictureUrl;
        this.author = author;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public AuthorDto getAuthor() {
        return author;
    }

    public void setAuthor(AuthorDto author) {
        this.author = author;
    }
}
