package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.validation.DatesValidation;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@Builder
@DatesValidation(message = "Date start must be earlier date end")
public class BookingDto {
    private Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long itemId;
    private ItemDto item;
    private UserDto booker;

    @FutureOrPresent
    private LocalDateTime start;
    @Future
    private LocalDateTime end;
    private BookingStatus status;
}
