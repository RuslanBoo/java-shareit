package ru.practicum.shareit.user;

import org.mapstruct.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    User fromDto(UserDto userDto);

    @InheritConfiguration
    void updateUser(UserDto userDto, @MappingTarget User user);
}
