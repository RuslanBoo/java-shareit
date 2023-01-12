package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getByOwner(@RequestHeader(value = "X-Sharer-User-Id") long ownerId) {
        return itemService.getByOwner(ownerId);
    }

    @GetMapping(path = "/{itemId}", name = "itemId")
    public ItemDto getById(@PathVariable long itemId) {
        return itemService.getById(itemId);
    }

    @GetMapping(path = "/search")
    public List<ItemDto> search(@RequestParam(name = "text") String text) {
        return itemService.search(text);
    }

    @PostMapping
    public ItemDto add(@RequestHeader(value = "X-Sharer-User-Id") long ownerId, @RequestBody ItemDto itemDto) {
        return itemService.add(itemDto, ownerId);
    }

    @PatchMapping(path = "/{itemId}", name = "itemId")
    public ItemDto update(@RequestHeader(value = "X-Sharer-User-Id") long ownerId, @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        return itemService.update(itemId, itemDto, ownerId);
    }

    @DeleteMapping(path = "/{itemId}", name = "itemId")
    public void delete(@RequestHeader(value = "X-Sharer-User-Id") long ownerId, @PathVariable long itemId) {
        itemService.delete(itemId, ownerId);
    }
}
