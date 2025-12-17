package model.book;

import model.core.Entity;
import java.util.List;
import model.core.Aggregateroot;



abstract class BookProps {
    String title;
    String author;
    String isbn;
    int publicationYear;
    
}

public class Book extends Entity<BookProps> implements Aggregateroot {
    private List<Bookcopies> copies;
    private Book(BookProps props, String id) {
        super(props, id);
    }

    public static Book makeBook(String id, String title, String author, String isbn, int publicationYear, int qty) {
        final BookProps props = new BookProps() {};
        props.title = title;
        props.author = author;
        props.isbn = isbn;
        props.publicationYear = publicationYear;
        return new Book(props, id);
    }

    public void addBookCopies(Bookcopies copy) {
        this.copies.add(copy);
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

    int readBookCopiesQty() {
        return this.copies.size();
    }

    int readBookcopiesAvailable() {
        int available = 0;
        for (Bookcopies copy : this.copies) {
            if (copy.readStatusBookCopy() == bookCopiesStatus.AVAILABLE) {
                available++;
            }
        }
        return available;
    }

    int readBookcopiesReserved() {
        int reserved = 0;
        for (Bookcopies copy : this.copies) {
            if (copy.readStatusBookCopy() == bookCopiesStatus.RESERVED) {
                reserved++;
            }
        }
        return reserved;
    }

    int readBookcopiesBorrowed() {
        int borrowed = 0;
        for (Bookcopies copy : this.copies) {
            if (copy.readStatusBookCopy() == bookCopiesStatus.BORROWED) {
                borrowed++;
            }
        }
        return borrowed;
    }


    int readBookcopiesSpoiled() {
        int spoiled = 0;
        for (Bookcopies copy : this.copies) {
            if (copy.readStatusBookCopy() == bookCopiesStatus.SPOILED) {
                spoiled++;
            }
        }
        return spoiled;
    }

   void BorrowOneCopy() {
       for (Bookcopies copy : this.copies) {
           if (copy.readStatusBookCopy() == bookCopiesStatus.AVAILABLE) {
               copy.changeCopyStatusToBorrowed();
               break;
           }
       }
   }

   void ReserveOneCopy() {
       for (Bookcopies copy : this.copies) {
           if (copy.readStatusBookCopy() == bookCopiesStatus.AVAILABLE) {
               copy.changeCopyStatusToReserved();
               break;
           }
       }
   }

   void ReturnOneCopy() {
       for (Bookcopies copy : this.copies) {
           if (copy.readStatusBookCopy() == bookCopiesStatus.BORROWED) {
               copy.changeCopyStatusToAvailable();
               break;
           }
       }
   }

}