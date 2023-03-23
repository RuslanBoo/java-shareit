package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.model.DataNotFoundException;
import ru.practicum.shareit.testUtils.Helper;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void testGetById_shouldReturnDataNotFoundException() {
        long userId = 1L;

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> userService.getById(userId)
        ).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void testGetById_shouldReturnUserDto() {
        long userId = 1L;
        User user = Helper.createUser(userId);
        UserDto userDto = userMapper.toDto(user);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThat(userService.getById(userId)).isEqualTo(userDto);
    }

    @Test
    void testGetAll_shouldReturnEmptyList() {
        List<User> emptyList = new ArrayList<>();
        List<UserDto> emptyDtoList = new ArrayList<>();
        when(userRepository.findAll()).thenReturn(emptyList);

        assertThat(userService.getAll()).isEqualTo(emptyDtoList);
    }

    @Test
    void testGetAll_shouldReturnListOfUserDto() {
        List<User> usersList = List.of(
                Helper.createUser(1),
                Helper.createUser(2),
                Helper.createUser(3)
        );
        List<UserDto> usersDtoList = usersList.stream().map(userMapper::toDto).collect(Collectors.toList());

        when(userRepository.findAll()).thenReturn(usersList);

        assertThat(userService.getAll()).isEqualTo(usersDtoList);
    }

    @Test
    void testAdd_shouldReturnUserDto() {
        long userId = 0L;
        User user = Helper.createUser(userId);
        UserDto userDto = userMapper.toDto(user);

        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        assertThat(userService.add(userDto)).isEqualTo(userDto);
    }

    @Test
    void testUpdate_shouldReturnDataNotFoundException() {
        long userId = 0L;
        User user = Helper.createUser(userId);
        UserDto userDto = userMapper.toDto(user);

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(userId, userDto)).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void testUpdate_shouldUserDto() {
        long userId = 0L;
        User user = Helper.createUser(userId);
        UserDto userDto = userMapper.toDto(user);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        assertThat(userService.update(userId, userDto)).isEqualTo(userDto);
    }

    @Test
    void testDelete_shouldReturnDataNotFoundException() {
        long userId = 0L;

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> userService.delete(userId));
    }

    @Test
    void testDelete_shouldReturnVoid() {
        long userId = 0L;
        User user = Helper.createUser(userId);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.delete(userId);
        Mockito.verify(userRepository, Mockito.times(1)).delete(user);
    }

    @Test
    void testFindById_shouldReturnDataNotFoundException() {
        long userId = 0L;

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(userId)).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void testFindById_shouldReturnUser() {
        long userId = 0L;
        User user = Helper.createUser(userId);
        UserDto userDto = userMapper.toDto(user);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThat(userService.findById(userId)).isEqualTo(user);
    }
}