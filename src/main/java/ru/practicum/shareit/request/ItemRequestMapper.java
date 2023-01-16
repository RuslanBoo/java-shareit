package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper
public interface ItemRequestMapper {
    ItemRequestDto toDto(ItemRequest itemRequest);

    ItemRequest fromDto(ItemRequestDto itemRequestDto);
}
