package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @GetMapping
    public List<BookingDto> getAllByBooker(
            @RequestHeader(name = USER_ID) long bookerId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        return bookingService.getAllByBooker(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(
            @RequestHeader(name = USER_ID) long ownerId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        return bookingService.getAllByOwner(ownerId, state);
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
