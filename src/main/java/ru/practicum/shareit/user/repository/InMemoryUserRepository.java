package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {
    private long nextId = 1;
    private final Map<Long, User> users;

    @Override
    public long add(User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        return user.getId();
    }

    @Override
    public void update(long userId, User user) {
        delete(userId);
        users.put(userId, user);
    }

    @Override
    public void delete(long userId) {
        users.remove(userId);
    }

    @Override
    public User getById(long userId) {
        return users.get(userId);
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return users.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }
}
