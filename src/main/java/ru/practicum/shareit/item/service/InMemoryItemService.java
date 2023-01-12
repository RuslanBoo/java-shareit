package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.model.BadRequestException;
import ru.practicum.shareit.error.model.DataNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.InMemoryUserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InMemoryItemService implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final InMemoryUserService userService;

    @Override
    public ItemDto getById(long itemId) {
        return itemMapper.toDto(findById(itemId));
    }

    @Override
    public List<ItemDto> getByOwner(long ownerId) {
        return itemRepository.getAll()
                .stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String query) {
        if (query == null) {
            throw new BadRequestException("Invalid search text");
        }

        if (query.isBlank()) {
            return new ArrayList<>();
        }

        return itemRepository.search(query.toLowerCase())
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto add(ItemDto itemDto, long ownerId) {
        Item item = prepareDao(itemDto);
        item.setOwner(userService.findById(ownerId));
        long newItemId = itemRepository.add(item);

        return prepareDto(itemRepository.getById(newItemId));
    }

    @Override
    public ItemDto update(long itemId, ItemDto itemDto, long ownerId) {
        Item item = prepareDao(itemDto);
        checkOwnerPermission(itemId, ownerId);
        partialUpdate(itemId, item);

        return prepareDto(itemRepository.getById(itemId));
    }

    @Override
    public void delete(long itemId, long ownerId) {
        findById(itemId);
        checkOwnerPermission(itemId, ownerId);
        itemRepository.delete(itemId);
    }

    private void partialUpdate(long itemId, Item item) {
        Item updatedItem = findById(itemId);

        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }

        itemRepository.update(itemId, updatedItem);
    }

    private void checkOwnerPermission(long itemId, long ownerId) {
        Item item = findById(itemId);
        if (item.getOwner() == null || item.getOwner().getId() != ownerId) {
            throw new DataNotFoundException("Invalid owner for this item");
        }
    }

    private Item findById(long itemId) {
        Item item = itemRepository.getById(itemId);

        if (item == null) {
            throw new DataNotFoundException("Item not found");
        }

        return item;
    }

    private Item prepareDao(ItemDto itemDto) {
        return itemMapper.fromDto(itemDto);
    }

    private ItemDto prepareDto(Item item) {
        return itemMapper.toDto(item);
    }
}
