package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.model.DataNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.InDBItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.InDBUserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class InDBItemService implements ItemService {
    private final InDBItemRepository inDBItemRepository;
    private final InDBUserService inDBUserService;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto getById(Long itemId) {
        return prepareDto(findById(itemId));
    }

    @Override
    public List<ItemDto> getByOwner(Long ownerId) {
        User owner = inDBUserService.findById(ownerId);
        return inDBItemRepository.getByOwner(owner).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String query) {
        List<ItemDto> result = new ArrayList<>();

        if(!query.isBlank()){
            result = inDBItemRepository.findByTextLike(query).stream()
                    .map(itemMapper::toDto)
                    .collect(Collectors.toList());
        }

        return result;
    }

    @Override
    public ItemDto add(ItemDto itemDto, Long ownerId) {
        Item item = prepareDao(itemDto);
        item.setOwner(inDBUserService.findById(ownerId));

        return prepareDto(inDBItemRepository.save(item));
    }

    @Override
    public ItemDto update(Long itemId, ItemDto itemDto, Long ownerId) {
        checkOwnerPermission(itemId, ownerId);
        partialUpdate(itemId, itemDto);

        return getById(itemId);
    }

    @Override
    public void delete(Long itemId, Long ownerId) {
        Item deletedItem = findById(itemId);
        isOwner(deletedItem, ownerId);
        inDBItemRepository.delete(deletedItem);
    }

    @Override
    public Item findById(Long itemId) {
        Optional<Item> item = inDBItemRepository.findById(itemId);

        if (item.isEmpty()) {
            throw new DataNotFoundException("Item not found");
        }

        return item.get();
    }

    private void partialUpdate(Long itemId, ItemDto itemDto) {
        Item updatedItem = findById(itemId);
        itemMapper.updateItem(itemDto, updatedItem);

        inDBItemRepository.save(updatedItem);
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
