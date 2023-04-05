package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.model.BadRequestException;
import ru.practicum.shareit.error.model.DataNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.pagination.PaginationService;
import ru.practicum.shareit.testUtils.Helper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaginationService paginationService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    private long bookingId;
    private long itemId;
    private long userId;
    private long ownerId;
    private User user;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingId = 1L;
        itemId = 1L;
        userId = 1L;
        ownerId = 2L;
        user = Helper.createUser(userId);
        owner = Helper.createUser(ownerId);
        item = Helper.createItem(itemId, owner);
        booking = Helper.createBokking(bookingId, item, user);
        bookingDto = Helper.createBookingDto(bookingId, itemId, user);
        bookingDto.setItem(itemMapper.toDto(item));
    }

    @Test
    void getById_shouldThrowDataNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> bookingService.getById(bookingId, userId)
        ).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void getById_shouldReturnBookingDto() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertEquals(bookingService.getById(bookingId, user.getId()), bookingDto);
    }

    @SneakyThrows
    @Test
    void create_shouldReturnDataNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.create(1L, bookingDto))
                .isInstanceOf(DataNotFoundException.class);

        assertThatThrownBy(() -> bookingService.create(2L, bookingDto))
                .isInstanceOf(DataNotFoundException.class);

        Item item2 = Helper.createItem(itemId, user);
        User user2 = Helper.createUser(2L);
        item2.setOwner(user2);
        bookingDto.setItem(itemMapper.toDto(item2));
        assertThatThrownBy(() -> bookingService.create(2L, bookingDto))
                .isInstanceOf(DataNotFoundException.class);
    }

    @SneakyThrows
    @Test
    void create_shouldReturnBadRequestException() {
        item.setAvailable(false);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(1L, bookingDto))
                .isInstanceOf(BadRequestException.class);
    }

    @SneakyThrows
    @Test
    void create_shouldReturnDataNotFoundException2() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> bookingService.create(1L, bookingDto))
                .isInstanceOf(DataNotFoundException.class);
    }

    @SneakyThrows
    @Test
    void create_shouldReturnBadRequestException2() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        bookingDto.setStart(LocalDateTime.of(2022, 12, 12, 0, 0, 0));
        bookingDto.setEnd(LocalDateTime.of(2021, 12, 12, 0, 0, 0));

        assertThatThrownBy(() -> bookingService.create(3L, bookingDto))
                .isInstanceOf(BadRequestException.class);
    }

    @SneakyThrows
    @Test
    void create_shouldReturnBookingDto() {
        booking.setStatus(BookingStatus.APPROVED);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        assertEquals(bookingService.create(1L, bookingDto), bookingDto);
    }

    @SneakyThrows
    @Test
    void update_shouldReturnBadRequestException() {
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.update(1L, owner.getId(), true))
                .isInstanceOf(BadRequestException.class);
    }

    @SneakyThrows
    @Test
    void update_shouldReturnBookingDto() {
        booking.setItem(item);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        assertEquals(bookingService.update(bookingId, owner.getId(), true), bookingMapper.toDto(booking));
    }

    @Test
    void getAllByBooker_shouldReturnDataNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getAllByBooker(1L, "ALL", 1, 10))
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void getAllByBooker_shouldReturnBadRequestException() {
        assertThatThrownBy(() -> bookingService.getAllByBooker(1L, "UNKOWN", 1, 10))
                .isInstanceOf(BadRequestException.class);
    }

    @SneakyThrows
    @Test
    void getAllByBooker_shouldReturnEmptyList() {
        User user = Helper.createUser(1L);
        List<BookingDto> emptyList = new ArrayList<>();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(paginationService.getPageable(anyInt(), anyInt())).thenReturn(PageRequest.of(1, 10));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(
                anyLong(), any(PageRequest.class))
        ).thenReturn(List.of());
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(PageRequest.class))
        ).thenReturn(List.of());
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class))
        ).thenReturn(List.of());
        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(PageRequest.class))
        ).thenReturn(List.of());
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                anyLong(), any(BookingStatus.class), any(PageRequest.class))
        ).thenReturn(List.of());

        assertEquals(bookingService.getAllByBooker(1L, "ALL", 1, 10), emptyList);
        assertEquals(bookingService.getAllByBooker(1L, "PAST", 1, 10), emptyList);
        assertEquals(bookingService.getAllByBooker(1L, "FUTURE", 1, 10), emptyList);
        assertEquals(bookingService.getAllByBooker(1L, "CURRENT", 1, 10), emptyList);
        assertEquals(bookingService.getAllByBooker(1L, "WAITING", 1, 10), emptyList);
        assertEquals(bookingService.getAllByBooker(1L, "REJECTED", 1, 10), emptyList);
    }

    @Test
    void getAllByOwner_shouldReturnDataNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getAllByOwner(1L, "ALL", 1, 10))
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void getAllByOwner_shouldReturnBadRequestException() {
        assertThatThrownBy(() -> bookingService.getAllByOwner(1L, "UNKOWN", 1, 10))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void getAllByOwner_shouldReturnEmptyList() {
        User user = Helper.createUser(1L);
        List<BookingDto> emptyList = new ArrayList<>();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(paginationService.getPageable(anyInt(), anyInt())).thenReturn(PageRequest.of(1, 10));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(
                anyLong(), any(PageRequest.class))
        ).thenReturn(List.of());
        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(PageRequest.class))
        ).thenReturn(List.of());
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class))
        ).thenReturn(List.of());
        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(PageRequest.class))
        ).thenReturn(List.of());
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                anyLong(), any(BookingStatus.class), any(PageRequest.class))
        ).thenReturn(List.of());

        assertEquals(bookingService.getAllByOwner(1L, "ALL", 1, 10), emptyList);
        assertEquals(bookingService.getAllByOwner(1L, "PAST", 1, 10), emptyList);
        assertEquals(bookingService.getAllByOwner(1L, "FUTURE", 1, 10), emptyList);
        assertEquals(bookingService.getAllByOwner(1L, "CURRENT", 1, 10), emptyList);
        assertEquals(bookingService.getAllByOwner(1L, "WAITING", 1, 10), emptyList);
        assertEquals(bookingService.getAllByOwner(1L, "REJECTED", 1, 10), emptyList);
    }
}