package model.user.usecase;

import model.user.User;
import model.user.UserRepo;

public class CreateUser {
    private UserRepo userRepository;

    public CreateUser(UserRepo userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(User user) {
        this.userRepository.save(user);
    }
}
