package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto add(ItemRequestDto itemRequestDto, long ownerId);

    List<ItemRequestDto> getAllByRequestorId(long requestorId, Integer from, Integer size);

    List<ItemRequestDto> getAll(long userId, Integer from, Integer size);

    ItemRequestDto getById(long ownerId, long requestId);
}
