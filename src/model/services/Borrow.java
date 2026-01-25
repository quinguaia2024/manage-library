package model.services;

import java.time.Instant;


import model.core.Entity;

enum BorrowStatus {
    ACTIVE,
    RETURNED,
    LATE
}

abstract class BorrowProps {
    String BookId;
    String readerId;
    Instant BorrowDate;
    Instant whenToreturnDate;
}

public class Borrow extends Entity<BorrowProps> implements model.core.Aggregateroot {
    private Instant returnDate;
    private BorrowStatus Borrowstatus;
    private Fine fineAttached;
    private Borrow(BorrowProps props, String id) {
        super(props, id);
        this.returnDate = null;
        this.Borrowstatus = BorrowStatus.ACTIVE;  
        this.fineAttached = null;
    }

    public static Borrow makeBorrow(String id, String bookId, String readerId, Instant borrowDate, Instant whenToReturnDate) {
        final BorrowProps props = new BorrowProps() {};
        props.BookId = bookId;
        props.readerId = readerId;
        props.BorrowDate = borrowDate;
        props.whenToreturnDate = whenToReturnDate;
        return new Borrow(props, id);
    }


    public String getBookId() {
        return this.getProps().BookId;
    }

    public String getUserId() {
        return this.getProps().readerId;
    }

    public Instant getBorrowDate() {
        return this.getProps().BorrowDate;
    }

    public Instant getReturnDate() {
        return this.returnDate;
    }

    public void setReturnDate(Instant returnDate) {
        this.returnDate = returnDate;
    }

     public BorrowStatus getBorrowStatus() {
        return this.Borrowstatus;
    }

    public void alterBorrowStatus(BorrowStatus status) {
        this.Borrowstatus = status;
    }

    Instant readWhenToReturnDate() {
        return this.getProps().whenToreturnDate;
    }

    public Fine getFineAttached() {
        return this.fineAttached;
    }

    public void attachFine(int amount, String idFine) {
        this.fineAttached = Fine.addFine(this.getId(), this.getProps().readerId, amount, idFine);
    }

    
}   