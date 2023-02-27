package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.TestHelper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceIntegrationTest {
    private final ItemService itemService;
    private final UserService userService;

    @Test
    void shouldCreateItemsAndGetByUserId() {
        ItemDto itemDto = TestHelper.createItemDto(true, null);

        UserDto user1 = userService.add(UserDto.builder().name("test name").email("test1@test.test").build());
        UserDto user2 = userService.add(UserDto.builder().name("test name").email("test2@test.test").build());

        itemService.add(itemDto, user1.getId());
        itemService.add(itemDto, user1.getId());
        itemService.add(itemDto, user1.getId());

        itemService.add(itemDto, user2.getId());

        List<ItemDto> items = itemService.getByOwner(user1.getId());

        assertThat(items).hasSize(3);
    }
}
