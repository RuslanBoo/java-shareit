package ru.practicum.shareit.error.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse {
    private final String error;

    public ErrorResponse(String errorMessage) {
        this.error = errorMessage;
    }
}
