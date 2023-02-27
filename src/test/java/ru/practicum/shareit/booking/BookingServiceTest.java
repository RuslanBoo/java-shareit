package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.model.DataNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.TestHelper;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;

    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void getAllByBooker_shouldCallFindAllByBookerIdOrderByStartDesc() {
        bookingService.getAllByBooker(1, "ALL", null, null);
        verify(bookingRepository).findAllByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getAllByBooker_shouldCallFindAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        bookingService.getAllByBooker(1, "CURRENT", null, null);
        verify(bookingRepository)
                .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any());
    }

    @Test
    void getAllByBooker_shouldCallFindAllByBookerIdAndEndBeforeOrderByStartDesc() {
        bookingService.getAllByBooker(1, "PAST", null, null);
        verify(bookingRepository)
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getAllByBooker_shouldCallFindAllByBookerIdAndStartAfterOrderByStartDesc() {
        bookingService.getAllByBooker(1, "FUTURE", null, null);
        verify(bookingRepository)
                .findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getAllByBooker_shouldCallFindAllByBookerIdAndStatusOrderByStartDescWithStatusWaiting() {
        bookingService.getAllByBooker(1, "WAITING", null, null);
        verify(bookingRepository)
                .findAllByBookerIdAndStatusOrderByStartDesc(1, BookingStatus.WAITING, null);
    }

    @Test
    void getAllByBooker_shouldCallFindAllByBookerIdAndStatusOrderByStartDescWithStatusRejected() {
        bookingService.getAllByBooker(1, "REJECTED", null, null);
        verify(bookingRepository)
                .findAllByBookerIdAndStatusOrderByStartDesc(1, BookingStatus.REJECTED, null);
    }

    @Test
    void getAllByBooker_shouldThrowUnsupportedStatusException() {
        assertThatThrownBy(() -> {
            bookingService.getAllByBooker(1, "UNDEFINED STATE", null, null);
        }).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void getAllByOwner_shouldCallFindAllByItemOwnerIdOrderByStartDesc() {
        bookingService.getAllByOwner(1, "ALL", null, null);
        verify(bookingRepository).findAllByItemOwnerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getAllByOwner_shouldCallFindAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        bookingService.getAllByOwner(1, "CURRENT", null, null);
        verify(bookingRepository)
                .findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any());
    }

    @Test
    void getAllByOwner_shouldCallFindAllByItemOwnerIdAndEndBeforeOrderByStartDesc() {
        bookingService.getAllByOwner(1, "PAST", null, null);
        verify(bookingRepository)
                .findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getAllByOwner_shouldCallFindAllByItemOwnerIdAndStartAfterOrderByStartDesc() {
        bookingService.getAllByOwner(1, "FUTURE", null, null);
        verify(bookingRepository)
                .findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getAllByOwner_shouldCallFindAllByItemOwnerIdAndStatusOrderByStartDescWithStatusWaiting() {
        bookingService.getAllByOwner(1, "WAITING", null, null);
        verify(bookingRepository)
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(1, BookingStatus.WAITING, null);
    }

    @Test
    void getAllByOwner_shouldCallFindAllByItemOwnerIdAndStatusOrderByStartDescWithStatusRejected() {
        bookingService.getAllByOwner(1, "REJECTED", null, null);
        verify(bookingRepository)
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(1, BookingStatus.REJECTED, null);
    }

    @Test
    void getAllByOwner_shouldThrowUnsupportedStatusException() {
        assertThatThrownBy(() -> {
            bookingService.getAllByOwner(1, "UNDEFIEND STATE", null, null);
        }).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void create_shouldThrowNotFoundIfUserIsNotExists() {
        long userId = 1;
        long itemId = 1;
        BookingDto dto = BookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .build();

        when(userService.getById(userId)).thenThrow(new DataNotFoundException("User not found"));

        assertThatThrownBy(() -> bookingService.create(userId, dto)).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void create_shouldThrowNotFoundIfItemIsNotExists() {
        long userId = 1;
        long itemId = 1;
        BookingDto dto = BookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.create(userId, dto)).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void create_shouldThrowFieldValidationExceptionIfItemIsUnavailable() {
        long userId = 1;
        long itemId = 1;
        BookingDto dto = BookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .build();
        Item item = TestHelper.createItem(itemId, false, null);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(userId, dto)).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void create_shouldThrowNotFoundIfUserIsNotOwner() {
        long userId = 1;
        long itemId = 1;
        BookingDto dto = BookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .build();
        User user = TestHelper.createUser(userId);
        Item item = TestHelper.createItem(itemId, true, user);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(userId, dto)).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void create_shouldThrowFieldValidationExceptionIfDateIsIncorrect() {
        long userId = 1;
        long itemId = 1;
        BookingDto dto = BookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().minusDays(1))
                .build();
        User user = TestHelper.createUser(2);
        Item item = TestHelper.createItem(itemId, true, user);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(userId, dto)).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void create_shouldCreateBooking() {
        long userId = 1;
        long itemId = 1;
        BookingDto dto = BookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .build();
        User user = TestHelper.createUser(2);
        Item item = TestHelper.createItem(itemId, true, user);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        BookingDto bookingDto = bookingService.create(userId, dto);

        assertThat(bookingDto).hasFieldOrProperty("id");
    }

    @Test
    void update_shouldThrowNotFoundIfBookingIsNotExists() {
        long bookingId = 1;
        long userId = 1;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.update(bookingId, userId, true)).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void update_shouldThrowNotFoundIfUserIsNotOwner() {
        long bookingId = 1;
        long userId = 1;
        long itemId = 1;
        User user = TestHelper.createUser(userId);
        Item item = TestHelper.createItem(itemId, true, user);
        Booking booking = new Booking(bookingId, item, user, LocalDateTime.now(), LocalDateTime.now(), BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.update(bookingId, 2, true)).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void update_shouldThrowFieldValidationExceptionIfBookingIsAlreadyApproved() {
        long bookingId = 1;
        long userId = 1;
        long itemId = 1;
        User user = TestHelper.createUser(userId);
        Item item = TestHelper.createItem(itemId, true, user);
        Booking booking = new Booking(bookingId, item, user, LocalDateTime.now(), LocalDateTime.now(), BookingStatus.APPROVED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.update(bookingId, userId, true)).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void update_shouldUpdateBookingToApproved() {
        long bookingId = 1;
        long userId = 1;
        long itemId = 1;
        User user = TestHelper.createUser(userId);
        Item item = TestHelper.createItem(itemId, true, user);
        Booking booking = new Booking(bookingId, item, user, LocalDateTime.now(), LocalDateTime.now(), BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        BookingDto bookingDto = bookingService.update(bookingId, userId, true);

        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void getById_shouldThrowNotFoundExceptionIfBookingIsNotExists() {
        long bookingId = 1;
        long userId = 1;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getById(bookingId, userId)).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void getById_shouldThrowNotFoundExceptionIfUserIsNotOwner() {
        long bookingId = 1;
        long userId = 1;
        long itemId = 1;

        User user = TestHelper.createUser(userId);
        Item item = TestHelper.createItem(itemId, true, user);
        Booking booking = new Booking(bookingId, item, user, LocalDateTime.now(), LocalDateTime.now(), BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.getById(bookingId, 2)).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void getById_shouldReturnBooking() {
        long bookingId = 1;
        long userId = 1;
        long itemId = 1;

        User user = TestHelper.createUser(userId);
        Item item = TestHelper.createItem(itemId, true, user);
        Booking booking = new Booking(bookingId, item, user, LocalDateTime.now(), LocalDateTime.now(), BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThat(bookingService.getById(bookingId, userId)).isEqualTo(booking);
    }
}
