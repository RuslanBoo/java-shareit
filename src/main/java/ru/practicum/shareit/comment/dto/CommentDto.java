package ru.practicum.shareit.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private long id;

    @NotBlank(message = "Invalid comment text", groups = {CreateCommentDto.class})
    private String text;
    private String authorName;
    private LocalDateTime created;
}
