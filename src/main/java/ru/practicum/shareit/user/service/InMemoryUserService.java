package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.model.BadRequestException;
import ru.practicum.shareit.error.model.ConflictException;
import ru.practicum.shareit.error.model.DataNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InMemoryUserService implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getById(long userId) {
        return userMapper.toDto(findById(userId));
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto add(UserDto userDto) {
        User user = prepareDao(userDto, true);
        long newUserId = userRepository.add(user);

        return userMapper.toDto(userRepository.getById(newUserId));
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        User user = prepareDao(userDto, false);
        partialUpdate(userId, user);

        return userMapper.toDto(userRepository.getById(userId));
    }

    @Override
    public void delete(long userId) {
        findById(userId);
        userRepository.delete(userId);
    }

    @Override
    public User findById(long userId) {
        User user = userRepository.getById(userId);
        if (user == null) {
            throw new DataNotFoundException("User not found");
        }

        return user;
    }

    private User prepareDao(UserDto userDto, boolean isCreating) {
        if (isCreating) {
            if (userDto.getName() == null || userDto.getName().isBlank()) {
                throw new BadRequestException("Invalid user name");
            }
            if (userDto.getEmail() == null || isEmailInvalid(userDto.getEmail())) {
                throw new BadRequestException("Invalid user email");
            }
        } else {
            if (userDto.getName() != null && userDto.getName().isBlank()) {
                throw new BadRequestException("Invalid user name");
            }
            if (userDto.getEmail() != null && isEmailInvalid(userDto.getEmail())) {
                throw new BadRequestException("Invalid user email");
            }
        }
        if (isEmailExist(userDto.getEmail())) {
            throw new ConflictException("User email already exist");
        }
        return userMapper.fromDto(userDto);
    }

    private void partialUpdate(long userId, User user) {
        User updatedUser = findById(userId);

        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }

        userRepository.update(userId, updatedUser);
    }

    private boolean isEmailInvalid(String email) {
        final String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        return !Pattern.compile(regex).matcher(email).matches();
    }

    private boolean isEmailExist(String email) {
        return userRepository.getAll()
                .stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
}
