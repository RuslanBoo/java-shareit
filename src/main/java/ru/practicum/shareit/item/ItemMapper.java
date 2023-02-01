package ru.practicum.shareit.item;

import org.mapstruct.DecoratedWith;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.booking.ItemMapperDecorator;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = "spring", uses = {UserMapper.class})
@DecoratedWith(ItemMapperDecorator.class)
public interface ItemMapper {
    ItemDto toDto(Item item);

    @Mapping(target = "id", ignore = true)
    Item fromDto(ItemDto itemDto);

    @InheritConfiguration
    void updateItem(ItemDto itemDto, @MappingTarget Item item);
}
