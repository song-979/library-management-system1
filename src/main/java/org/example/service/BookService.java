package org.example.service;

import org.example.model.Book;
import java.util.List;

public interface BookService {
    List<Book> getAllBooks();
    boolean addBook(Book book);
    boolean updateBook(Book book);
    boolean deleteBook(int bookId);
}