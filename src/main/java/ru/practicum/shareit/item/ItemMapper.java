package ru.practicum.shareit.item;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = "spring")
public interface ItemMapper {
    ItemDto toDto(Item item);

    @Mapping(target = "id", ignore = true)
    Item fromDto(ItemDto itemDto);

    @InheritConfiguration
    void updateItem(ItemDto itemDto, @MappingTarget Item item);
}
