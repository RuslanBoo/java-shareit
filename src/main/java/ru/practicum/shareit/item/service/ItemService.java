package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto getById(long itemId);

    List<ItemDto> getByOwner(long ownerId);

    List<ItemDto> search(String query);

    ItemDto add(ItemDto itemDto, long ownerId);

    ItemDto update(long itemId, ItemDto itemDto, long ownerId);

    void delete(long itemId, long ownerId);
}

