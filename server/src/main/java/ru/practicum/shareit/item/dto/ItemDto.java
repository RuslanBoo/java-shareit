package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.comment.dto.CommentDto;

import javax.persistence.Transient;
import java.util.List;
import java.util.Objects;


@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;

    @Transient
    private ShortBookingDto lastBooking;

    @Transient
    private ShortBookingDto nextBooking;

    @Transient
    private List<CommentDto> comments;

    private Long requestId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDto itemDto = (ItemDto) o;
        return id == itemDto.id && Objects.equals(name, itemDto.name) && Objects.equals(description, itemDto.description) && Objects.equals(available, itemDto.available) && Objects.equals(lastBooking, itemDto.lastBooking) && Objects.equals(nextBooking, itemDto.nextBooking) && Objects.equals(comments, itemDto.comments) && Objects.equals(requestId, itemDto.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, available, lastBooking, nextBooking, comments, requestId);
    }
}
