package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto getById(long userId);

    List<UserDto> getAll();

    UserDto add(UserDto userDto);

    UserDto update(long userId, UserDto userDto);

    void delete(long userId);

    User findById(long userId);
}
