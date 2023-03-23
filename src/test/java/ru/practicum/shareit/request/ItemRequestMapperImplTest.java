package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.testUtils.Helper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ItemRequestMapperImplTest {
    private final ItemRequestMapperImpl itemRequestMapper = new ItemRequestMapperImpl();

    @Test
    void toDto_shouldReturnItemRequestDto() {
        long itemRequestId = 0L;
        long userId = 0L;
        LocalDateTime date = LocalDateTime.now();
        User user = Helper.createUser(userId);
        ItemRequest itemRequest = Helper.createItemRequest(itemRequestId, user);
        itemRequest.setCreated(date);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(itemRequestId)
                .created(date)
                .description(itemRequest.getDescription())
                .build();

        assertEquals(itemRequestMapper.toDto(itemRequest), itemRequestDto);
    }

    @Test
    void toDto_shouldReturnNull() {
        assertNull(itemRequestMapper.toDto(null));
    }

    @Test
    void fromDto_shouldReturnItemRequestDto() {
        long itemRequestId = 0L;
        LocalDateTime date = LocalDateTime.now();
        ItemRequest itemRequest = Helper.createItemRequest(itemRequestId, null);
        itemRequest.setCreated(date);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(itemRequestId)
                .created(date)
                .description(itemRequest.getDescription())
                .build();

        assertEquals(itemRequestMapper.fromDto(itemRequestDto), itemRequest);
    }

    @Test
    void fromDto_shouldReturnNull() {
        assertNull(itemRequestMapper.fromDto(null));
    }
}