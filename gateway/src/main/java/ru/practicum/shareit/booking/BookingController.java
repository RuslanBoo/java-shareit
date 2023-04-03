package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private static final String HEADER_USER_ID_KEY = "X-Sharer-User-Id";
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getAllByBooker(
			@RequestHeader(name = HEADER_USER_ID_KEY) long bookerId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", required = false) Integer from,
			@Positive @RequestParam(name = "size", required = false) Integer size
	) {
		BookingState state = checkBookingState(stateParam);
		return bookingClient.getAllByBooker(bookerId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllByOwner(
			@RequestHeader(name = HEADER_USER_ID_KEY) long ownerId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", required = false) Integer from,
			@Positive @RequestParam(name = "size", required = false) Integer size
	) {
		BookingState state = checkBookingState(stateParam);
		return bookingClient.getAllByOwner(ownerId, state, from, size);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getById(@PathVariable long bookingId,
										  @RequestHeader(name = HEADER_USER_ID_KEY) long userId) {
		return bookingClient.getById(bookingId, userId);
	}

	@PostMapping
	public ResponseEntity<Object> create(@RequestHeader(name = HEADER_USER_ID_KEY) long userId,
										 @Valid @RequestBody ShortBookingDto bookingDto) {
		return bookingClient.create(userId, bookingDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> update(
			@PathVariable long bookingId,
			@RequestHeader(name = HEADER_USER_ID_KEY) long ownerId,
			@RequestParam boolean approved
	) {
		return bookingClient.update(bookingId, ownerId, approved);
	}

	private BookingState checkBookingState(String stateParam) {
		return BookingState.from(stateParam).orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
	}
}
