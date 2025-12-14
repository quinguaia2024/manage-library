package model.user.usecase;


import model.user.user;

public class FindDeletedUsers {
    private model.user.userRepo userRepository;

    public findDeletedUsers(model.user.userRepo userRepository) {
        this.userRepository = userRepository;
    }

    user[] execute() {
       user[] usersFound =  this.userRepository.findDeletedUsers();
       return usersFound;
    }
}
