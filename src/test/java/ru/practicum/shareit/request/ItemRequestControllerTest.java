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

    private long userId;
    long requestId;
    private User user;
    private List<ItemRequestDto> emptyList;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .setControllerAdvice(ErrorHandler.class)
                .build();

        userId = 1L;
        user = Helper.createUser(1L);
        itemRequestDto = Helper.createItemRequestDto(1L, user);
        emptyList = new ArrayList<>();
    }

    @SneakyThrows
    @Test
    void getAllByRequestorId_shouldReturnEmptyList() {
        when(itemRequestService.getAllByRequestorId(anyLong(), anyInt(), anyInt())).thenReturn(emptyList);

        mockMvc.perform(get("/requests")
                        .header(Helper.HEADER_USER_ID, userId)
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(emptyList)));
    }

    @SneakyThrows
    @Test
    void getById_shouldReturnItemRequestDto() {
        when(itemRequestService.getById(anyLong(), anyLong())).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId).header(Helper.HEADER_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDto)));
    }

    @SneakyThrows
    @Test
    void getAll_shouldReturnEmptyList() {
        when(itemRequestService.getAll(anyLong(), anyInt(), anyInt())).thenReturn(emptyList);

        mockMvc.perform(get("/requests/all")
                        .header(Helper.HEADER_USER_ID, userId)
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(emptyList)));
    }

    @SneakyThrows
    @Test
    void add_shouldReturnItemRequestDto() {
        String json = objectMapper.writeValueAsString(itemRequestDto);

        when(itemRequestService.add(any(ItemRequestDto.class), anyLong())).thenAnswer(i -> i.getArguments()[0]);

        mockMvc.perform(post("/requests/")
                        .header(Helper.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }
}