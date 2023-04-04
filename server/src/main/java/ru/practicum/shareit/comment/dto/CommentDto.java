package ru.practicum.shareit.comment.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Objects;

@Setter
@Getter
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class CommentDto {
    private long id;
    private String text;
    private String authorName;
    private LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentDto that = (CommentDto) o;
        return id == that.id && Objects.equals(text, that.text) && Objects.equals(authorName, that.authorName) && Objects.equals(created, that.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, authorName, created);
    }
}
