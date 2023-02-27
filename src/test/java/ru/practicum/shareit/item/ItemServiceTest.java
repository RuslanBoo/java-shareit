package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.error.model.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.TestHelper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private ItemService itemService;

    @Test
    void search_shouldReturnEmptyListIfTextIsBlank() {
        assertThat(itemService.search("")).isEmpty();
    }

    @Test
    void search_shouldReturnListOfItems() {
        List<Item> items = List.of(
                TestHelper.createItem(1, true, null),
                TestHelper.createItem(2, true, null),
                TestHelper.createItem(3, true, null)
        );
        List<ItemDto> itemDtos = items
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());

        when(itemRepository.findAllByText(anyString())).thenReturn(items);
        assertThat(itemService.search("text")).isEqualTo(itemDtos);
    }

    @Test
    void create_shouldThrowNotFoundExceptionIfUserIsNotExists() {
        long userId = 1;
        when(userService.getById(userId)).thenThrow(DataNotFoundException.class);
        assertThatThrownBy(() -> itemService.add(null, 0L)).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void create_shouldCreateItemWithRequest() {
        long userId = 1;
        long requestId = 1;
        User user = TestHelper.createUser(userId);
        ItemRequest itemRequest = TestHelper.createItemRequest(requestId, LocalDateTime.now(), user);
        ItemDto ItemDto = TestHelper.createItemDto(true, requestId);

        when(userService.getById(userId)).thenReturn(userMapper.toDto(user));
        when(itemRequestRepository.findById(userId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemDto itemDto = itemService.add(ItemDto, userId);

        assertThat(itemDto.getRequestId()).isEqualTo(requestId);
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getComments()).isNull();
    }

    @Test
    void update_shouldThrowNotFoundExceptionIfUserIsNotExists() {
        long itemId = 1;
        long userId = 1;

        when(userService.getById(userId)).thenThrow(DataNotFoundException.class);

        assertThatThrownBy(() -> itemService.update(itemId, null, userId)).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void update_shouldThrowNotFoundExceptionIfRequestIsNotExists() {
        long itemId = 1;
        long userId = 1;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.update(itemId, null, userId)).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void update_shouldUpdateItemName() {
        long itemId = 1;
        long userId = 1;
        User user = TestHelper.createUser(userId);
        Item item = TestHelper.createItem(itemId, true, user);
        ItemDto itemDto = ItemDto.builder()
                .name("new name")
                .build();

        when(userService.getById(userId)).thenReturn(userMapper.toDto(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemDto resultItemDto = itemService.update(itemId, itemDto, userId);

        assertThat(resultItemDto.getName()).isEqualTo("new name");
    }

    @Test
    void update_shouldUpdateItemDescription() {
        long itemId = 1;
        long userId = 1;
        User user = TestHelper.createUser(userId);
        Item item = TestHelper.createItem(itemId, true, user);
        ItemDto updateItemDto = ItemDto.builder()
                .description("new description")
                .build();

        when(userService.getById(userId)).thenReturn(userMapper.toDto(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemDto itemDto = itemService.update(itemId, updateItemDto, userId);

        assertThat(itemDto.getDescription()).isEqualTo("new description");
    }

    @Test
    void update_shouldUpdateItemAvailable() {
        long itemId = 1;
        long userId = 1;
        User user = TestHelper.createUser(userId);
        Item item = TestHelper.createItem(itemId, true, user);
        ItemDto updateItemDto = ItemDto.builder()
                .available(false)
                .build();

        when(userService.getById(userId)).thenReturn(userMapper.toDto(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemDto itemDto = itemService.update(itemId, updateItemDto, userId);

        assertThat(itemDto.getAvailable()).isFalse();
    }

    @Test
    void comment_shouldCommentRequest() {
        long itemId = 1;
        long userId = 1;
        User user = TestHelper.createUser(userId);
        Item item = TestHelper.createItem(itemId, true, user);
        CommentDto createCommentDto = CommentDto.builder()
                .text("new comment")
                .build();

        when(userService.getById(userId)).thenReturn(userMapper.toDto(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                anyLong(),
                any(),
                any()
        )).thenReturn(List.of(new Booking(1L, item, user, LocalDateTime.now(), LocalDateTime.now(), BookingStatus.APPROVED)));
        when(commentRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        CommentDto commentDto = itemService.commentSave(itemId, userId, createCommentDto);

        assertThat(commentDto.getAuthorName()).isEqualTo(user.getName());
    }

    @Test
    void delete_shouldDeleteItemAndReturnDeletedItem() {
        long itemId = 1;
        long userId = 1;
        User user = TestHelper.createUser(userId);
        Item item = TestHelper.createItem(itemId, true, user);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.delete(itemId, 0L);
    }

    @Test
    void getById_should() {
        long itemId = 1;
        long userId = 1;
        User user = TestHelper.createUser(userId);
        Item item = TestHelper.createItem(itemId, true, user);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllById(Collections.singleton(anyLong()))).thenReturn(Collections.emptyList());
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(Collections.emptyList());

        assertThat(itemService.getById(itemId, userId)).isEqualTo(itemMapper.toDto(item));
    }
}
