package model.application.book;

import model.book.Book;
import model.book.BookRepo;

public class FindOneBook {
    private BookRepo bookRepository;

    public FindOneBook(BookRepo bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book execute(String id) {
        return this.bookRepository.findOne(id);
    }
}