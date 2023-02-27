package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.TestHelper;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {
    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @Test
    void bookingToShortBookingDto() {
        User user = TestHelper.createUser(1);
        Item item = TestHelper.createItem(1, true, user);
        Booking booking = new Booking(1L, item, user, LocalDateTime.now(), LocalDateTime.now(), BookingStatus.WAITING);

        ShortBookingDto dto = bookingMapper.toShortBookingDto(booking);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getBookerId()).isEqualTo(1L);
    }
}
