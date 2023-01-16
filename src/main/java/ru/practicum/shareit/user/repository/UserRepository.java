package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    long add(User user);

    void update(long userId, User user);

    void delete(long userId);

    User getById(long userId);

    Optional<User> getByEmail(String email);

    List<User> getAll();
}
