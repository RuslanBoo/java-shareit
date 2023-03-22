package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Service
public interface BookingService {
    BookingDto getById(long bookingId, long userId);

    BookingDto create(long userId, BookingDto dto);

    BookingDto update(long bookingId, long ownerId, boolean approved);

    List<BookingDto> getAllByBooker(long bookerId, String state, Integer from, Integer size);

    List<BookingDto> getAllByOwner(long ownerId, String state, Integer from, Integer size);
}
