package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
public class Booking {
    private Long id;
    private Item item;
    private User booker;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
}
