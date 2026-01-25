package model.services;

import model.core.Entity;
import java.time.Instant;

abstract class waitlistProps {
    public String bookId;
    public String readerId;
    public Instant waitlistDate;
}


public class Waitlist extends Entity<waitlistProps>  {
    private Waitlist(String id, waitlistProps props) {
        super(props, id);
    }

static Waitlist makeWaitlist(String id, waitlistProps props) {
        return new Waitlist(id, props);
    }   

    public static Waitlist makeWaitlist(String id, String bookId, String readerId, Instant waitlistDate) {
        final waitlistProps props = new waitlistProps() {};
        props.bookId = bookId;
        props.readerId = readerId;
        props.waitlistDate = waitlistDate;
        return new Waitlist(id, props);
    }

    public String getId(){
        return this.getProps().bookId;
    }

    public Instant getWaitlistDate() {
        return this.getProps().waitlistDate;
    }

    public String getReaderId() {
        return this.getProps().readerId;
    }
    
}
