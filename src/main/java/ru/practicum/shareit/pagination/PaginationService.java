package ru.practicum.shareit.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.model.BadRequestException;

@Service
public class PaginationService {
    public Pageable getPageable(Integer from, Integer size) {
        if (from == null || size == null) {
            return null;
        }

        if (from < 0 || size <= 0) {
            throw new BadRequestException("Invalid value of from or size param");
        }

        int pageNumber = from / size;

        return PageRequest.of(pageNumber, size);
    }
}
