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


    public static User makeUser(String id, String firstName, String lastName, UserAccessStatus accessStatus, String email, String password, String phoneNumber, String address) {
        final UserProps props = new UserProps() {};
        props.firstName = firstName;
        props.lastName = lastName;
        props.accessStatus = accessStatus;
        props.email = email;
        props.password = password;
        props.phoneNumber = phoneNumber;
        props.address = address;
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
