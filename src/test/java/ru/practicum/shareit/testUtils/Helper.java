package ru.practicum.shareit.testUtils;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Component
public class Helper {
    private static final LocalDateTime CREATE = LocalDateTime.of(2022, 12, 10, 00, 00, 00);
    private static final LocalDateTime START = LocalDateTime.of(2023, 4, 11, 00, 00, 00);
    private static final LocalDateTime END = LocalDateTime.of(2023, 5, 12, 00, 00, 00);

    public static UserDto createUserDto(long id) {
        return UserDto.builder()
                .id(id)
                .name("Test name" + id)
                .email("Test@email" + id)
                .build();
    }

    public static User createUser(long id) {
        return User.builder()
                .id(id)
                .name("Test name" + id)
                .email("Test@email" + id)
                .build();
    }

    public static Item createItem(long itemId, User user) {
        return Item.builder()
                .id(itemId)
                .name("Test name")
                .description("Test description")
                .owner(user)
                .requestId(1L)
                .comments(List.of())
                .available(true)
                .build();
    }

    public static ItemDto createItemDto(long itemId, long userId) {
        return ItemDto.builder()
                .id(itemId)
                .name("Test name")
                .description("Test description")
                .requestId(1L)
                .available(true)
                .build();
    }

    public static Comment createComment(long id, Item item, User user) {
        return Comment.builder()
                .id(id)
                .text("Test")
                .author(user)
                .item(item)
                .created(CREATE)
                .build();
    }

    public static CommentDto createCommentDto(long id) {
        return CommentDto.builder()
                .id(id)
                .text("Test")
                .created(CREATE)
                .build();
    }

    public static Booking createBokking(long id, Item item, User user) {
        return Booking.builder()
                .id(id)
                .item(item)
                .status(BookingStatus.WAITING)
                .start(START)
                .end(END)
                .booker(user)
                .build();
    }

    public static BookingDto createBookingDto(long id, long itemId, User user) {
        return BookingDto.builder()
                .id(id)
                .itemId(itemId)
                .status(BookingStatus.WAITING)
                .start(START)
                .end(END)
                .booker(user)
                .build();
    }

    public static ShortBookingDto createShortBookingDto(long id, long bookerId) {
        return ShortBookingDto.builder()
                .id(id)
                .bookerId(bookerId)
                .build();
    }

    public static ItemRequest createItemRequest(long itemRequestId, User user) {
        return ItemRequest.builder()
                .id(itemRequestId)
                .created(LocalDateTime.now())
                .description("test")
                .requestor(user)
                .build();
    }

    public static ItemRequestDto createItemRequestDto(long itemRequestId, User user) {
        return ItemRequestDto.builder()
                .id(itemRequestId)
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .description("test")
                .build();
    }
}
