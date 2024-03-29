package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.error.ErrorHandler;
import ru.practicum.shareit.error.model.DataNotFoundException;
import ru.practicum.shareit.testUtils.Helper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

    private long userId;
    private List<UserDto> emptyList;
    private List<UserDto> users;
    private UserDto userDto;
    private String jsonDto;

    @SneakyThrows
    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(ErrorHandler.class)
                .build();

        userId = 1;
        userDto = Helper.createUserDto(userId);
        emptyList = new ArrayList<>();
        users = List.of(
                Helper.createUserDto(1),
                Helper.createUserDto(2),
                Helper.createUserDto(3)
        );
        jsonDto = objectMapper.writeValueAsString(userDto);
    }

    @SneakyThrows
    @Test
    void getAll_shouldReturnEmptyList() {
        when(userService.getAll()).thenReturn(emptyList);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(emptyList)));
    }

    @SneakyThrows
    @Test
    void getAll_shouldReturnListOfUsersDto() {
        when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));
    }

    @SneakyThrows
    @Test
    void getById_shouldDataNotFoundException() {
        when(userService.getById(anyLong())).thenThrow(new DataNotFoundException("User not found"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getById_shouldInternalServerErrorException() {
        mockMvc.perform(get("/users/test"))
                .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @Test
    void create_shouldReturnUserDto() {
        when(userService.add(any(UserDto.class))).thenAnswer(i -> i.getArguments()[0]);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonDto))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonDto));
    }

    @SneakyThrows
    @Test
    void update_shouldReturnDataNotFoundException() {
        when(userService.update(anyLong(), any(UserDto.class))).thenThrow(new DataNotFoundException("User not found"));

        mockMvc.perform(patch("/users/" + userId).contentType(MediaType.APPLICATION_JSON).content(jsonDto))
                .andExpect(status().isNotFound());

    }

    @SneakyThrows
    @Test
    void update_shouldReturnUserDto() {
        when(userService.update(anyLong(), any(UserDto.class))).thenAnswer(i -> i.getArguments()[1]);

        mockMvc.perform(patch("/users/" + userId).contentType(MediaType.APPLICATION_JSON).content(jsonDto))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonDto));
    }

    @SneakyThrows
    @Test
    void delete_shouldReturnDataNotFoundException() {
        doThrow(new DataNotFoundException("User not found")).when(userService).delete(anyLong());

        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void delete_shouldReturnStatusOk() {
        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isOk());
    }
}
