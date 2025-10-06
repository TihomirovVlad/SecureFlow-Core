package services;

import model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(String login);
    void updateUserLogin(Long userId, String login);
    void deleteUser(Long id);
    Optional<User> findUserById(Long id);
    Optional<User> findUserByLogin(String login);
    List<User> findAllUsers();
}
