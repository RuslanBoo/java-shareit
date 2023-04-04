package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;
import java.util.Objects;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {
    private long id;
    private Long itemId;
    private LocalDateTime start;
    private User booker;
    private ItemDto item;
    private LocalDateTime end;
    private BookingStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingDto that = (BookingDto) o;
        return id == that.id && Objects.equals(itemId, that.itemId) && Objects.equals(start, that.start) && Objects.equals(booker, that.booker) && Objects.equals(item, that.item) && Objects.equals(end, that.end) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, itemId, start, booker, item, end, status);
    }
}
