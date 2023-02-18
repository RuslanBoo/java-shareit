package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingPermission;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.model.BadRequestException;
import ru.practicum.shareit.error.model.DataNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto getById(long bookingId, long userId) {
        return prepareDto(findById(bookingId, userId, BookingPermission.BOOKER_OR_OWNER));
    }

    @Override
    public BookingDto create(long userId, BookingDto bookingDto) {
        Booking booking = prepareDao(bookingDto);
        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new DataNotFoundException("User not found");
        }

        if (item.isEmpty()) {
            throw new DataNotFoundException("Item not found");
        }

        if (!item.get().getAvailable()) {
            throw new BadRequestException("Item is not available");
        }

        if (user.get().getId() == item.get().getOwner().getId()) {
            throw new DataNotFoundException("Owner can not booking self item");
        }

        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new BadRequestException("Date end must be after date start");
        }

        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(user.get());
        booking.setItem(item.get());

        return prepareDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto update(long bookingId, long ownerId, boolean approved) {
        Booking booking = findById(bookingId, ownerId, BookingPermission.OWNER);

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BadRequestException("Can not change status after APPROVED");
        }

        BookingStatus status = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(status);

        return prepareDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingDto> getAllByBooker(long bookerId, String state) {
        try {
            List<Booking> result = new ArrayList<>();
            BookingState bookingState = BookingState.valueOf(state);

            Optional<User> booker = userRepository.findById(bookerId);
            if (booker.isEmpty()) {
                throw new DataNotFoundException("User not found");
            }

            switch (bookingState) {
                case ALL:
                    result = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId);
                    break;
                case PAST:
                    result = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now());
                    break;
                case CURRENT:
                    result = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, LocalDateTime.now(), LocalDateTime.now());
                    break;
                case FUTURE:
                    result = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now());
                    break;
                case WAITING:
                    result = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING);
                    break;
                case REJECTED:
                    result = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED);
                    break;
            }

            return result.stream()
                    .map(bookingMapper::toDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingDto> getAllByOwner(long ownerId, String state) {
        try {
            List<Booking> result = new ArrayList<>();
            BookingState bookingState = BookingState.valueOf(state);

            Optional<User> owner = userRepository.findById(ownerId);
            if (owner.isEmpty()) {
                throw new DataNotFoundException("User not found");
            }

            switch (bookingState) {
                case ALL:
                    result = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
                    break;
                case PAST:
                    result = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now());
                    break;
                case CURRENT:
                    result = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, LocalDateTime.now(), LocalDateTime.now());
                    break;
                case FUTURE:
                    result = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now());
                    break;
                case WAITING:
                    result = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
                    break;
                case REJECTED:
                    result = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
                    break;
            }

            return result.stream()
                    .map(bookingMapper::toDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private Booking prepareDao(BookingDto bookingDto) {
        return bookingMapper.fromDto(bookingDto);
    }

    private BookingDto prepareDto(Booking booking) {
        return bookingMapper.toDto(booking);
    }

    private void checkPermission(Booking booking, Long userId, BookingPermission permission) {
        if (!permission.check(booking, userId)) {
            throw new DataNotFoundException("User permission denied");
        }
    }

    private Booking findById(long bookingId, long userId, BookingPermission permission) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);

        if (booking.isEmpty()) {
            throw new DataNotFoundException("Booking not found");
        }

        checkPermission(booking.get(), userId, permission);

        return booking.get();
    }
}
