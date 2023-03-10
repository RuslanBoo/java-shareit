package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping()
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @GetMapping(path = "/{userId}", name = "userId")
    public UserDto getById(@PathVariable long userId) {
        return userService.getById(userId);
    }

    @PostMapping
    public UserDto create(@Validated(CreateUserDto.class) @RequestBody UserDto userDto) {
        return userService.add(userDto);
    }

    @PatchMapping(path = "/{userId}", name = "userId")
    public UserDto update(@PathVariable long userId,
                          @Valid @RequestBody UserDto userDto) {
        return userService.update(userId, userDto);
    }

    @DeleteMapping(path = "/{userId}", name = "userId")
    public void delete(@PathVariable long userId) {
        userService.delete(userId);
    }
}
