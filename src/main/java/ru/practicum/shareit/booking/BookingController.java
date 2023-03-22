package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.pagination.PaginationService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";

    @Autowired
    private final BookingService bookingService;
    private final PaginationService paginationService;

    @GetMapping
    public List<BookingDto> getAllByBooker(
            @RequestHeader(name = USER_ID) long bookerId,
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestParam(name = "from", required = false) Integer from,
            @RequestParam(name = "size", required = false) Integer size
    ) {
        return bookingService.getAllByBooker(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(
            @RequestHeader(name = USER_ID) long ownerId,
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestParam(name = "from", required = false) Integer from,
            @RequestParam(name = "size", required = false) Integer size
    ) {
        return bookingService.getAllByOwner(ownerId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable long bookingId, @RequestHeader(name = USER_ID) long userId) {
        return bookingService.getById(bookingId, userId);
    }

    @PostMapping
    public BookingDto create(@RequestHeader(name = USER_ID) long userId, @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(
            @PathVariable long bookingId,
            @RequestHeader(name = USER_ID) long ownerId,
            @RequestParam boolean approved
    ) {
        return bookingService.update(bookingId, ownerId, approved);
    }
}
