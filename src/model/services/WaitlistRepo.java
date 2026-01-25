package model.services;

import java.util.List;

public abstract class WaitlistRepo {
    void addToWaitlist(Waitlist waitlist){};
    void removeFromWaitlist(String waitlistId){};
    Waitlist getWaitlistById(String waitlistId){ return null; };
    List<Waitlist> getWaitlistsByBookId(String bookId){ return null; };
    List<Waitlist> getWaitlistsByReaderId(String readerId){ return null; };
    List<Waitlist> findAll(){ return null; };
}
