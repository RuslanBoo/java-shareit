package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.testUtils.Helper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {
    private final UserMapperImpl userMapper = new UserMapperImpl();
    private long userId;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userId = 0L;
        user = Helper.createUser(userId);
        userDto = UserDto.builder()
                .id(userId)
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    @Test
    void testToDto_shouldReturnUserDto() {
        assertEquals(userDto, userMapper.toDto(user));
    }

    @Test
    void testToDto_shouldReturnNull() {
        assertNull(userMapper.toDto(null));
    }

    @Test
    void testFromDto_shouldReturnUser() {
        assertEquals(user, userMapper.fromDto(userDto));
    }

    @Test
    void testFromDto_shouldReturnNull() {
        assertNull(userMapper.fromDto(null));
    }

    @Test
    void testUpdateUser_shouldReturnNonChangedUser() {
        userMapper.updateUser(null, user);

        assertEquals(user, user);
    }

    @Test
    void testUpdateUser_shouldReturnChangedUser() {
        userMapper.updateUser(userDto, user);

        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }
}