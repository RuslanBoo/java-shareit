package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private static final String HEADER_USER_ID_KEY = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestDto> getAllByRequestorId(@RequestHeader(value = HEADER_USER_ID_KEY) long requestorId,
                                                    @RequestParam(name = "from", required = false) Integer from,
                                                    @RequestParam(name = "size", required = false) Integer size) {
        return itemRequestService.getAllByRequestorId(requestorId, from, size);
    }

    @GetMapping(path = "/{requestId}", name = "requestId")
    public ItemRequestDto getById(@RequestHeader(value = HEADER_USER_ID_KEY) long ownerId,
                                  @PathVariable long requestId) {
        return itemRequestService.getById(ownerId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader(value = HEADER_USER_ID_KEY) long userId,
                                       @RequestParam(name = "from", required = false) Integer from,
                                       @RequestParam(name = "size", required = false) Integer size) {
        return itemRequestService.getAll(userId, from, size);
    }

    @PostMapping
    public ItemRequestDto add(@RequestHeader(value = HEADER_USER_ID_KEY) long requestorId,
                              @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestService.add(itemRequestDto, requestorId);
    }
}
