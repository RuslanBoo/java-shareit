package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingPermission;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.model.BadRequestException;
import ru.practicum.shareit.error.model.DataNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InDBBookingService implements BookingService {
    private static final Sort SORT = Sort.by(Sort.Direction.DESC, "start");
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserService userService;

    @Override
    public BookingDto add(BookingDto bookingDto, Long userId) {
        Booking booking = initBooking(bookingDto, userId);

        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(userService.findById(userId));

        return prepareDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto changeStatus(Long bookingId, boolean isApproved, Long ownerId) {
        Booking booking = findById(bookingId);
        checkPermission(booking, ownerId, BookingPermission.OWNER);
        if(booking.getStatus().equals(BookingStatus.APPROVED)){
            throw new BadRequestException("Booking status can't be changed");
        }

        if(isApproved){
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        bookingRepository.save(booking);

        return prepareDto(findById(bookingId));
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = findById(bookingId);
        checkPermission(booking, userId, BookingPermission.BOOKER_OR_OWNER);

        return prepareDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookingsByState(Long userId, String state) {
        try {
            BookingState bookingState = BookingState.valueOf(state);
            User user = userService.findById(userId);
            List<Booking> result = bookingState.getBookerBooking(user, SORT, bookingRepository);

            return result.stream()
                    .map(bookingMapper::toDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new BadRequestException(String.format("Unknown state: %s", state));
        }
    }

    @Override
    public List<BookingDto> getOwnerBookingsByState(Long userId, String state) {
        try {
            BookingState bookingState = BookingState.valueOf(state);
            User user = userService.findById(userId);
            List<Booking> result = bookingState.getOwnerBooking(user, SORT, bookingRepository);

            return result.stream()
                    .map(bookingMapper::toDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new BadRequestException(String.format("Unknown state: %s", state));
        }
    }

    @Override
    public Optional<Booking> getLastForItem(Long itemId) {
        return bookingRepository.findFirstBookingByItemIdAndEndBefore(
                itemId,
                LocalDateTime.now(),
                Sort.by(Sort.Direction.DESC, "end"));
    }

    @Override
    public Optional<Booking> getNextForItem(Long itemId) {
        return bookingRepository.findFirstBookingByItemIdAndStartAfter(
                itemId,
                LocalDateTime.now(),
                Sort.by(Sort.Direction.ASC, "start"));
    }

    public Booking findById(Long bookingId){
        Optional<Booking> booking = bookingRepository.findById(bookingId);

        if(booking.isEmpty()){
            throw new DataNotFoundException("Booking not found");
        }

        return booking.get();
    }

    private void checkPermission(Booking booking, Long userId, BookingPermission bookingPermission){
        if(!bookingPermission.check(userId, booking)){
            throw new DataNotFoundException("User has not access to this booking");
        }
    }

    private Booking initBooking(BookingDto bookingDto, Long userId){
        Booking booking = prepareDao( bookingDto);
        Item item = booking.getItem();

        if(!item.getAvailable().equals(Boolean.TRUE)){
            throw new BadRequestException("Item is not available");
        }

        if(item.getOwner().getId().equals(userId)){
            throw new DataNotFoundException("Item can't be booked by owner");
        }

        return booking;
    }

    private Booking prepareDao(BookingDto bookingDto) {
        return bookingMapper.fromDto(bookingDto);
    }

    private BookingDto prepareDto(Booking booking) {
        return bookingMapper.toDto(booking);
    }
}
