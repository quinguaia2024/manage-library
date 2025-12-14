package model.user;

public abstract class UserRepo implements model.core.Irepository<User> {
   public abstract void save(User user);
   public abstract User findById(String id);
   public abstract User[] findMany();
   public abstract void delete(String id);
   public abstract void update(String id, User user);
   public abstract User[] findDeletedUsers();
}
