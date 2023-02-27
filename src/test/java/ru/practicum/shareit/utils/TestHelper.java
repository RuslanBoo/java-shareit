package ru.practicum.shareit.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestHelper {
    public static User createUser(long id) {
        return User.builder()
                .id(id)
                .name("Test name")
                .email("test@test.test").build();
    }

    public static Item createItem(long id, boolean available, User user) {
        return Item.builder()
                .id(id)
                .name("Test name")
                .description("Test description")
                .available(available)
                .owner(user)
                .build();
    }

    public static ItemDto createItemDto(boolean available, Long requestId) {
        return ItemDto.builder()
                .name("Test name")
                .description("Test description")
                .available(available)
                .requestId(requestId)
                .build();
    }

    public static ItemRequest createItemRequest(long id, LocalDateTime created, User user) {
        return ItemRequest.builder()
                .id(id)
                .description("Test description")
                .created(created)
                .requestor(user)
                .build();
    }

    public static ItemRequestDto createItemRequestDto(long id) {
        return ItemRequestDto.builder()
                .id(id)
                .description("Test description")
                .created(LocalDateTime.now())
                .build();
    }
}
