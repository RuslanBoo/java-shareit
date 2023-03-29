package ru.practicum.shareit.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.testUtils.Helper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CommentMapperTest {
    private final CommentMapperImpl commentMapper = new CommentMapperImpl();

    private long itemId;
    private long userId;
    private long commentId;
    private User owner;
    private Item item;
    private User user;
    private LocalDateTime date;

    @BeforeEach
    void setUp() {
        itemId = 0L;
        userId = 0L;
        commentId = 0L;
        owner = Helper.createUser(userId);
        item = Helper.createItem(itemId, owner);
        user = Helper.createUser(userId);
        date = LocalDateTime.now();
    }

    @Test
    void toDto_shouldReturnCommentDto() {
        Comment comment = Helper.createComment(0L, item, user);
        comment.setCreated(date);

        CommentDto commentDto = CommentDto.builder()
                .id(commentId)
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(date)
                .build();

        assertEquals(commentDto, commentMapper.toDto(comment));
    }

    @Test
    void toDto_shouldReturnCommentAuthorNameNull2() {
        Comment comment = Helper.createComment(0L, item, user);
        comment.setAuthor(null);

        assertNull(commentMapper.toDto(comment).getAuthorName());
    }

    @Test
    void toDto_shouldReturnCommentAuthorNameNull3() {
        user.setName(null);

        Comment comment = Helper.createComment(0L, item, user);
        comment.setAuthor(null);

        assertNull(commentMapper.toDto(comment).getAuthorName());
    }

    @Test
    void toDto_shouldReturnNull() {
        assertNull(commentMapper.toDto(null));
    }

    @Test
    void fromDto_shouldReturnComment() {
        Comment comment = Comment.builder()
                .id(commentId)
                .text("test")
                .created(date)
                .build();
        comment.setCreated(date);

        CommentDto commentDto = CommentDto.builder()
                .id(commentId)
                .text(comment.getText())
                .created(date)
                .build();

        assertEquals(comment, commentMapper.fromDto(commentDto));
    }

    @Test
    void fromDto_shouldReturnNull() {
        assertNull(commentMapper.fromDto(null));
    }
}
