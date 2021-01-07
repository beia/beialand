package com.example.demo.model;

import java.util.List;

@Document
public class Customer {

  private String id;
  private String name;
  private String email;
  private int age;
  @Field(name = "is_student")
  private boolean student;
  @DBRef
  private List<Book> books;

  public Customer(String id, String name, String email, int age, boolean student,
      List<Book> books) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.age = age;
    this.student = student;
    this.books = books;
  }

  public Customer() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public boolean isStudent() {
    return student;
  }

  public void setStudent(boolean student) {
    this.student = student;
  }

  public List<Book> getBooks() {
    return books;
  }

  public void setBooks(List<Book> books) {
    this.books = books;
  }
}
