package model.user;


enum UserAccessStatus {
    ADMIN,
    TEACHER,
    STUDENT,
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
        final UserProps userProps = new UserProps() {};
        userProps.firstName = props.firstName;
        userProps.lastName = props.lastName;
        userProps.accessStatus = props.accessStatus;       
        userProps.email = props.email;
        userProps.password = props.password;
        userProps.phoneNumber = props.phoneNumber;
        userProps.address = props.address;
        return new User(userProps, id);
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
