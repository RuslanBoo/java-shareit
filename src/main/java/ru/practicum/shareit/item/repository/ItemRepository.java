package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    long add(Item item);

    void update(long itemId, Item item);

    void delete(long itemId);

    Item getById(long itemId);

    List<Item> getAll();

    List<Item> search(String query);
}
