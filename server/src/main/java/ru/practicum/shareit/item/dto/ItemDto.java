package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.comment.dto.CommentDto;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    private long id;

    @NotNull(message = "Empty item name", groups = CreateItemDto.class)
    @NotBlank(message = "Invalid item name", groups = {CreateItemDto.class, UpdateItemDto.class})
    private String name;

    @NotNull(message = "Empty item description", groups = CreateItemDto.class)
    @NotBlank(message = "Invalid item description", groups = {CreateItemDto.class, UpdateItemDto.class})
    private String description;

    @NotNull(message = "Empty item available", groups = CreateItemDto.class)
    private Boolean available;

    @Transient
    private ShortBookingDto lastBooking;

    @Transient
    private ShortBookingDto nextBooking;

    @Transient
    private List<CommentDto> comments;

    private Long requestId;
}
