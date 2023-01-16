package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
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
}
