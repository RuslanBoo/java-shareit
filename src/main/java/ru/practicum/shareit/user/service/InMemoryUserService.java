package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.model.ConflictException;
import ru.practicum.shareit.error.model.DataNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InMemoryUserService implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    @Override
    public UserDto getById(Long userId) {
        return userMapper.toDto(findById(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return userRepository.getAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto add(UserDto userDto) {
        User user = prepareDao(userDto);
        Long newUserId = userRepository.add(user);

        return prepareDto(userRepository.getById(newUserId));
    }

    @Override
    @Transactional
    public UserDto update(Long userId, UserDto userDto) {
        if (userDto.getEmail() != null && isEmailExist(userDto.getEmail())) {
            throw new ConflictException("User email already exist");
        }
        partialUpdate(userId, userDto);

        return prepareDto(userRepository.getById(userId));
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        findById(userId);
        userRepository.delete(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long userId) {
        User user = userRepository.getById(userId);
        if (user == null) {
            throw new DataNotFoundException("User not found");
        }

        return user;
    }

    private void partialUpdate(Long userId, UserDto userDto) {
        User updatedUser = findById(userId);
        userMapper.updateUser(userDto, updatedUser);

        userRepository.update(userId, updatedUser);
    }

    private boolean isEmailExist(String email) {
        return userRepository.getByEmail(email).isPresent();
    }

    private User prepareDao(UserDto userDto) {
        if (isEmailExist(userDto.getEmail())) {
            throw new ConflictException("User email already exist");
        }
        return userMapper.fromDto(userDto);
    }

    private UserDto prepareDto(User user) {
        return userMapper.toDto(user);
    }
}
