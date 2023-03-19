package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.testUtils.Helper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class CommentMapperTest {
    private Mock mock;

    private CommentMapperImpl commentMapper = new CommentMapperImpl();

    @Test
    void toDto_shouldReturnCommentDto() {
        long itemId = 0L;
        long userId = 0L;
        long commentId = 0L;
        LocalDateTime date = LocalDateTime.now();
        Item item = Helper.createItem(itemId, userId);
        User user = Helper.createUser(userId);

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
        long itemId = 0L;
        long userId = 0L;
        long commentId = 0L;
        LocalDateTime date = LocalDateTime.now();
        Item item = Helper.createItem(itemId, userId);
        User user = Helper.createUser(userId);

        Comment comment = Helper.createComment(0L, item, user);
        comment.setAuthor(null);

        assertNull(commentMapper.toDto(comment).getAuthorName());
    }

    @Test
    void toDto_shouldReturnCommentAuthorNameNull3() {
        long itemId = 0L;
        long userId = 0L;
        long commentId = 0L;
        LocalDateTime date = LocalDateTime.now();
        Item item = Helper.createItem(itemId, userId);
        User user = Helper.createUser(userId);
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
        long itemId = 0L;
        long userId = 0L;
        long commentId = 0L;
        LocalDateTime date = LocalDateTime.now();
        Item item = Helper.createItem(itemId, userId);
        User user = Helper.createUser(userId);

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
