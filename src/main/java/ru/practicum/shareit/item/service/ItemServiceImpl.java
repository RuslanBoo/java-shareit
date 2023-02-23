package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.error.model.BadRequestException;
import ru.practicum.shareit.error.model.DataNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserServiceImpl userService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Override
    public ItemDto getById(long itemId, long userId) {
        Item item = findById(itemId);
        setBookings(item, userId);
        setComments(item);

        return itemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getByOwner(long ownerId) {
        return itemRepository.findAll()
                .stream()
                .filter(item -> isOwner(item, ownerId))
                .map(item -> setBookings(item, ownerId))
                .map(this::setComments)
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String query) {
        if (query == null) {
            throw new BadRequestException("Invalid search text");
        }

        if (query.isBlank()) {
            return new ArrayList<>();
        }

        return itemRepository.findAllByText(query)
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto add(ItemDto itemDto, long ownerId) {
        Item item = prepareDao(itemDto);
        item.setOwner(userService.findById(ownerId));

        return prepareDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(long itemId, ItemDto itemDto, long ownerId) {
        checkOwnerPermission(itemId, ownerId);
        partialUpdate(itemId, itemDto);

        return prepareDto(itemRepository.getById(itemId));
    }

    @Override
    public void delete(long itemId, long ownerId) {
        Item item = findById(itemId);
        checkOwnerPermission(itemId, ownerId);
        itemRepository.delete(item);
    }

    @Override
    public CommentDto commentSave(long itemId, long userId, CommentDto commentDto) {
        Optional<Item> item = itemRepository.findById(itemId);
        User user = userService.findById(userId);
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), null);

        if (item.isEmpty()) {
            throw new DataNotFoundException("Item not found");
        }

        if (bookings.isEmpty()) {
            throw new BadRequestException("User haven't booking for item");
        }

        Comment comment = commentMapper.fromDto(commentDto);
        comment.setItem(item.get());
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        return commentMapper.toDto(commentRepository.save(comment));
    }

    private void partialUpdate(long itemId, ItemDto itemDto) {
        Item updatedItem = findById(itemId);
        itemMapper.updateItem(itemDto, updatedItem);

        itemRepository.save(updatedItem);
    }

    private void checkOwnerPermission(long itemId, long ownerId) {
        Item item = findById(itemId);
        if (item.getOwner() == null || item.getOwner().getId() != ownerId) {
            throw new DataNotFoundException("Invalid owner for this item");
        }
    }

    private Item findById(long itemId) {
        Item item = itemRepository.getById(itemId);

        if (item == null) {
            throw new DataNotFoundException("Item not found");
        }

        return item;
    }

    private boolean isOwner(Item item, long ownerId) {
        return item.getOwner().getId() == ownerId;
    }

    private Item prepareDao(ItemDto itemDto) {
        return itemMapper.fromDto(itemDto);
    }

    private ItemDto prepareDto(Item item) {
        return itemMapper.toDto(item);
    }

    private Item setBookings(Item item, long userId) {
        if (item.getOwner().getId() == userId) {
            item.setNextBooking(bookingMapper.toShortBookingDto(getNextBooking(item)));
            item.setLastBooking(bookingMapper.toShortBookingDto(getLastBooking(item)));
        }

        return item;
    }

    private Item setComments(Item item) {
        List<CommentDto> commentsDto = commentRepository.findAllByItemId(item.getId())
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());

        item.setComments(commentsDto);

        return item;
    }

    private Booking getNextBooking(Item item) {
        Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now());

        return nextBooking.orElse(null);
    }

    private Booking getLastBooking(Item item) {
        Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndEndBeforeOrderByEndDesc(item.getId(), LocalDateTime.now());

        return lastBooking.orElse(null);
    }
}
