package repository;

import model.Account;
import model.User;

import java.util.List;

public interface UserRepository {
    void createUser(User user);
    void updateUser(User user);
    void deleteUser(Long id);
    User findUserById(Long id);
    List<User> findAllUsers();
    List<Account> findAccountsByUserId(Long userId);
}
