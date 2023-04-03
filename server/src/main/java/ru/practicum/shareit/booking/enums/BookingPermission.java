package ru.practicum.shareit.booking.enums;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;

@Component
public enum BookingPermission {
    OWNER {
        @Override
        public boolean check(Booking booking, Long userId) {
            return booking.getItem().getOwner().getId() == userId;
        }
    },
    BOOKER {
        @Override
        public boolean check(Booking booking, Long userId) {
            return booking.getBooker().getId() == userId;
        }
    },
    BOOKER_OR_OWNER {
        @Override
        public boolean check(Booking booking, Long userId) {
            return BOOKER.check(booking, userId)
                    || OWNER.check(booking, userId);
        }
    };

    public abstract boolean check(Booking booking, Long userId);
}
