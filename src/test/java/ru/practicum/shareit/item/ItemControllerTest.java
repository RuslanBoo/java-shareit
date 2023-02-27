package ru.practicum.shareit.item;

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
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.error.ErrorHandler;
import ru.practicum.shareit.error.model.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.TestHelper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private MockMvc mockMvc;

    @Mock
    private ItemService itemService;

    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @InjectMocks
    private ItemController itemController;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .setControllerAdvice(ErrorHandler.class)
                .build();
    }

    @Test
    void getByUserId_shouldReturnListOfItems() throws Exception {
        long userId = 1;
        List<ItemDto> items = Stream.of(
                TestHelper.createItem(1, true, null),
                TestHelper.createItem(2, true, null),
                TestHelper.createItem(3, true, null)
        ).map(itemMapper::toDto).collect(Collectors.toList());

        when(itemService.getByOwner(anyLong())).thenReturn(items);

        mockMvc.perform(get("/items").header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
    }

    @Test
    void getByUserId_shouldReturnNotFound() throws Exception {
        long userId = 1;

        when(itemService.getByOwner(anyLong())).thenThrow(new DataNotFoundException("User not found"));

        mockMvc.perform(get("/items").header(USER_ID_HEADER, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByUserId_shouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/items")).andExpect(status().isInternalServerError());
    }

    @Test
    void search_shouldReturnListOfItems() throws Exception {
        List<ItemDto> items = Stream.of(
                TestHelper.createItem(1, true, null),
                TestHelper.createItem(2, true, null),
                TestHelper.createItem(3, true, null)
        ).map(itemMapper::toDto).collect(Collectors.toList());

        when(itemService.search(anyString())).thenReturn(items);

        mockMvc.perform(get("/items/search").queryParam("text", "test"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
    }

    @Test
    void getById_shouldReturnItem() throws Exception {
        long itemId = 1;
        long userId = 1;
        ItemDto item = itemMapper.toDto(TestHelper.createItem(itemId, true, null));

        when(itemService.getById(itemId, userId)).thenReturn(item);

        mockMvc.perform(get("/items/" + itemId).header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(item)));
    }

    @Test
    void getById_shouldReturnNotFound() throws Exception {
        long itemId = 1;
        long userId = 1;

        when(itemService.getById(itemId, userId)).thenThrow(new DataNotFoundException("Item not found"));

        mockMvc.perform(get("/items/" + itemId).header(USER_ID_HEADER, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldCreateAndReturnNewItem() throws Exception {
        long itemId = 1;
        long userId = 1;
        ItemDto item = itemMapper.toDto(TestHelper.createItem(itemId, true, null));
        ItemDto dto = TestHelper.createItemDto(true, 1L);
        String json = objectMapper.writeValueAsString(dto);

        when(itemService.add(dto, userId)).thenReturn(item);

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(item)));
    }

    @Test
    void update_shouldUpdateItem() throws Exception {
        long itemId = 1;
        long userId = 1;
        ItemDto item = itemMapper.toDto(TestHelper.createItem(itemId, true, null));
        ItemDto dto = ItemDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        String json = objectMapper.writeValueAsString(dto);

        when(itemService.update(itemId, dto, userId)).thenReturn(item);

        mockMvc.perform(patch("/items/" + itemId)
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(item)));
    }

    @Test
    void update_shouldReturnNotFound() throws Exception {
        long itemId = 1;
        long userId = 1;
        ItemDto item = itemMapper.toDto(TestHelper.createItem(itemId, true, null));
        ItemDto dto = ItemDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        String json = objectMapper.writeValueAsString(dto);

        when(itemService.update(itemId, dto, userId)).thenThrow(new DataNotFoundException("Item not found"));

        mockMvc.perform(patch("/items/" + itemId)
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldReturnDeletedItem() throws Exception {
        long itemId = 1;
        ItemDto item = itemMapper.toDto(TestHelper.createItem(itemId, true, null));

        mockMvc.perform(delete("/items/" + itemId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(item)));
    }

    @Test
    void delete_shouldReturnNotFound() throws Exception {
        long itemId = 1;

        doThrow(new DataNotFoundException("Item not found")).when(itemService).delete(itemId, 1L);

        mockMvc.perform(delete("/items/" + itemId))
                .andExpect(status().isNotFound());
    }

    @Test
    void comment_shouldReturnNotFound() throws Exception {
        long itemId = 1;
        long userId = 1;
        CommentDto dto = CommentDto.builder()
                .text("text")
                .build();
        CommentDto comment = CommentDto.builder()
                .id(1)
                .authorName("test name")
                .text("comment")
                .created(LocalDateTime.now())
                .build();
        String json = objectMapper.writeValueAsString(dto);

        when(itemService.commentSave(itemId, userId, dto)).thenReturn(comment);

        mockMvc.perform(post("/items/" + itemId + "/comment")
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(comment)));
    }
}
