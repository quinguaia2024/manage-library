package model.user.usecase;

import model.user.User;

public class FindUser {
    private model.user.UserRepo userRepository;
    public FindUser(model.user.UserRepo userRepository) {
        this.userRepository = userRepository;
    }

    User execute(String id) {
       User userFound =  this.userRepository.findById(id);
       return userFound;
    }
}