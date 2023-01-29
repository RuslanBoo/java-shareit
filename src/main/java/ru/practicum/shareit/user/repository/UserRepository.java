package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Long add(User user);

    void update(Long userId, User user);

    void delete(Long userId);

    User getById(Long userId);

    Optional<User> getByEmail(String email);

    List<User> getAll();
}
