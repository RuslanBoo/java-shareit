package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.model.ConflictException;
import ru.practicum.shareit.error.model.DataNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.InDBUserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class InDBUserService implements UserService {
    private final InDBUserRepository inDBUserRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getById(Long userId) {
        return prepareDto(findById(userId));
    }

    @Override
    public List<UserDto> getAll() {
        return inDBUserRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto add(UserDto userDto) {
        User newUser = inDBUserRepository.save(prepareDao(userDto));
        return prepareDto(newUser);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        checkEmailOwner(userId, userDto.getEmail());

        User updatedUser = userMapper.fromDto(getById(userId));
        userMapper.updateUser(userDto, updatedUser);
        updatedUser.setId(userId);

        User newUser = inDBUserRepository.save(updatedUser);

        return prepareDto(newUser);
    }

    @Override
    public void delete(Long userId) {
        inDBUserRepository.deleteById(userId);
    }

    @Override
    public User findById(Long userId) {
        Optional<User> user = inDBUserRepository.findById(userId);
        if (user.isEmpty()) {
            throw new DataNotFoundException("User not found");
        }

        return user.get();
    }

    private boolean isEmailExist(String email) {
        return inDBUserRepository.findByEmail(email).isPresent();
    }

    private void checkEmailOwner(Long userId, String email) {
        Optional<User> user = inDBUserRepository.findByEmail(email);
        if (user.isPresent() && user.get().getId() != userId) {
            throw new ConflictException("User email already exist");
        }
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
