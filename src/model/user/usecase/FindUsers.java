package model.user.usecase;

import model.user.User;

public class FindUsers {
    private model.user.UserRepo userRepository;
    public FindUsers(model.user.UserRepo userRepository) {
        this.UserRepository = userRepository;
    }

    public User[] execute() {
       User[] usersFound =  this.UserRepository.findMany();
       return usersFound;
    }
}
