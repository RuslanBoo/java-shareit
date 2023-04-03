package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String HEADER_USER_ID_KEY = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getByOwner(@RequestHeader(value = HEADER_USER_ID_KEY) long ownerId) {
        return itemService.getByOwner(ownerId);
    }

    @GetMapping(path = "/{itemId}", name = "itemId")
    public ItemDto getById(@PathVariable long itemId, @RequestHeader(value = HEADER_USER_ID_KEY) long ownerId) {
        return itemService.getById(itemId, ownerId);
    }

    @GetMapping(path = "/search")
    public List<ItemDto> search(@RequestParam(name = "text") String text) {
        return itemService.search(text);
    }

    @PostMapping
    public ItemDto add(@RequestHeader(value = HEADER_USER_ID_KEY) long ownerId,
                       @RequestBody ItemDto itemDto) {
        return itemService.add(itemDto, ownerId);
    }

    @PatchMapping(path = "/{itemId}", name = "itemId")
    public ItemDto update(@RequestHeader(value = HEADER_USER_ID_KEY) long ownerId,
                          @PathVariable long itemId,
                          @RequestBody ItemDto itemDto) {
        return itemService.update(itemId, itemDto, ownerId);
    }

    @DeleteMapping(path = "/{itemId}", name = "itemId")
    public void delete(@RequestHeader(value = HEADER_USER_ID_KEY) long ownerId,
                       @PathVariable long itemId) {
        itemService.delete(itemId, ownerId);
    }

    @PostMapping("/{id}/comment")
    public CommentDto comment(
            @PathVariable long id,
            @RequestHeader(name = HEADER_USER_ID_KEY) long userId,
            @RequestBody CommentDto commentDto
    ) {
        return itemService.commentSave(id, userId, commentDto);
    }
}
