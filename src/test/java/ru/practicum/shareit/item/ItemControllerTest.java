package ru.practicum.shareit.item;

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
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.error.ErrorHandler;
import ru.practicum.shareit.error.model.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.testUtils.Helper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private MockMvc mockMvc;

    @Mock
    private ItemServiceImpl itemService;

    @InjectMocks
    private ItemController itemController;

    private long commentId;
    private long userId;
    private CommentDto commentDto;
    private String jsonDto;
    private ItemDto itemDto;
    private List<UserDto> emptyList;
    private List<ItemDto> items;

    @SneakyThrows
    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .setControllerAdvice(ErrorHandler.class)
                .build();

        commentId = 1L;
        userId = 1L;
        commentDto = Helper.createCommentDto(commentId);
        jsonDto = objectMapper.writeValueAsString(commentDto);
        emptyList = new ArrayList<>();
        items = List.of(
                Helper.createItemDto(1L, 1L),
                Helper.createItemDto(2L, 1L),
                Helper.createItemDto(3L, 1L)
        );
        itemDto = Helper.createItemDto(1L, 1L);
    }

    @SneakyThrows
    @Test
    void getByOwner_shouldReturnEmptyList() {
        when(itemService.getByOwner(anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/items").header(Helper.HEADER_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(emptyList)));
    }

    @SneakyThrows
    @Test
    void getByOwner_shouldReturnListOfItemDto() {
        when(itemService.getByOwner(anyLong())).thenReturn(items);

        mockMvc.perform(get("/items").header(Helper.HEADER_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
    }

    @SneakyThrows
    @Test
    void getById_shouldDataNotFoundException() {
        when(itemService.getById(anyLong(), anyLong())).thenThrow(DataNotFoundException.class);

        mockMvc.perform(get("/items/{itemId}", 1L).header(Helper.HEADER_USER_ID, userId))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getById__shouldInternalServerErrorException() {
        mockMvc.perform(get("/items/test"))
                .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @Test
    void getById() {
        when(itemService.getById(anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", 1L).header(Helper.HEADER_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));
    }

    @SneakyThrows
    @Test
    void search_shouldReturnListOfItemDto() {
        String query = "test";

        when(itemService.search(anyString())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search").param("text", query))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemDto))));
    }

    @SneakyThrows
    @Test
    void add_shouldReturnBadRequestException() {
        mockMvc.perform(post("/items")
                        .header(Helper.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDto)
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_shouldReturnItemDto() {
        ItemDto itemDto2 = Helper.createItemDto(0L, 1L);
        String jsonDto2 = objectMapper.writeValueAsString(itemDto2);

        when(itemService.add(any(ItemDto.class), anyLong())).thenAnswer(i -> i.getArguments()[0]);

        mockMvc.perform(post("/items")
                        .header(Helper.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDto2)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(jsonDto2));
    }

    @SneakyThrows
    @Test
    void update_shouldReturnDataNotFoundException() {
        when(itemService.update(anyLong(), any(ItemDto.class), anyLong())).thenThrow(new DataNotFoundException("Item not found"));

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header(Helper.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDto)
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void update_shouldReturnItemDto() {
        ItemDto itemDto = Helper.createItemDto(0L, 1L);
        String jsonDto = objectMapper.writeValueAsString(itemDto);

        when(itemService.update(anyLong(), any(ItemDto.class), anyLong())).thenAnswer(i -> i.getArguments()[1]);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header(Helper.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDto)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(jsonDto));
    }

    @SneakyThrows
    @Test
    void delete_shouldReturnNotFoundException() {
        doThrow(new DataNotFoundException("Item not found")).when(itemService).delete(anyLong(), anyLong());

        mockMvc.perform(delete("/items/{itemId}", 1L)
                        .header(Helper.HEADER_USER_ID, userId)
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void delete_shouldReturnStatusOk() {
        mockMvc.perform(delete("/items/{itemId}", 1L)
                        .header(Helper.HEADER_USER_ID, userId)
                )
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void comment_shouldReturnCommentDto() {
        when(itemService.commentSave(anyLong(), anyLong(), any(CommentDto.class))).thenAnswer(i -> i.getArguments()[2]);

        mockMvc.perform(post("/items/{id}/comment", 1L)
                        .header(Helper.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDto)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(jsonDto));
    }
}