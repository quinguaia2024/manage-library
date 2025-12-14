import model.book.Book;

enum BorrowStatus {
    ACTIVE,
    RETURNED,
    LATE
}

abstract class BorrowProps {
    String BookId;
    String readerId;
    Date BorrowDate;
}

public class Borrow extends Entity<BorrowProps> implements model.core.AggregateRoot {
    private Date returnDate;
    private BorrowStatus Borrowstatus;
    public Borrow(BorrowProps props, String id) {
        super(props, id);
        this.returnDate = null;
        this.Borrowstatus = BorrowStatus.ACTIVE;   
    }
    public String getBookId() {
        return this.getProps().BookId;
    }

    public String getUserId() {
        return this.getProps().UserId;
    }

    public Date getBorrowDate() {
        return this.getProps().BorrowDate;
    }

    public Date getReturnDate() {
        return this.getProps().ReturnDate;
    }

    public setReturnDate(Date returnDate) {
        this.getProps().ReturnDate = returnDate;
    }

    public alterBorrowStatus(BorrowStatus status) {
        this.Borrowstatus = status;
    }

    
}   