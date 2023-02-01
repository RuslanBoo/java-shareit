package ru.practicum.shareit.booking;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = "spring")
@DecoratedWith(BookingMapperDecorator.class)
public interface BookingMapper {
    public BookingDto toDto(Booking booking);

    public BookingDto itemToDto(Booking booking);
    @Mapping(target = "id", ignore = true)
    public Booking fromDto(BookingDto bookingDto);
}

