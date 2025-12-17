package model.application.book;

import model.book.BookRepo;

public class DeleteBook {
    private BookRepo bookRepository;

    public DeleteBook(BookRepo bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void execute(String id) {
        this.bookRepository.delete(id);
    }
}   