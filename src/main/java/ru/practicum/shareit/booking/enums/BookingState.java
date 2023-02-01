package ru.practicum.shareit.booking.enums;

import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public enum BookingState {
    ALL{
        @Override
        public List<Booking> getOwnerBooking(User user, Sort sort, BookingRepository bookingRepository) {
            return bookingRepository
                    .findAllByOwner(user, sort);
        }

        @Override
        public List<Booking> getBookerBooking(User user, Sort sort, BookingRepository bookingRepository) {
            return bookingRepository
                    .findAllByBooker(user, sort);
        }
    },
    CURRENT{
        @Override
        public List<Booking> getOwnerBooking(User user, Sort sort, BookingRepository bookingRepository) {
            return bookingRepository
                    .findAllByOwnerAndStartBeforeAndEndAfter(user, sort);
        }

        @Override
        public List<Booking> getBookerBooking(User user, Sort sort, BookingRepository bookingRepository) {
            return bookingRepository
                    .findAllByBookerAndStartBeforeAndEndAfter(user, LocalDateTime.now(), LocalDateTime.now(), sort);
        }
    },
    PAST{
        @Override
        public List<Booking> getOwnerBooking(User user, Sort sort, BookingRepository bookingRepository) {
            return bookingRepository
                    .findAllByOwnerAndEndBefore(user, sort);
        }

        @Override
        public List<Booking> getBookerBooking(User user, Sort sort, BookingRepository bookingRepository) {
            return bookingRepository
                    .findAllByBookerAndEndBefore(user, LocalDateTime.now(), sort);
        }
    },
    FUTURE{
        @Override
        public List<Booking> getOwnerBooking(User user, Sort sort, BookingRepository bookingRepository) {
            return bookingRepository
                    .findAllByOwnerAndStartAfter(user, sort);
        }

        @Override
        public List<Booking> getBookerBooking(User user, Sort sort, BookingRepository bookingRepository) {
            return bookingRepository
                    .findAllByBookerAndStartAfter(user, LocalDateTime.now(), sort);
        }
    },
    WAITING{
        @Override
        public List<Booking> getOwnerBooking(User user, Sort sort, BookingRepository bookingRepository) {
            return bookingRepository
                    .findAllByOwnerAndStatus(user, BookingStatus.WAITING, sort);
        }

        @Override
        public List<Booking> getBookerBooking(User user, Sort sort, BookingRepository bookingRepository) {
            return bookingRepository
                    .findAllByBookerAndStatus(user, BookingStatus.WAITING, sort);
        }
    },
    REJECTED{
        @Override
        public List<Booking> getOwnerBooking(User user, Sort sort, BookingRepository bookingRepository) {
            return bookingRepository
                    .findAllByOwnerAndStatus(user, BookingStatus.REJECTED, sort);
        }

        @Override
        public List<Booking> getBookerBooking(User user, Sort sort, BookingRepository bookingRepository) {
            return bookingRepository
                    .findAllByBookerAndStatus(user, BookingStatus.REJECTED, sort);
        }
    };

    public abstract List<Booking> getOwnerBooking(User user, Sort sort, BookingRepository bookingRepository);
    public abstract List<Booking> getBookerBooking(User user, Sort sort, BookingRepository bookingRepository);
}
