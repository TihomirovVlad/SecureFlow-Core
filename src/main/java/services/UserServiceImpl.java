package services;

import model.User;
import org.springframework.stereotype.Service;
import repository.UserRepository;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String login) {
        if (userRepository.existsByLogin(login)) {
            throw new IllegalArgumentException("User already exists");
        }
        return userRepository.createUser(login);
    }

    @Override
    public void updateUserLogin(Long userId, String login) {
        if (userRepository.findUserById(userId).isEmpty()) {
            throw new IllegalArgumentException("User with id " + userId + " does not exist");
        }
        userRepository.updateUserLogin(userId, login);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteUser(id);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findUserById(id);
    }

    @Override
    public Optional<User> findUserByLogin(String login) {
        return userRepository.findUserByLogin(login);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAllUsers();
    }
}
