package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.testUtils.Helper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private MockMvc mockMvc;

    @Mock
    private ItemRequestServiceImpl itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .setControllerAdvice(ErrorHandler.class)
                .build();
    }

    @SneakyThrows
    @Test
    void getAllByRequestorId_shouldReturnEmptyList() {
        long userId = 1L;
        List<ItemRequestDto> emptyList = new ArrayList<>();

        when(itemRequestService.getAllByRequestorId(anyLong(), anyInt(), anyInt())).thenReturn(emptyList);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(emptyList)));
    }

    @SneakyThrows
    @Test
    void getById_shouldReturnItemRequestDto() {
        long userId = 1L;
        long requestId = 1L;
        User user = Helper.createUser(1L);
        ItemRequestDto itemRequestDto = Helper.createItemRequestDto(1L, user);

        when(itemRequestService.getById(anyLong(), anyLong())).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId).header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDto)));
    }

    @SneakyThrows
    @Test
    void getAll_shouldReturnEmptyList() {
        long userId = 1L;
        List<ItemRequestDto> emptyList = new ArrayList<>();

        when(itemRequestService.getAll(anyLong(), anyInt(), anyInt())).thenReturn(emptyList);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(emptyList)));
    }

    @SneakyThrows
    @Test
    void add_shouldReturnItemRequestDto() {
        long userId = 1L;
        long requestId = 0L;
        User user = Helper.createUser(1L);
        ItemRequestDto itemRequestDto = Helper.createItemRequestDto(requestId, user);
        String json = objectMapper.writeValueAsString(itemRequestDto);

        when(itemRequestService.add(any(ItemRequestDto.class), anyLong())).thenAnswer(i -> i.getArguments()[0]);

        mockMvc.perform(post("/requests/")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }
}