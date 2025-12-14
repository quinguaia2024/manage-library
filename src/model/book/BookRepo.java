package model.book;

public abstract class BookRepo implements model.core.Irepository<Book> {
   public abstract void save(Book book, int qty);
   public abstract Book findOne(String id);
   public abstract Book[] findAll();
   public abstract void delete(String id);
   public abstract void update(String id, Book book);
   public abstract Book[] findDeletedBooks();
    
}
