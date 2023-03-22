package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.error.model.BadRequestException;
import ru.practicum.shareit.error.model.DataNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.testUtils.Helper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    private Mock mock;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void getById_shouldThrowDataNotFoundException() {
        long userId = 1L;
        long itemId = 1L;

        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> itemService.getById(itemId, userId)
        ).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void getById_shouldReturnItemDto() {
        long userId = 1L;
        long itemId = 1L;
        User owner = Helper.createUser(userId);
        Item item = Helper.createItem(itemId, owner);
        ItemDto itemDto = itemMapper.toDto(item);
        User user = Helper.createUser(userId);
        item.setOwner(user);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndStatusNotAndStartAfterOrderByStartAsc(
                anyLong(),
                any(BookingStatus.class),
                any(LocalDateTime.class))
        ).thenReturn(Optional.empty());
        when(bookingRepository.findFirstByItemIdAndStatusNotAndStartBeforeOrderByStartDesc(
                anyLong(),
                any(BookingStatus.class),
                any(LocalDateTime.class))
        ).thenReturn(Optional.empty());
        when(commentRepository.findAllByItemId(
                anyLong())
        ).thenReturn(List.of());

        ItemDto result = itemService.getById(itemId, userId);
        assertEquals(result, itemDto);
    }

    @Test
    void getByOwner_shouldReturnEmptyList() {
        long userId = 1L;
        long itemId = 1L;

        when(itemRepository.findAll()).thenReturn(List.of());

        assertEquals(itemService.getByOwner(userId), List.of());
    }

    @Test
    void search_shouldThrowBadRequestException() {
        assertThatThrownBy(
                () -> itemService.search(null)
        ).isInstanceOf(BadRequestException.class);
    }

    @Test
    void search_shouldReturnEmptyList() {
        assertEquals(itemService.search(" "), List.of());
    }

    @Test
    void search_shouldReturnList() {
        long userId = 1L;
        long itemId = 1L;
        User owner = Helper.createUser(userId);
        Item item = Helper.createItem(itemId, owner);
        ItemDto itemDto = itemMapper.toDto(item);
        User user = Helper.createUser(userId);
        item.setOwner(user);

        when(itemRepository.findAllByText(anyString())).thenReturn(List.of(item));

        assertEquals(itemService.search("test"), List.of(itemDto));
    }

    @Test
    void add_shouldReturnItemDto() {
        long userId = 1L;
        long itemId = 0L;
        User owner = Helper.createUser(userId);
        Item item = Helper.createItem(itemId, owner);
        ItemDto itemDto = itemMapper.toDto(item);
        User user = Helper.createUser(userId);
        item.setOwner(user);

        when(userService.findById(userId)).thenReturn(user);
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArguments()[0]);

        assertEquals(itemService.add(itemDto, userId), itemDto);
    }

    @Test
    void update_shouldThrowDataNotFoundException() {
        long userId = 1L;
        long itemId = 0L;
        User owner = Helper.createUser(userId);
        Item item = Helper.createItem(itemId, owner);
        ItemDto itemDto = itemMapper.toDto(item);
        User user = Helper.createUser(userId);
        item.setOwner(user);

        assertThatThrownBy(
                () -> itemService.update(itemId, itemDto, 2)
        ).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void update_shouldThrowDataNotFoundException2() {
        long userId = 1L;
        long itemId = 0L;
        User owner = Helper.createUser(userId);
        Item item = Helper.createItem(itemId, owner);
        ItemDto itemDto = itemMapper.toDto(item);
        User user = Helper.createUser(userId);

        assertThatThrownBy(
                () -> itemService.update(itemId, itemDto, 1)
        ).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void update_shouldReturnItemDto() {
        long userId = 1L;
        long itemId = 0L;
        User owner = Helper.createUser(userId);
        Item item = Helper.createItem(itemId, owner);
        User user = Helper.createUser(userId);
        item.setOwner(user);
        item.setComments(new ArrayList<>());
        ItemDto itemDto = itemMapper.toDto(item);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.getById(anyLong())).thenReturn(item);

        assertEquals(itemService.update(itemId, itemDto, userId), itemDto);
    }

    @Test
    void delete_shouldReturnVoid() {
        long userId = 1L;
        long itemId = 0L;
        User owner = Helper.createUser(userId);
        Item item = Helper.createItem(itemId, owner);
        User user = Helper.createUser(userId);
        item.setOwner(user);
        item.setComments(new ArrayList<>());
        ItemDto itemDto = itemMapper.toDto(item);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        itemService.delete(itemId, userId);
        verify(itemRepository, Mockito.times(1)).delete(item);
    }

    @Test
    void commentSave_shouldThrowDataNotFoundException() {
        long userId = 1L;
        long itemId = 0L;

        User owner = Helper.createUser(userId);
        Item item = Helper.createItem(itemId, owner);
        User user = Helper.createUser(userId);
        item.setOwner(user);

        Comment comment = Helper.createComment(1L, item, user);
        CommentDto commentDto = commentMapper.toDto(comment);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(
                () -> itemService.commentSave(itemId, userId, commentDto)
        ).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void commentSave_shouldThrowBadRequestException() {
        long userId = 1L;
        long itemId = 0L;

        User owner = Helper.createUser(userId);
        Item item = Helper.createItem(itemId, owner);
        User user = Helper.createUser(userId);
        item.setOwner(user);

        Comment comment = Helper.createComment(1L, item, user);
        CommentDto commentDto = commentMapper.toDto(comment);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class), any())).thenReturn(List.of());
        assertThatThrownBy(
                () -> itemService.commentSave(itemId, userId, commentDto)
        ).isInstanceOf(BadRequestException.class);
    }

    @Test
    void commentSave_shouldReturnCommentDto() {
        long userId = 1L;
        long itemId = 0L;
        User owner = Helper.createUser(userId);
        Item item = Helper.createItem(itemId, owner);
        User user = Helper.createUser(userId);
        item.setOwner(user);
        Comment comment = Helper.createComment(0L, item, user);
        CommentDto commentDto = commentMapper.toDto(comment);
        Booking booking = Helper.createBokking(0L, item, user);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userService.findById(anyLong())).thenReturn(user);
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class), any())).thenReturn(List.of(booking));
        when(commentRepository.save(
                any(Comment.class))).thenAnswer(i -> i.getArguments()[0]);
        CommentDto result = itemService.commentSave(itemId, userId, commentDto);
        commentDto.setCreated(result.getCreated());

        assertEquals(
                result,
                commentDto
        );
    }
}