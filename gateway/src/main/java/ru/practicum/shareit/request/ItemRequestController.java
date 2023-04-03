package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private static final String HEADER_USER_ID_KEY = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> getAllByRequestorId(@RequestHeader(value = HEADER_USER_ID_KEY) long requestorId,
                                                      @PositiveOrZero @RequestParam(name = "from", required = false) Integer from,
                                                      @PositiveOrZero @RequestParam(name = "size", required = false) Integer size) {
        return itemRequestClient.getAllByRequestorId(requestorId, from, size);
    }

    @GetMapping(path = "/{requestId}", name = "requestId")
    public ResponseEntity<Object> getById(@RequestHeader(value = HEADER_USER_ID_KEY) long ownerId,
                                          @Positive @PathVariable long requestId) {
        return itemRequestClient.getById(ownerId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader(value = HEADER_USER_ID_KEY) long userId,
                                         @PositiveOrZero @RequestParam(name = "from", required = false) Integer from,
                                         @PositiveOrZero @RequestParam(name = "size", required = false) Integer size) {
        return itemRequestClient.getAll(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(value = HEADER_USER_ID_KEY) long requestorId,
                                      @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestClient.add(itemRequestDto, requestorId);
    }
}
