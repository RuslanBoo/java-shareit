package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional(readOnly = true)
    public ItemDto getById(Long itemId) {
        return itemMapper.toDto(findById(itemId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getByOwner(Long ownerId) {
        return itemRepository.getAll()
                .stream()
                .filter(item -> isOwner(item, ownerId))
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional
    public ItemDto add(ItemDto itemDto, Long ownerId) {
        Item item = prepareDao(itemDto);
        item.setOwner(userService.findById(ownerId));
        Long newItemId = itemRepository.add(item);

        return prepareDto(itemRepository.getById(newItemId));
    }

    @Override
    @Transactional
    public ItemDto update(Long itemId, ItemDto itemDto, Long ownerId) {
        checkOwnerPermission(itemId, ownerId);
        partialUpdate(itemId, itemDto);

        return prepareDto(itemRepository.getById(itemId));
    }

    @Override
    @Transactional
    public void delete(Long itemId, Long ownerId) {
        findById(itemId);
        checkOwnerPermission(itemId, ownerId);
        itemRepository.delete(itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public Item findById(Long itemId) {
        Item item = itemRepository.getById(itemId);

        if (item == null) {
            throw new DataNotFoundException("Item not found");
        }

        return item;
    }

    private void partialUpdate(Long itemId, ItemDto itemDto) {
        Item updatedItem = findById(itemId);
        itemMapper.updateItem(itemDto, updatedItem);

        itemRepository.update(itemId, updatedItem);
    }

    private void checkOwnerPermission(Long itemId, Long ownerId) {
        Item item = findById(itemId);
        if (item.getOwner() == null || item.getOwner().getId() != ownerId) {
            throw new DataNotFoundException("Invalid owner for this item");
        }
    }

    private boolean isOwner(Item item, Long ownerId) {
        return item.getOwner().getId() == ownerId;
    }

    private Item prepareDao(ItemDto itemDto) {
        return itemMapper.fromDto(itemDto);
    }

    private ItemDto prepareDto(Item item) {
        return itemMapper.toDto(item);
    }
}
