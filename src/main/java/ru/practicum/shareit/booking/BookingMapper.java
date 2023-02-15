package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.service.ItemServiceImpl;

@Mapper(componentModel = "spring", uses = ItemServiceImpl.class)
public interface BookingMapper {
    @Mapping(target = "itemId", source = "item.id")
    public BookingDto toDto(Booking booking);

    public Booking fromDto(BookingDto bookingDto);

    @Mapping(target = "bookerId", source = "booker.id")
    ShortBookingDto toShortBookingDto(Booking booking);
}

