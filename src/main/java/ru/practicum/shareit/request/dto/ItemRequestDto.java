package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
public class ItemRequestDto {
    private long id;

    @NotBlank(message = "Invalid itemRequest description")
    private String description;

    private List<ItemDto> items;

    private LocalDateTime created;
}
