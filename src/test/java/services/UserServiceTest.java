package services;

import model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.UserRepository;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void shouldCreateUserWhenLoginIsUnique(){
        String login = "wrrfdgsd";
        User expectedUser = new User();
        expectedUser.setId(5L);
        expectedUser.setLogin(login);

        when(userRepository.existsByLogin(login)).thenReturn(false);
        when(userRepository.createUser(login)).thenReturn(expectedUser);

        User actualUser = userService.createUser(login);

        assertThat(actualUser).isNotNull();
        assertThat(actualUser.getLogin()).isEqualTo(login);
        assertThat(actualUser.getId()).isEqualTo(expectedUser.getId());

        verify(userRepository).existsByLogin(login);
        verify(userRepository).createUser(login);

    }
}
