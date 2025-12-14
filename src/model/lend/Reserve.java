package model.lend;

import model.core.Entity;
import java.sql.Date;


enum reservationStatus {
    ACTIVE,
    CANCELLED,
    ATTENDED
}

abstract class reserveProps {
    public String bookId;
    public String readerId;
    public Date reserveDate;
}

public class Reserve extends Entity<reserveProps> implements model.core.Aggregateroot {
    private reservationStatus ReservationStatus;
    public Reserve(String id, reserveProps props) {
        super(props, id);
        this.ReservationStatus = reservationStatus.ACTIVE; 
    }

    public String getId(){
        return this.getProps().bookId;
    }

    public reservationStatus getReservationStatus() {
        return ReservationStatus;
    }

    public void setReservationStatus(reservationStatus status) {
        this.ReservationStatus = status;
    }

    public Date getReserveDate() {
        return this.getProps().reserveDate;
    }

    public String getReaderId() {
        return this.getProps().readerId;
    }
}