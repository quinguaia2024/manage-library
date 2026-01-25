package model.services;

// import model.lend.Borrow;
import java.util.List;

public interface BorrowRepo {
    void save(Borrow borrow);
    Borrow findById(String id);
    List<Borrow> findActiveBorrowsByReaderId(String readerId);
    List<Borrow> findActiveBorrowsByBookId(String bookId);
    void editBorrow(String idBorrow); 
}
