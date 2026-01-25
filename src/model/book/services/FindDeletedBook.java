package model.book.services;
import model.book.Book;
import model.book.BookRepo;

public class FindDeletedBook {
    private BookRepo bookRepository;

    public FindDeletedBook(BookRepo bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book[] execute() {
        return this.bookRepository.findDeletedBooks();
    }
}   