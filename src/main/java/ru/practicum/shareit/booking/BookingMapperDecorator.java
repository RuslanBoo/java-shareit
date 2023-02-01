package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.service.ItemService;

public abstract class BookingMapperDecorator implements BookingMapper {
    @Autowired
    @Qualifier("delegate")
    private BookingMapper bookingMapper;

    @Autowired
    private ItemService itemService;

    @Override
    public Booking fromDto(BookingDto bookingDto) {
        Booking booking = bookingMapper.fromDto(bookingDto);
        booking.setItem(itemService.findById(bookingDto.getItemId()));

        return booking;
    }

    @Override
    public BookingDto toDto(Booking booking) {
        BookingDto bookingDto = bookingMapper.toDto(booking);
        bookingDto.setItem(itemService.getById(booking.getItem().getId()));

        return bookingDto;
    }
}
