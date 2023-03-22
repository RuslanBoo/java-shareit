package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.model.DataNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.pagination.PaginationService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    private final UserService userService;
    private final PaginationService paginationService;

    @Override
    public ItemRequestDto add(ItemRequestDto itemRequestDto, long requestorId) {
        User requestor = userService.findById(requestorId);

        ItemRequest itemRequest = prepareDao(itemRequestDto);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());

        return prepareDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getAllByRequestorId(long requestorId, Integer from, Integer size) {
        userService.findById(requestorId);

        Pageable pageable = paginationService.getPageable(from, size);
        return itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(requestorId, pageable)
                .stream()
                .map(this::prepareDto)
                .map(this::setItems)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAll(long userId, Integer from, Integer size) {
        userService.findById(userId);
        Pageable pageable = paginationService.getPageable(from, size);

        return itemRequestRepository.findAllByRequestorIdIsNotOrderByCreatedDesc(userId, pageable)
                .stream()
                .map(this::prepareDto)
                .map(this::setItems)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(long userId, long requestId) {
        userService.findById(userId);
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(requestId);

        if (itemRequest.isEmpty()) {
            throw new DataNotFoundException("ItemRequest not found");
        }

        ItemRequestDto itemRequestDto = prepareDto(itemRequest.get());
        setItems(itemRequestDto);

        return itemRequestDto;
    }

    private ItemRequestDto setItems(ItemRequestDto itemRequestDto) {
        List<ItemDto> itemsIds = itemRepository.findAllByRequestId(itemRequestDto.getId())
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());

        itemRequestDto.setItems(itemsIds);

        return itemRequestDto;
    }

    private ItemRequestDto prepareDto(ItemRequest itemRequest) {
        return itemRequestMapper.toDto(itemRequest);
    }

    private ItemRequest prepareDao(ItemRequestDto itemRequestDto) {
        return itemRequestMapper.fromDto(itemRequestDto);
    }
}
