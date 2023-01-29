package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto getById(Long itemId);

    List<ItemDto> getByOwner(Long ownerId);

    List<ItemDto> search(String query);

    ItemDto add(ItemDto itemDto, Long ownerId);

    ItemDto update(Long itemId, ItemDto itemDto, Long ownerId);

    void delete(Long itemId, Long ownerId);
}

