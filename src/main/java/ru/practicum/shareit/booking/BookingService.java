package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto getById(long bookingId, long userId);

    BookingDto create(long userId, BookingDto dto);

    BookingDto update(long bookingId, long ownerId, boolean approved);

    List<BookingDto> getAllByBooker(long bookerId, String state);

    List<BookingDto> getAllByOwner(long ownerId, String state);
}
