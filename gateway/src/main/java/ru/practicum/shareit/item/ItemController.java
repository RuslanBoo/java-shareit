package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private static final String HEADER_USER_ID_KEY = "X-Sharer-User-Id";
    @Autowired
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getByOwner(@RequestHeader(value = HEADER_USER_ID_KEY) long ownerId) {
        return itemClient.getItemsByOwner(ownerId);
    }

    @GetMapping(path = "/{itemId}", name = "itemId")
    public ResponseEntity<Object> getById(@Positive @PathVariable long itemId,
                                          @RequestHeader(value = HEADER_USER_ID_KEY) long ownerId) {
        return itemClient.getItemById(itemId, ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestParam String text,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "20") Integer size
    ) {
        if (text.isBlank()) {
            return ResponseEntity.ok(Collections.EMPTY_LIST);
        }

        return itemClient.search(text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(value = HEADER_USER_ID_KEY) long ownerId,
                                      @RequestBody @Validated(CreateItemDto.class) ItemDto itemDto) {
        return itemClient.addItem(itemDto, ownerId);
    }

    @PatchMapping(path = "/{itemId}", name = "itemId")
    public ResponseEntity<Object> update(@RequestHeader(value = HEADER_USER_ID_KEY) long ownerId,
                                         @Positive @PathVariable long itemId,
                                         @RequestBody @Valid ItemDto itemDto) {
        return itemClient.updateItem(itemId, itemDto, ownerId);
    }

    @DeleteMapping(path = "/{itemId}", name = "itemId")
    public ResponseEntity<Object> delete(@RequestHeader(value = HEADER_USER_ID_KEY) long ownerId,
                                         @Positive @PathVariable long itemId) {
        return itemClient.deleteItem(itemId, ownerId);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> comment(
            @Positive @PathVariable long id,
            @RequestHeader(name = HEADER_USER_ID_KEY) long userId,
            @RequestBody @Validated(CreateCommentDto.class) CommentDto commentDto
    ) {
        return itemClient.commentSave(id, userId, commentDto);
    }
}
