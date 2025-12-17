package model.book;

import model.core.Entity;


enum bookCopiesStatus{
   AVAILABLE,
   BORROWED,
   RESERVED,
   SPOILED
}

abstract class bookcopiesProps {
    String bookId;
    String barCode;
}

public class Bookcopies extends Entity<bookcopiesProps> {
   private bookCopiesStatus statusBookCopy;

   private Bookcopies(bookcopiesProps props, String id){
      super(props, id);
      this.statusBookCopy = bookCopiesStatus.AVAILABLE;
   }


  public static Bookcopies makeBookcopies(String bookId, String barCode, String idBookCopy ){
     final bookcopiesProps props = new bookcopiesProps() {};
     props.bookId = bookId;
     props.barCode = barCode;
     return new Bookcopies(props, idBookCopy);
  }

   public bookCopiesStatus readStatusBookCopy() {
       return this.statusBookCopy;
   }

   public String readBarCode() {
       return this.getProps().barCode;
   }

   void changeCopyStatusToBorrowed() {
       this.statusBookCopy = bookCopiesStatus.BORROWED;
   }

    void changeCopyStatusToAvailable() {
         this.statusBookCopy = bookCopiesStatus.AVAILABLE;
    }

    void changeCopyStatusToReserved() {
         this.statusBookCopy = bookCopiesStatus.RESERVED;
    }

    void changeCopyStatusToSpoiled() {
         this.statusBookCopy = bookCopiesStatus.SPOILED;
    }

    
    
}
