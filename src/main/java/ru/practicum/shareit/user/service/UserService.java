package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto getById(Long userId);

    List<UserDto> getAll();

    UserDto add(UserDto userDto);

    UserDto update(Long userId, UserDto userDto);

    void delete(Long userId);

    User findById(Long userId);
}
