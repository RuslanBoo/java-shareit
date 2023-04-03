package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.error.model.DataNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.pagination.PaginationService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.testUtils.Helper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private PaginationService paginationService;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Spy
    private ItemRequestMapper itemRequestMapper = Mappers.getMapper(ItemRequestMapper.class);

    private long itemRequestId;
    private long userId;
    private User user;
    private List<ItemRequest> list;
    private List<ItemRequestDto> listDto;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        itemRequestId = 1L;
        userId = 1L;
        user = Helper.createUser(userId);

        itemRequestDto = ItemRequestDto.builder()
                .id(itemRequestId)
                .created(LocalDateTime.now())
                .description("test")
                .build();

        list = List.of(
                Helper.createItemRequest(1L, user),
                Helper.createItemRequest(2L, user),
                Helper.createItemRequest(3L, user)
        );

        listDto = List.of(
                Helper.createItemRequestDto(1L, user),
                Helper.createItemRequestDto(2L, user),
                Helper.createItemRequestDto(3L, user)
        );
    }

    @Test
    void add_shouldReturnDataNotFoundException() {
        when(userService.findById(anyLong())).thenThrow(DataNotFoundException.class);

        assertThatThrownBy(() -> itemRequestService.add(itemRequestDto, 1L))
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void add_shouldReturnItemRequestDto() {
        when(userService.findById(anyLong())).thenReturn(user);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenAnswer(i -> i.getArguments()[0]);
        ItemRequestDto result = itemRequestService.add(itemRequestDto, 1L);
        itemRequestDto.setCreated(result.getCreated());

        assertEquals(result, itemRequestDto);
    }

    @Test
    void getAllByRequestorId_shouldReturnDataNotFoundException() {
        when(userService.findById(anyLong())).thenThrow(DataNotFoundException.class);

        assertThatThrownBy(() -> itemRequestService.getAllByRequestorId(1L, 1, 1))
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void getAllByRequestorId_shouldReturnListOfItemRequestDto() {
        when(userService.findById(anyLong())).thenReturn(user);
        when(paginationService.getPageable(anyInt(), anyInt())).thenReturn(PageRequest.of(1, 10));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of());
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(anyLong(), any(PageRequest.class))).thenReturn(list);

        assertEquals(itemRequestService.getAllByRequestorId(userId, 1, 10).size(), listDto.size());
    }

    @Test
    void getAll_shouldReturnDataNotFoundException() {
        when(userService.findById(anyLong())).thenThrow(DataNotFoundException.class);

        assertThatThrownBy(() -> itemRequestService.getAll(userId, 1, 1))
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void getAll_shouldReturnListOfItemRequestDto() {
        when(userService.findById(anyLong())).thenReturn(user);
        when(paginationService.getPageable(anyInt(), anyInt())).thenReturn(PageRequest.of(1, 10));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of());
        when(itemRequestRepository.findAllByRequestorIdIsNotOrderByCreatedDesc(anyLong(), any(PageRequest.class))).thenReturn(list);

        assertEquals(itemRequestService.getAll(userId, 1, 10).size(), listDto.size());
    }

    @Test
    void getById_shouldReturnDataNotFoundException() {
        when(itemRequestRepository.findById(anyLong())).thenThrow(DataNotFoundException.class);

        assertThatThrownBy(() -> itemRequestService.getById(itemRequestId, userId))
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void getById_shouldReturnItemRequestDto() {
        ItemRequest itemRequest = Helper.createItemRequest(1L, user);
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .created(itemRequest.getCreated())
                .items(List.of())
                .description(itemRequest.getDescription())
                .build();
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        assertEquals(itemRequestService.getById(itemRequestId, userId), itemRequestDto);
    }
}