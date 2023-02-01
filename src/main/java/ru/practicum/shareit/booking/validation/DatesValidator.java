package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class DatesValidator implements ConstraintValidator<DatesValidation, BookingDto> {
    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext context) {
        LocalDateTime startDate = bookingDto.getStart();
        LocalDateTime endDate = bookingDto.getEnd();

        if (startDate == null || endDate == null) {
            return true;
        }

        return startDate.isBefore(endDate);
    }
}
