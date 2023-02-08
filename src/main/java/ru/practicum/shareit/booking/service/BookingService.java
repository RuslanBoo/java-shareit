package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    BookingDto add(BookingDto bookingDto, Long userId);

    BookingDto changeStatus(Long bookingId, boolean isApproved, Long ownerId);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getUserBookingsByState(Long userId, String state);

    List<BookingDto> getOwnerBookingsByState(Long userId, String state);

    Optional<BookingDto> getLastForItem(Long itemId);

    Optional<BookingDto> getNextForItem(Long itemId);

    Booking findById(Long bookingId);
}
