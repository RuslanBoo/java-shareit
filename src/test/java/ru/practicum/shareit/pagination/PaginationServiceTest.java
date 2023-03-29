package ru.practicum.shareit.pagination;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.error.model.BadRequestException;

import static org.junit.jupiter.api.Assertions.*;

class PaginationServiceTest {
    private final PaginationService paginationService = new PaginationService();

    @Test
    void getPageable_shouldReturnNull() {
        assertNull(paginationService.getPageable(null, null));
        assertNull(paginationService.getPageable(1, null));
        assertNull(paginationService.getPageable(null, 1));
    }

    @Test
    void getPageable_shouldReturnBadRequestException() {
        assertThrows(BadRequestException.class, () -> paginationService.getPageable(-1, 1));
        assertThrows(BadRequestException.class, () -> paginationService.getPageable(1, 0));
    }

    @Test
    void getPageable_shouldReturnPageRequest() {
        int from = 20;
        int size = 10;
        int pageNumber = from / size;

        assertInstanceOf(PageRequest.class, paginationService.getPageable(from, size));
        assertEquals(PageRequest.of(pageNumber, size), paginationService.getPageable(from, size));
    }
}