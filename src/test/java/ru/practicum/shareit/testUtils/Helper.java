package ru.practicum.shareit.testUtils;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Component
public class Helper {

    public static UserDto createUserDto(long id){
        return UserDto.builder()
                .id(id)
                .name("Test name" + id)
                .email("Test@email" + id)
                .build();
    }

    public static User createUser(long id){
        return User.builder()
                .id(id)
                .name("Test name" + id)
                .email("Test@email" + id)
                .build();
    }
}
