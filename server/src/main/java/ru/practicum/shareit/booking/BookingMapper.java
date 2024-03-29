package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = "spring")
public interface BookingMapper {
    @Mapping(target = "itemId", source = "item.id")
    public BookingDto toDto(Booking booking);

    public Booking fromDto(BookingDto bookingDto);

    @Mapping(target = "bookerId", source = "booker.id")
    ShortBookingDto toShortBookingDto(Booking booking);
}

