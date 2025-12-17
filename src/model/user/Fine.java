package model.user;

import model.core.Entity;

enum fineStatus{
   PAID,
   PENDING
}

abstract class  fineProps{
   String borrowId;
   int amount;    
}

public class Fine extends Entity<fineProps> {
   private fineStatus statusFine;
   private Fine(fineProps props, String id){
      super(props, id);
      this.statusFine = fineStatus.PENDING;
   }

  public static Fine create(fineProps props, String id) {
     return new Fine(props, id);
  }

  public static Fine addFine(String borrowId, String userId, int amount, String idFine ){
     final fineProps props = new fineProps() {};
     props.borrowId = borrowId;
     props.amount = amount;
     return new Fine(props, idFine);
  }

  public int readAmount() {
     return this.getProps().amount;
  }

   public fineStatus readStatusFine() {
       return this.statusFine;
   }

   public String readBorrowId() {
       return this.getProps().borrowId;
   }

   public void changeStatusToPaid() {
       this.statusFine = fineStatus.PAID;
   }
}
