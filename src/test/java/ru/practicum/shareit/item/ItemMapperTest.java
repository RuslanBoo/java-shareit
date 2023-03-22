package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.testUtils.Helper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class ItemMapperTest {
    private Mock mock;

    private ItemMapperImpl itemMapper = new ItemMapperImpl();

    @Test
    void toDto_shouldReturnItemDto() {
        long itemId = 0L;
        long userId = 0L;
        User owner = Helper.createUser(userId);
        Item item = Helper.createItem(itemId, owner);
        ItemDto itemDto = ItemDto.builder()
                .id(itemId)
                .name("Test name")
                .description("Test description")
                .requestId(1L)
                .comments(List.of())
                .available(true)
                .build();

        assertEquals(itemDto, itemMapper.toDto(item));
    }

    @Test
    void toDto_shouldReturnNull() {
        assertNull(itemMapper.toDto(null));
    }

    @Test
    void fromDto_shouldReturnItemDto() {
        long itemId = 0L;
        long userId = 0L;
        Item item = Helper.createItem(itemId, null);
        ItemDto itemDto = ItemDto.builder()
                .id(itemId)
                .name("Test name")
                .description("Test description")
                .requestId(1L)
                .comments(List.of())
                .available(true)
                .build();

        assertEquals(item, itemMapper.fromDto(itemDto));
    }

    @Test
    void fromDto_shouldReturnNull() {
        assertNull(itemMapper.fromDto(null));
    }

    @Test
    void updateItem_shouldReturnNonChangedItem() {
        User owner = Helper.createUser(1L);
        Item item = Helper.createItem(1L, owner);
        itemMapper.updateItem(null, item);

        assertEquals(item, item);
    }

    @Test
    void testUpdateItem_shouldReturnChangedItem() {

        User user = Helper.createUser(1L);
        CommentDto commentDto1 = Helper.createCommentDto(1L);
        CommentDto commentDto2 = Helper.createCommentDto(2L);
        ShortBookingDto bookingDto1 = Helper.createShortBookingDto(1L, 1L);
        ShortBookingDto bookingDto2 = Helper.createShortBookingDto(2L, 1L);
        List<CommentDto> comments = new ArrayList<>();
        comments.add(commentDto1);

        Item item = Item.builder()
                .id(1L)
                .name("test name")
                .description("test description")
                .owner(user)
                .available(true)
                .requestId(1L)
                .build();

        comments.add(commentDto2);


        ItemDto itemDto = ItemDto.builder()
                .name("new name")
                .description("new description")
                .comments(comments)
                .lastBooking(bookingDto1)
                .nextBooking(bookingDto2)
                .build();

        itemMapper.updateItem(itemDto, item);

        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getComments(), itemDto.getComments());
    }

}