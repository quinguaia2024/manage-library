package model.book;

import model.core.Entity;

abstract class BookProps {
    String title;
    String author;
    String isbn;
    int publicationYear;
}

public class Book extends Entity<BookProps> {

    private Book(BookProps props, String id) {
        super(props, id);
    }

    public static Book create(BookProps props, String id) {
        return new Book(props, id);
    }

    String getTitle() {
        return this.getProps().title;
    }

    String getAuthor() {
        return this.getProps().author;
    }

    String getIsbn() {
        return this.getProps().isbn;
    }

    int getPublicationYear() {
        return this.getProps().publicationYear;
    }
    
}
