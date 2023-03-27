package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private long id;
    private String name;
    private String description;
    private Boolean available;

    @JoinColumn(name = "user_id")
    @ManyToOne(cascade = CascadeType.ALL)
    private User owner;

    @Transient
    private ShortBookingDto lastBooking;

    @Transient
    private ShortBookingDto nextBooking;

    @Transient
    private List<CommentDto> comments;

    private Long requestId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id && Objects.equals(name, item.name) && Objects.equals(description, item.description) && Objects.equals(available, item.available) && Objects.equals(owner, item.owner) && Objects.equals(lastBooking, item.lastBooking) && Objects.equals(nextBooking, item.nextBooking) && Objects.equals(comments, item.comments) && Objects.equals(requestId, item.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, available, owner, lastBooking, nextBooking, comments, requestId);
    }
}
