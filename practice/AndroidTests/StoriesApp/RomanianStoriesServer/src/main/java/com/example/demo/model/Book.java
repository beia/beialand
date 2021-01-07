package com.example.demo.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Book {

  private String id;
  private String title;
  private String author;
  private double price;

  public Book(String id) {
    this.id = id;
  }

  public Book() {
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

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }
}
