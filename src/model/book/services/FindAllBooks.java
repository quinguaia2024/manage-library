package model.book.services;
import model.book.Book;
import model.book.BookRepo;

public class FindAllBooks {
    private BookRepo bookRepository;

    public FindAllBooks(BookRepo bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book[] execute() {
        return this.bookRepository.findAll();
    }
}