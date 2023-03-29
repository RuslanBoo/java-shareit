package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.error.ErrorHandler;
import ru.practicum.shareit.error.model.BadRequestException;
import ru.practicum.shareit.error.model.DataNotFoundException;
import ru.practicum.shareit.testUtils.Helper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private MockMvc mockMvc;

    @Mock
    private BookingServiceImpl bookingService;

    @InjectMocks
    private BookingController bookingController;

    private long userId;
    private User user;
    private BookingDto bookingDto;
    private List<BookingDto> emptyList;
    private List<BookingDto> list;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .setControllerAdvice(ErrorHandler.class)
                .build();

        userId = 0L;
        user = Helper.createUser(0L);
        bookingDto = Helper.createBookingDto(0L, 0L, user);
        emptyList = new ArrayList<>();
        list = List.of(
                Helper.createBookingDto(1L, 0L, user),
                Helper.createBookingDto(2L, 0L, user),
                Helper.createBookingDto(3L, 0L, user)
        );
    }

    @SneakyThrows
    @Test
    void getAllByBooker_shouldReturnEmptyList() {
        when(bookingService.getAllByBooker(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(emptyList);

        mockMvc.perform(get("/bookings")
                        .header(Helper.HEADER_USER_ID, String.valueOf(userId))
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(emptyList)));
    }

    @SneakyThrows
    @Test
    void getAllByBooker_shouldReturnListOfBookingDto() {
        when(bookingService.getAllByBooker(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(list);

        mockMvc.perform(get("/bookings")
                        .header(Helper.HEADER_USER_ID, String.valueOf(userId))
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(list)));
    }

    @SneakyThrows
    @Test
    void getAllByBooker_shouldReturnBadRequestException() {
        when(bookingService.getAllByBooker(anyLong(), anyString(), anyInt(), anyInt())).thenThrow(new BadRequestException("Unknown state: UNSUPPORTED_STATUS"));

        mockMvc.perform(get("/bookings")
                        .header(Helper.HEADER_USER_ID, String.valueOf(userId))
                        .param("state", "1")
                        .param("from", "1")
                        .param("size", "10")
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllByBooker_shouldReturnDataNotFoundException() {
        when(bookingService.getAllByBooker(anyLong(), anyString(), anyInt(), anyInt())).thenThrow(new DataNotFoundException("User not found"));

        mockMvc.perform(get("/bookings")
                        .header(Helper.HEADER_USER_ID, String.valueOf(userId))
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "10")
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getAllByOwner_shouldReturnListOfBookingDto() {

        when(bookingService.getAllByOwner(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(list);

        mockMvc.perform(get("/bookings/owner")
                        .header(Helper.HEADER_USER_ID, String.valueOf(userId))
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(list)));
    }

    @SneakyThrows
    @Test
    void getAllByOwner_shouldReturnEmptyList() {
        when(bookingService.getAllByOwner(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(emptyList);

        mockMvc.perform(get("/bookings/owner")
                        .header(Helper.HEADER_USER_ID, String.valueOf(userId))
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(emptyList)));
    }

    @SneakyThrows
    @Test
    void getAllByOwner_shouldReturnBadRequestException() {
        when(bookingService.getAllByOwner(anyLong(), anyString(), anyInt(), anyInt())).thenThrow(new BadRequestException("Unknown state: UNSUPPORTED_STATUS"));

        mockMvc.perform(get("/bookings/owner")
                        .header(Helper.HEADER_USER_ID, String.valueOf(userId))
                        .param("state", "1")
                        .param("from", "1")
                        .param("size", "10")
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllByOwner_shouldReturnDataNotFoundException() {
        when(bookingService.getAllByOwner(anyLong(), anyString(), anyInt(), anyInt())).thenThrow(new DataNotFoundException("User not found"));

        mockMvc.perform(get("/bookings/owner")
                        .header(Helper.HEADER_USER_ID, String.valueOf(userId))
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "10")
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getById_shouldReturnDataNotFoundException() {
        when(bookingService.getById(anyLong(), anyLong())).thenThrow(new DataNotFoundException("Booking not found"));

        mockMvc.perform(get("/bookings/{bookingId}", 0L)
                        .header(Helper.HEADER_USER_ID, String.valueOf(userId))
                )
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getById_shouldReturnBookingDto() {
        when(bookingService.getById(anyLong(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", 0L)
                        .header(Helper.HEADER_USER_ID, String.valueOf(userId))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)));
    }

    @SneakyThrows
    @Test
    void create_shouldReturnBookingDto() {
        String json = objectMapper.writeValueAsString(bookingDto);

        when(bookingService.create(anyLong(), any(BookingDto.class))).thenAnswer(i -> i.getArguments()[1]);

        mockMvc.perform(post("/bookings")
                        .header(Helper.HEADER_USER_ID, String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @SneakyThrows
    @Test
    void update_shouldReturnBookingDto() {
        when(bookingService.update(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(Helper.HEADER_USER_ID, String.valueOf(userId))
                        .param("approved", "true")
                )
                .andExpect(status().isOk());
    }
}