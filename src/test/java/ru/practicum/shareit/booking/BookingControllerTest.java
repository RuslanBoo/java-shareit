package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.ErrorHandler;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .setControllerAdvice(ErrorHandler.class)
                .build();
    }

    @Test
    void getAllByBookerTest() throws Exception {
        long bookerId = 1;

        when(bookingService.getAllByBooker(anyLong(), any(), any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings").header(USER_ID_HEADER, bookerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.emptyList())));
    }

    @Test
    void getAllByOwnerTest() throws Exception {
        long bookerId = 1;

        when(bookingService.getAllByOwner(anyLong(), any(), any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings/owner").header(USER_ID_HEADER, bookerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.emptyList())));
    }

    @Test
    void getByIdTest() throws Exception {
        long bookingId = 1;
        long bookerId = 1;
        BookingDto bookingDto = BookingDto.builder()
                .id(bookingId)
                .build();

        when(bookingService.getById(anyLong(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/" + bookingId).header(USER_ID_HEADER, bookerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)));
    }

    @Test
    void createTest() throws Exception {
        long bookingId = 1;
        long bookerId = 1;

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .build();
        String json = objectMapper.writeValueAsString(bookingDto);

        mockMvc.perform(
                        post("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(USER_ID_HEADER, bookerId)
                                .content(json))
                .andExpect(status().isOk());

        Booking booking = new Booking(bookingId, null, null, null, null, null);
        bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .build();
        json = objectMapper.writeValueAsString(bookingDto);

        mockMvc.perform(
                        post("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(USER_ID_HEADER, bookerId)
                                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTest() throws Exception {
        long bookingId = 1;
        long bookerId = 1;
        BookingDto bookingDto = BookingDto.builder()
                .id(bookingId)
                .build();

        when(bookingService.update(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/" + bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, bookerId)
                        .queryParam("approved", "true")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)));
    }
}
