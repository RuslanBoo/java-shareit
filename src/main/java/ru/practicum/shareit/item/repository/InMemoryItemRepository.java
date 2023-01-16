package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {
    private long nextId = 1;
    private final Map<Long, Item> items;

    @Override
    public long add(Item item) {
        item.setId(nextId++);
        items.put(item.getId(), item);

        return item.getId();
    }

    @Override
    public void update(long itemId, Item item) {
        delete(itemId);
        items.put(itemId, item);
    }

    @Override
    public void delete(long itemId) {
        items.remove(itemId);
    }

    @Override
    public Item getById(long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> search(String query) {
        return items.values()
                .stream()
                .filter(Item::getAvailable)
                .filter(item -> isSuggest(item, query))
                .collect(Collectors.toList());
    }

    public boolean isSuggest(Item item, String query) {
        return item.getName().toLowerCase().contains(query) || item.getDescription().toLowerCase().contains(query);
    }
}
