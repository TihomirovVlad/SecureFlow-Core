package services.Impls;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import repository.UserRepository;
import services.UserService;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String login) {
        if (userRepository.existsByLogin(login)) {
            LOGGER.error("User with login {} already exists", login);
            throw new IllegalArgumentException("User already exists");
        }
        LOGGER.info("Creating new user with login: {}", login);
        User user = userRepository.createUser(login);
        LOGGER.info("Successfully created user with ID: {}", user.getId());
        return user;
    }

    @Override
    public void updateUserLogin(Long userId, String login) {
        if (userRepository.findUserById(userId).isEmpty()) {
            LOGGER.error("User with userId {} and login {} does not exist", userId, login);
            throw new IllegalArgumentException("User with id " + userId + " does not exist");
        }
        LOGGER.info("Updating user {} with login {}", userId, login);
        userRepository.updateUserLogin(userId, login);
        LOGGER.info("User with userId {} has been updated to login {}", userId, login);
    }

    @Override
    public void deleteUser(Long id) {
        LOGGER.info("Deleting user with id {}", id);
        userRepository.deleteUser(id);
        LOGGER.info("User with id {} has been deleted", id);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        LOGGER.info("Finding user with id {}", id);
        Optional<User> user = userRepository.findUserById(id);

        if (user.isPresent()) {
            LOGGER.info("User with id {} has been found: {}", id, user.get().getLogin());
        } else {
            LOGGER.warn("User with id {} not found", id);
        }
        return user;
    }

    @Override
    public Optional<User> findUserByLogin(String login) {
        LOGGER.info("Finding user with login {}", login);
        Optional<User> user = userRepository.findUserByLogin(login);

        if (user.isPresent()) {
            LOGGER.info("User with login {} has been found", login);
        } else {
            LOGGER.warn("User with login {} not found", login);
        }
        return user;
    }

    @Override
    public List<User> findAllUsers() {
        LOGGER.info("Finding all users");
        return userRepository.findAllUsers();
    }
}
