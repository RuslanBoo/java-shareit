package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    long add(User user);

    void update(long userId, User user);

    void delete(long userId);

    User getById(long userId);

    List<User> getAll();
}
