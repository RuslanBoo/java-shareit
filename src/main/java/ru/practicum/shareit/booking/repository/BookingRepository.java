package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findFirstBookingByItemIdAndStartAfter(Long itemId, LocalDateTime start, Sort sort);

    Optional<Booking> findFirstBookingByItemIdAndEndBefore(Long itemId, LocalDateTime end, Sort sort);

    List<Booking> findAllByBooker(User booker, Sort sort);

    List<Booking> findAllByBookerAndStatus(User booker, BookingStatus status, Sort sort);

    List<Booking> findAllByBookerAndStartAfter(User booker, LocalDateTime start, Sort sort);

    List<Booking> findAllByBookerAndEndBefore(User booker, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(User booker, LocalDateTime start, LocalDateTime end, Sort sort);

    @Query(value = "select b from Booking b where b.item.owner = :owner")
    List<Booking> findAllByOwner(User owner, Sort sort);

    @Query(value = "select b from Booking b where b.item.owner = :owner and b.status = :status")
    List<Booking> findAllByOwnerAndStatus(User owner, BookingStatus status, Sort sort);

    @Query(value = "select b from Booking b where b.item.owner = :owner and b.start > current_timestamp ")
    List<Booking> findAllByOwnerAndStartAfter(User owner, Sort sort);

    @Query(value = "select b from Booking b where b.item.owner = :owner and b.end < current_timestamp")
    List<Booking> findAllByOwnerAndEndBefore(User owner, Sort sort);

    @Query(value = "select b from Booking b where b.item.owner = :owner and b.start < current_timestamp and b.end > current_timestamp ")
    List<Booking> findAllByOwnerAndStartBeforeAndEndAfter(User owner, Sort sort);
}
