package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

@Mapper
public interface BookingMapper {
    public BookingDto toDto(Booking booking);

    public Booking fromDto(BookingDto bookingDto);
}

