package org.example.service.impl;

import org.example.dao.BookDAO;
import org.example.model.Book;
import org.example.service.BookService;
import java.util.List;

public class BookServiceImpl implements BookService {
    private final BookDAO bookDAO = new BookDAO();

    @Override
    public List<Book> getAllBooks() {
        return bookDAO.getAllBooks();
    }

    @Override
    public boolean addBook(Book book) {
        return bookDAO.addBook(book);
    }

    @Override
    public boolean updateBook(Book book) {
        return bookDAO.updateBook(book);
    }

    @Override
    public boolean deleteBook(int bookId) {
        return bookDAO.deleteBook(bookId);
    }
}