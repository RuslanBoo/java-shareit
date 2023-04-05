package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long bookerId,
                                                                             LocalDateTime start,
                                                                             LocalDateTime end,
                                                                             Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItemIdAndBookerIdAndStatusAndEndBeforeOrderByStartDesc(long itemId, long bookerId, BookingStatus status, LocalDateTime date, Pageable pageable);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(long ownerId,
                                                                                LocalDateTime start,
                                                                                LocalDateTime end,
                                                                                Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(long ownerId,
                                                                   LocalDateTime end,
                                                                   Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(long ownerId,
                                                                    LocalDateTime start,
                                                                    Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long ownerId,
                                                                BookingStatus status,
                                                                Pageable pageable);

    Optional<Booking> findFirstByItemIdAndStatusNotAndStartBeforeOrderByStartDesc(long itemId,
                                                                                  BookingStatus status,
                                                                                  LocalDateTime start);

    Optional<Booking> findFirstByItemIdAndStatusNotAndStartAfterOrderByStartAsc(long itemId,
                                                                                BookingStatus status,
                                                                                LocalDateTime start);
}
