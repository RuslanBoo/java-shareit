package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
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

    @Test
    void getById_shouldThrowDataNotFoundException() {
        long userId = 1L;
        long bookingId = 1L;

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> bookingService.getById(bookingId, userId)
        ).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void getById_shouldReturnBookingDto() {
        long bookingId = 1L;
        long itemId = 1L;
        User user = Helper.createUser(1L);
        Item item = Helper.createItem(itemId, user);
        BookingDto bookingDto = Helper.createBookingDto(bookingId, itemId, user);
        bookingDto.setItem(itemMapper.toDto(item));

        Booking booking = Helper.createBokking(bookingId, item, user);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getById(bookingId, user.getId());
        assertEquals(result, bookingDto);
    }

    @SneakyThrows
    @Test
    void create_shouldReturnDataNotFoundException() {
        long bookingId = 1L;
        long itemId = 1L;
        User user = Helper.createUser(1L);
        Item item = Helper.createItem(itemId, user);
        BookingDto bookingDto = Helper.createBookingDto(bookingId, itemId, user);
        bookingDto.setItem(itemMapper.toDto(item));

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
        long bookingId = 1L;
        long itemId = 1L;
        User user = Helper.createUser(1L);
        Item item = Helper.createItem(itemId, user);
        BookingDto bookingDto = Helper.createBookingDto(bookingId, itemId, user);
        bookingDto.setItem(itemMapper.toDto(item));
        item.setAvailable(false);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(1L, bookingDto))
                .isInstanceOf(BadRequestException.class);
    }

    @SneakyThrows
    @Test
    void create_shouldReturnDataNotFoundException2() {
        long bookingId = 1L;
        long itemId = 1L;
        User user = Helper.createUser(1L);
        Item item = Helper.createItem(itemId, user);
        BookingDto bookingDto = Helper.createBookingDto(bookingId, itemId, user);
        bookingDto.setItem(itemMapper.toDto(item));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(1L, bookingDto))
                .isInstanceOf(DataNotFoundException.class);
    }

    @SneakyThrows
    @Test
    void create_shouldReturnBadRequestException2() {
        long bookingId = 1L;
        long itemId = 1L;
        User user = Helper.createUser(1L);
        User owner = Helper.createUser(2L);
        Item item = Helper.createItem(itemId, owner);
        BookingDto bookingDto = Helper.createBookingDto(bookingId, itemId, user);
        bookingDto.setItem(itemMapper.toDto(item));

        bookingDto.setStart(LocalDateTime.of(2022, 12, 12, 0, 0, 0));
        bookingDto.setEnd(LocalDateTime.of(2021, 12, 12, 0, 0, 0));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(1L, bookingDto))
                .isInstanceOf(BadRequestException.class);
    }

    @SneakyThrows
    @Test
    void create_shouldReturnBookingDto() {
        long bookingId = 1L;
        long itemId = 1L;
        User user = Helper.createUser(1L);
        User owner = Helper.createUser(2L);
        Item item = Helper.createItem(itemId, owner);
        BookingDto bookingDto = Helper.createBookingDto(bookingId, itemId, user);
        bookingDto.setItem(itemMapper.toDto(item));
        Booking booking = bookingMapper.fromDto(bookingDto);
        booking.setStatus(BookingStatus.APPROVED);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        BookingDto result = bookingService.create(1L, bookingDto);

        assertEquals(result, bookingDto);
    }

    @SneakyThrows
    @Test
    void update_shouldReturnBadRequestException() {
        long bookingId = 1L;
        long itemId = 1L;
        User user = Helper.createUser(1L);
        User owner = Helper.createUser(2L);
        Item item = Helper.createItem(itemId, owner);
        BookingDto bookingDto = Helper.createBookingDto(bookingId, itemId, user);

        Booking booking = bookingMapper.fromDto(bookingDto);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.update(1L, owner.getId(), true))
                .isInstanceOf(BadRequestException.class);
    }

    @SneakyThrows
    @Test
    void update_shouldReturnBookingDto() {
        long bookingId = 1L;
        long itemId = 1L;
        User user = Helper.createUser(1L);
        User owner = Helper.createUser(2L);
        Item item = Helper.createItem(itemId, owner);
        BookingDto bookingDto = Helper.createBookingDto(bookingId, itemId, user);

        Booking booking = bookingMapper.fromDto(bookingDto);
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