package ru.practicum.shareit.booking.enums;

import ru.practicum.shareit.booking.model.Booking;

public enum BookingPermission {
    BOOKER{
        @Override
        public boolean check(Long userId, Booking booking){
            return booking.getBooker().getId().equals(userId);
        }
    },
    OWNER{
        @Override
        public boolean check(Long userId, Booking booking){
            return booking.getItem().getOwner().getId().equals(userId);
        }
    },
    BOOKER_OR_OWNER{
        @Override
        public boolean check(Long userId, Booking booking){
            return booking.getBooker().getId().equals(userId)
                    || booking.getItem().getOwner().getId().equals(userId);
        }
    };

    public abstract boolean check(Long userId, Booking booking);
}
