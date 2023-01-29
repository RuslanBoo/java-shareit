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
    private Long nextId = 1L;
    private final Map<Long, User> users;

    @Override
    public Long add(User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        return user.getId();
    }

    @Override
    public void update(Long userId, User user) {
        delete(userId);
        users.put(userId, user);
    }

    @Override
    public void delete(Long userId) {
        users.remove(userId);
    }

    @Override
    public User getById(Long userId) {
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
