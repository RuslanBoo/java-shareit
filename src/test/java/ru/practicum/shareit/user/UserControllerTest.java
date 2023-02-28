package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.error.ErrorHandler;
import ru.practicum.shareit.error.model.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.utils.TestHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private MockMvc mockMvc;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private UserController userController;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(ErrorHandler.class)
                .build();
    }

    @Test
    void getAll_shouldReturnListOfUsers() throws Exception {
        List<UserDto> users = Stream.of(
                TestHelper.createUser(1),
                TestHelper.createUser(2),
                TestHelper.createUser(3)
        ).map(userMapper::toDto).collect(Collectors.toList());

        when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));
    }

    @Test
    void getAll_shouldReturnEmptyList() throws Exception {
        List<User> users = Collections.emptyList();

        when(userService.getAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));
    }

    @Test
    void getById_shouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/users/abc"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getById_shouldReturnNotFound() throws Exception {
        long userId = 1;

        when(userService.getById(userId)).thenThrow(new DataNotFoundException("User not found"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_shouldReturnUser() throws Exception {
        long userId = 1;
        UserDto userDto = userMapper.toDto(TestHelper.createUser(userId));

        when(userService.getById(userId)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
    }

    @Test
    void create_shouldCreateUser() throws Exception {
        long userId = 1;
        User user = TestHelper.createUser(userId);
        UserDto dto = UserDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
        String json = objectMapper.writeValueAsString(dto);

        when(userService.add(dto)).thenReturn(userMapper.toDto(user));

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    void update_shouldUpdateUser() throws Exception {
        long userId = 1;
        User user = TestHelper.createUser(userId);
        UserDto dto = UserDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
        String json = objectMapper.writeValueAsString(dto);

        when(userService.update(userId, dto)).thenReturn(userMapper.toDto(user));

        mockMvc.perform(patch("/users/" + userId).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    void update_shouldReturnNotFound() throws Exception {
        long userId = 1;
        User user = TestHelper.createUser(userId);
        UserDto dto = UserDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
        String json = objectMapper.writeValueAsString(dto);

        when(userService.update(userId, dto)).thenThrow(new DataNotFoundException("User not found"));

        mockMvc.perform(patch("/users/" + userId).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldReturnNotFound() throws Exception {
        long userId = 1;

        doThrow(new DataNotFoundException("User not found")).when(userService).delete(userId);

        mockMvc.perform(delete("/users/" + userId)).andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldReturnDeletedUser() throws Exception {
        long userId = 1;

        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isOk());
    }
}
