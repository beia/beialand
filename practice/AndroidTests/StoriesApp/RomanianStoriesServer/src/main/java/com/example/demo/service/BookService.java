package com.example.demo.service;

import java.util.List;

import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import org.springframework.stereotype.Service;

@Service
public class BookService {

  private BookRepository bookRepository;

  public BookService(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  public void saveBooks(List<Book> books) {
    bookRepository.saveAll(books);
  }
}
