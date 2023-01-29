package ru.practicum.shareit.user.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@Repository
@Primary
public interface InDBUserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByEmail(String email);
}
