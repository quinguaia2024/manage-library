package model.user;

import model.user.usecase.CreateUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateUserTest {

    @Test
    void execute_should_call_save_on_repository() {
        UserProps props = new UserProps() {{
            firstName = "Ana";
            lastName = "Silva";
            accessStatus = UserAccessStatus.READER;
            email = "ana@example.com";
            password = "secret";
            phoneNumber = "123";
            address = "Rua A";
        }};

        User u = User.create(props, "id-1");

        final boolean[] saved = {false};
        UserRepo repo = new UserRepo() {
            @Override public void save(User user) { saved[0] = true; }
            @Override public User findById(String id) { return null; }
            @Override public User[] findMany() { return new User[0]; }
            @Override public void delete(String id) {}
            @Override public void update(String id, User user) {}
            @Override public User[] findDeletedUsers() { return new User[0]; }
        };

        CreateUser usecase = new CreateUser(repo);
        usecase.execute(u);

        assertTrue(saved[0], "Expected repository.save to be called");
    }
}
