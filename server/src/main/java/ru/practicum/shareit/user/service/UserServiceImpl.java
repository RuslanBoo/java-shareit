package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.model.DataNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getById(long userId) {
        return userMapper.toDto(findById(userId));
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto add(UserDto userDto) {
        User user = prepareDao(userDto);

        return prepareDto(userRepository.save(user));
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        partialUpdate(userId, userDto);

        return prepareDto(findById(userId));
    }

    @Override
    public void delete(long userId) {
        User user = findById(userId);
        userRepository.delete(user);
    }

    @Override
    public User findById(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new DataNotFoundException("User not found");
        }

        return user.get();
    }

    private void partialUpdate(long userId, UserDto userDto) {
        User updatedUser = findById(userId);
        userMapper.updateUser(userDto, updatedUser);
        userRepository.save(updatedUser);
    }

    private boolean isEmailExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private User prepareDao(UserDto userDto) {
        return userMapper.fromDto(userDto);
    }

    private UserDto prepareDto(User user) {
        return userMapper.toDto(user);
    }
}
