package model.user.usecase;

import model.user.user;

public class FindUser {
    private model.user.userRepo userRepository;
    public findUser(model.user.userRepo userRepository) {
        this.userRepository = userRepository;
    }

    user execute(String id) {
       user userFound =  this.userRepository.findById(id);
       return userFound;
    }
}