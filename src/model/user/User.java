package model.user;


enum UserAccessStatus {
    ADMIN,
    READER,
    LIBRARIAN
}


abstract class UserProps {
    String firstName;
    String lastName;
    UserAccessStatus accessStatus;
    String email;
    String password;
    String phoneNumber;
    String address;
}


public class User extends model.core.Entity<UserProps> {
    private User(UserProps props, String id) {
        super(props, id);
    }

    public static User create(UserProps props, String id) {
        return new User(props, id);
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
    
    UserAccessStatus getAccessStatus() {
        return this.getProps().accessStatus;
    }   
    
}
