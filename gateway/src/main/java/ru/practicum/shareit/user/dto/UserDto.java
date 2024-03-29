package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UserDto {
    private long id;

    @NotNull(message = "Empty user name", groups = CreateUserDto.class)
    @NotBlank(message = "Invalid user name", groups = {CreateUserDto.class, UpdateUserDto.class})
    private String name;

    @NotNull(message = "Empty user name", groups = CreateUserDto.class)
    @Email(message = "Invalid user email", groups = {CreateUserDto.class, UpdateUserDto.class})
    private String email;
}
