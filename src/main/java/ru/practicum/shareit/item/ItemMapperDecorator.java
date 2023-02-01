package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;

public abstract class ItemMapperDecorator implements ItemMapper {
    @Autowired
    @Qualifier("delegate")
    private ItemMapper itemMapper;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingMapper bookingMapper;

    @Override
    public ItemDto toDto(Item item) {
        ItemDto itemDto = itemMapper.toDto(item);

        Optional<Booking> lastBooking = bookingService.getLastForItem(item.getId());
        lastBooking.ifPresent(booking -> itemDto.setLastBookingDto(bookingMapper.itemToDto(booking)));

        Optional<Booking> nextBooking = bookingService.getNextForItem(item.getId());
        nextBooking.ifPresent(booking -> itemDto.setNextBookingDto(bookingMapper.itemToDto(booking)));

        return itemDto;
    }
}
