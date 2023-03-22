package ru.practicum.shareit.error;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.model.BadRequestException;
import ru.practicum.shareit.error.model.ErrorResponse;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ErrorHandlerTest {
    private Mock mock;

    private ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleBadRequestException_shouldReturnErrorResponse() {
        ErrorResponse errorResponse = errorHandler.handleBadRequestException(new BadRequestException("test"));
        assertEquals(errorResponse.getError(), "test");
    }

    @Test
    void handleEntityNotFoundException_shouldReturnErrorResponse() {
        ErrorResponse errorResponse = errorHandler.handleEntityNotFoundException(new EntityNotFoundException("test"));
        assertEquals(errorResponse.getError(), "test");
    }
}
