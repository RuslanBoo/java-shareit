package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String HEADER_USER_ID_KEY = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @GetMapping
    public List<BookingDto> getUserBookingsByState(
            @RequestParam(name = "state", defaultValue = "ALL") @Valid String bookingState,
            @RequestHeader(value = HEADER_USER_ID_KEY) Long userId) {
        return bookingService.getUserBookingsByState(userId, bookingState);
    }

    @PostMapping
    public BookingDto add(@RequestBody @Valid BookingDto bookingDto,
                          @RequestHeader(value = HEADER_USER_ID_KEY) Long userId) {
        return bookingService.add(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeStatus(@PathVariable(name = "bookingId") Long bookingId,
                                   @RequestParam(name = "approved") @Valid boolean approved,
                                   @RequestHeader(value = HEADER_USER_ID_KEY) Long ownerId) {
        return bookingService.changeStatus(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable(name = "bookingId") Long bookingId,
                                   @RequestHeader(value = HEADER_USER_ID_KEY) Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsForUserItemsByState(
            @RequestParam(name = "state", defaultValue = "ALL") @Valid() String bookingState,
            @RequestHeader(value = HEADER_USER_ID_KEY) Long userId) {
        return bookingService.getOwnerBookingsByState(userId, bookingState);
    }
}
