package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.model.ConflictException;
import ru.practicum.shareit.error.model.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.TestHelper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void getById_shouldThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getById(1)).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void getById_shouldReturnUser() {
        long id = 1;
        User user = TestHelper.createUser(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        assertThat(userService.getById(id)).isEqualTo(userMapper.toDto(user));
    }

    @Test
    void update_shouldThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getById(1)).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void update_shouldUpdateEmail() {
        long id = 1;
        String newEmail = "newEmail@test.test";

        UserDto dto = UserDto.builder()
                .email(newEmail)
                .build();
        User user = TestHelper.createUser(id);
        User newUser = TestHelper.createUser(id);
        newUser.setEmail(newEmail);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        assertThat(userService.update(id, dto)).isEqualTo(userMapper.toDto(newUser));
    }

    @Test
    void update_shouldThrowDuplicateEmailException() {
        long id = 1;
        String newEmail = "newEmail@test.test";

        UserDto dto = UserDto.builder()
                .email(newEmail)
                .build();
        User user = TestHelper.createUser(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(newEmail)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.update(id, dto)).isInstanceOf(ConflictException.class);
    }

    @Test
    void update_shouldUpdateName() {
        long id = 1;
        String newName = "new name";

        UserDto dto = UserDto.builder()
                .name(newName)
                .build();
        User user = TestHelper.createUser(id);
        User newUser = TestHelper.createUser(id);
        newUser.setName(newName);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        assertThat(userService.update(id, dto)).isEqualTo(userMapper.toDto(newUser));
    }

    @Test
    void getAll_shouldReturnListOfUsers() {
        List<User> users = List.of(
                TestHelper.createUser(1),
                TestHelper.createUser(2),
                TestHelper.createUser(3)
        );

        when(userRepository.findAll()).thenReturn(users);

        assertThat(userService.getAll()).isEqualTo(users.stream().map(userMapper::toDto).collect(Collectors.toList()));
    }

    @Test
    void delete_shouldReturnDeletedUser() {
        long userId = 1;
        User user = TestHelper.createUser(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.delete(userId);
    }
}
