package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Long add(Item item);

    void update(Long itemId, Item item);

    void delete(Long itemId);

    Item getById(Long itemId);

    List<Item> getAll();

    List<Item> search(String query);
}
