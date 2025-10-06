package repository;

import model.Account;
import model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User createUser(String login);
    void updateUserLogin(User user);
    void deleteUser(Long id);
    Optional<User> findUserById(Long id);
    List<User> findAllUsers();
    boolean existsByLogin(String login);
}
