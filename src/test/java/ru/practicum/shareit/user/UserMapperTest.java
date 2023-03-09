package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.testUtils.Helper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    private Mock mock;

    private UserMapperImpl userMapper = new UserMapperImpl();

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
    void updateUser() {
    }
}