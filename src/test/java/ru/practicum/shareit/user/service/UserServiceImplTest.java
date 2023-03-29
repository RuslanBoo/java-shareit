package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
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

    private long userId;
    private User user;
    private UserDto userDto;
    private List<User> emptyList;
    private List<UserDto> emptyDtoList;


    @BeforeEach
    void setUp() {
        userId = 0L;
        user = Helper.createUser(userId);
        userDto = userMapper.toDto(user);
        emptyList = new ArrayList<>();
        emptyDtoList = new ArrayList<>();
    }

    @Test
    void testGetById_shouldReturnDataNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> userService.getById(userId)
        ).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void testGetById_shouldReturnUserDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThat(userService.getById(userId)).isEqualTo(userDto);
    }

    @Test
    void testGetAll_shouldReturnEmptyList() {
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
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        assertThat(userService.add(userDto)).isEqualTo(userDto);
    }

    @Test
    void testUpdate_shouldReturnDataNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(userId, userDto)).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void testUpdate_shouldUserDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        assertThat(userService.update(userId, userDto)).isEqualTo(userDto);
    }

    @Test
    void testDelete_shouldReturnDataNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> userService.delete(userId));
    }

    @Test
    void testDelete_shouldReturnVoid() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.delete(userId);
        Mockito.verify(userRepository, Mockito.times(1)).delete(user);
    }

    @Test
    void testFindById_shouldReturnDataNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(userId)).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void testFindById_shouldReturnUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThat(userService.findById(userId)).isEqualTo(user);
    }
}