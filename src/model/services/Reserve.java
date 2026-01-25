package model.services;

import model.core.Entity;
import model.core.Aggregateroot;
import java.time.Instant;


enum ReservationStatus {
    ACTIVE,
    CANCELLED,
    ATTENDED
}


abstract class ReserveProps {
    String bookId;
    String readerId;
    Instant reserveDate;
}


public class Reserve extends Entity<ReserveProps> implements Aggregateroot {
    private ReservationStatus reservationStatus;

    private Reserve(ReserveProps props, String id) {
        super(props, id);
        this.reservationStatus = ReservationStatus.ACTIVE;
    }

    public static Reserve makeReservation(String id, String bookId, String readerId, Instant reserveDate) {
      final ReserveProps props = new ReserveProps(){};
        props.bookId = bookId;
        props.readerId = readerId;
        props.reserveDate = reserveDate;
        return new Reserve(props, id);
    }

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(ReservationStatus status) {
        this.reservationStatus = status;
    }

    public Instant getReserveDate() {
        return this.getProps().reserveDate;
    }

    public String getReaderId() {
        return this.getProps().readerId;
    }

    public String getBookId() {
        return this.getProps().bookId;
    }
}