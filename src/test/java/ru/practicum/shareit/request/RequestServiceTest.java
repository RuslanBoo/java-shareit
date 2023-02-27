package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.model.DataNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.TestHelper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Spy
    private ItemRequestMapper requestMapper = Mappers.getMapper(ItemRequestMapper.class);

    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private ItemRequestService requestService;

    @Test
    void createRequest_shouldCreateNewRequest() {
        long userId = 1;
        User user = TestHelper.createUser(userId);
        ItemRequestDto createRequestDto = ItemRequestDto.builder()
                .description("New request")
                .build();

        when(userService.getById(userId)).thenReturn(userMapper.toDto(user));
        when(requestRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemRequestDto requestDto = requestService.add(createRequestDto, userId);

        assertThat(requestDto.getDescription()).isEqualTo(createRequestDto.getDescription());
        assertThat(requestDto.getCreated()).isBefore(LocalDateTime.now());
        assertThat(requestDto.getItems()).isNull();
    }

    @Test
    void createRequest_shouldThrowNotFoundExceptionIfUserIsNotExists() {
        long userId = 1;
        ItemRequestDto createRequestDto = ItemRequestDto.builder()
                .description("New request")
                .build();

        when(userService.getById(userId)).thenThrow(DataNotFoundException.class);

        assertThatThrownBy(() -> {
            requestService.add(createRequestDto, userId);
        }).isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void getOwnRequests_shouldReturnListOfRequests() {
        long userId = 1;
        User user = TestHelper.createUser(userId);
        List<ItemRequest> requests = List.of(
                TestHelper.createItemRequest(1, LocalDateTime.now(), user),
                TestHelper.createItemRequest(2, LocalDateTime.now(), user),
                TestHelper.createItemRequest(3, LocalDateTime.now(), user)
        );

        when(userService.getById(userId)).thenReturn(userMapper.toDto(user));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(Collections.emptyList());
        when(requestRepository.findAllByRequestorIdOrderByCreatedDesc(anyLong())).thenReturn(requests);

        assertThat(requestService.getAllByRequestorId(userId, null, null)).isEqualTo(requests
                .stream()
                .map(requestMapper::toDto)
                .map(requestDto -> {
                    requestDto.setItems(Collections.emptyList());
                    return requestDto;
                }).collect(Collectors.toList()));
    }

    @Test
    void getOtherRequests_shouldReturnListOfRequests() {
        long userId = 1;
        User user = TestHelper.createUser(userId);
        List<ItemRequest> requests = List.of(
                TestHelper.createItemRequest(1, LocalDateTime.now(), user),
                TestHelper.createItemRequest(2, LocalDateTime.now(), user),
                TestHelper.createItemRequest(3, LocalDateTime.now(), user)
        );

        when(userService.getById(userId)).thenReturn(userMapper.toDto(user));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(Collections.emptyList());
        when(requestRepository.findAllByRequestorIdIsNotOrderByCreatedDesc(anyLong(), any())).thenReturn(requests);

        assertThat(requestService.getAll(userId, null, null)).isEqualTo(requests
                .stream()
                .map(requestMapper::toDto)
                .map(requestDto -> {
                    requestDto.setItems(Collections.emptyList());
                    return requestDto;
                }).collect(Collectors.toList()));
    }

    @Test
    void getById_shouldReturnRequest() {
        long requestId = 1;
        long userId = 1;
        User user = TestHelper.createUser(userId);
        ItemRequest request = TestHelper.createItemRequest(1, LocalDateTime.now(), user);
        ItemRequestDto requestDto = requestMapper.toDto(request);
        requestDto.setItems(Collections.emptyList());

        when(userService.getById(userId)).thenReturn(userMapper.toDto(user));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(Collections.emptyList());
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));

        assertThat(requestService.getById(requestId, userId)).isEqualTo(requestDto);
    }
}
