package model.book.usecase;

import model.book.Book;
import model.book.BookRepo;

public class CreateBook {
    private BookRepo bookRepository;

    public CreateBook(BookRepo bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void execute(Book book, int qty) {
        this.bookRepository.save(book, qty);
    }
}
