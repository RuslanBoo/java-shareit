package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.error.ErrorHandler;
import ru.practicum.shareit.error.model.DataNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.utils.TestHelper;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private MockMvc mockMvc;

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .setControllerAdvice(ErrorHandler.class)
                .build();
    }

    @Test
    void createRequest_shouldReturnNewRequest() throws Exception {
        long userId = 1;
        ItemRequestDto request = TestHelper.createItemRequestDto(1);
        ItemRequestDto dto = ItemRequestDto.builder()
                .description((request.getDescription()))
                .build();
        String json = objectMapper.writeValueAsString(dto);

        when(itemRequestService.add(dto, userId)).thenReturn(request);

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(request)));
    }

    @Test
    void getOwnRequests_shouldReturnListOfRequests() throws Exception {
        long userId = 1;
        List<ItemRequestDto> requests = List.of(
                TestHelper.createItemRequestDto(1),
                TestHelper.createItemRequestDto(2),
                TestHelper.createItemRequestDto(3)
        );

        when(itemRequestService.getAllByRequestorId(userId, null, null)).thenReturn(requests);

        mockMvc.perform(get("/requests").header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requests)));
    }

    @Test
    void getOtherRequests_shouldReturnListOfRequests() throws Exception {
        long userId = 1;
        List<ItemRequestDto> requests = List.of(
                TestHelper.createItemRequestDto(1),
                TestHelper.createItemRequestDto(2),
                TestHelper.createItemRequestDto(3)
        );

        when(itemRequestService.getAll(anyLong(), any(), any())).thenReturn(requests);

        mockMvc.perform(get("/requests/all").header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requests)));
    }

    @Test
    void getById_shouldReturnListOfRequests() throws Exception {
        long userId = 1;
        long requestId = 1;
        ItemRequestDto request = TestHelper.createItemRequestDto(requestId);

        when(itemRequestService.getById(requestId, userId)).thenReturn(request);

        mockMvc.perform(get("/requests/" + requestId).header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(request)));
    }

    @Test
    void getById_shouldReturnNotFound() throws Exception {
        long userId = 1;
        long requestId = 1;

        when(itemRequestService.getById(requestId, userId)).thenThrow(new DataNotFoundException("item request not found"));

        mockMvc.perform(get("/requests/" + requestId).header(USER_ID_HEADER, userId))
                .andExpect(status().isNotFound());
    }
}
