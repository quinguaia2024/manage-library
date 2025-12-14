package model.user.usecase;

import model.user.user;

public class FindUsers {
    private model.user.userRepo userRepository;
    public findUsers(model.user.userRepo userRepository) {
        this.userRepository = userRepository;
    }

    user[] execute() {
       user[] usersFound =  this.userRepository.findMany();
       return usersFound;
    }
}
