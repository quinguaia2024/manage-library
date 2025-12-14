package model.user;


enum userAccessStatus {
    ADMIN,
    READER,
    LIBRARIAN
}


abstract class userProps{
    String firstName;
    String lastName;
    userAccessStatus accessStatus;
    String email;
    String password;
    String phoneNumber;
    String address;
}


public class user extends model.core.entity<userProps> {
    private user(userProps props, String id) {
        super(props, id);
    }

    public static user create(userProps props, String id) {
        return new user(props, id);
    }

    String getFullName() {
        return this.getProps().firstName + " " + this.getProps().lastName;
    }

    String getEmail() {
        return this.getProps().email;
    }

    String getPhoneNumber() {
        return this.getProps().phoneNumber;
    }

    String getAddress() {
        return this.getProps().address;
    }

    String getFirstName() {
        return this.getProps().firstName;
    }

    String getLastName() {
        return this.getProps().lastName;
    }
    
    userAccessStatus getAccessStatus() {
        return this.getProps().accessStatus;
    }   
    
}
