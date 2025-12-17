package model.application.user;

import model.user.User;

public class FindUsers {
    private model.user.UserRepo userRepository;
    public FindUsers(model.user.UserRepo userRepository) {
        this.userRepository = userRepository;
    }

    public User[] execute() {
       User[] usersFound =  this.userRepository.findMany();
       return usersFound;
    }
}
