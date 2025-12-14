package model.user.usecase;


import model.user.User;

public class FindDeletedUsers {
    private model.user.UserRepo userRepository;

    public FindDeletedUsers(model.user.UserRepo userRepository) {
        this.userRepository = userRepository;
    }

    User[] execute() {
       User[] usersFound =  this.userRepository.findDeletedUsers();
       return usersFound;
    }
}
