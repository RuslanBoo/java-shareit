package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.testUtils.Helper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {
    private final UserMapperImpl userMapper = new UserMapperImpl();

    @Test
    void testToDto_shouldReturnUserDto() {
        long userId = 0L;
        User user = Helper.createUser(userId);
        UserDto userDto = UserDto.builder()
                .id(userId)
                .email(user.getEmail())
                .name(user.getName())
                .build();

        assertEquals(userDto, userMapper.toDto(user));
    }

    @Test
    void testToDto_shouldReturnNull() {
        assertNull(userMapper.toDto(null));
    }

    @Test
    void testFromDto_shouldReturnUser() {
        long userId = 0L;
        User user = Helper.createUser(userId);
        UserDto userDto = UserDto.builder()
                .id(userId)
                .email(user.getEmail())
                .name(user.getName())
                .build();

        assertEquals(user, userMapper.fromDto(userDto));
    }

    @Test
    void testFromDto_shouldReturnNull() {
        assertNull(userMapper.fromDto(null));
    }

    @Test
    void testUpdateUser_shouldReturnNonChangedUser() {
        User user = Helper.createUser(1L);
        userMapper.updateUser(null, user);

        assertEquals(user, user);
    }

    @Test
    void testUpdateUser_shouldReturnChangedUser() {
        User user = Helper.createUser(1L);
        UserDto userDto = UserDto.builder()
                .name("new name")
                .email("new email")
                .build();

        userMapper.updateUser(userDto, user);

        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }
}