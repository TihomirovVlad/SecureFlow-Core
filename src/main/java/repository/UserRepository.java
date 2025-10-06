package repository;

import model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User createUser(String login);
    void updateUserLogin(Long userId, String newLogin);
    void deleteUser(Long id);
    Optional<User> findUserById(Long id);
    List<User> findAllUsers();
    Optional<User> findUserByLogin(String login);
    boolean existsByLogin(String login);
}
