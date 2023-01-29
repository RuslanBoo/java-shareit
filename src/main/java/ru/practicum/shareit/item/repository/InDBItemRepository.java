package ru.practicum.shareit.item.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
@Primary
public interface InDBItemRepository extends JpaRepository<Item, Long> {
    List<Item> getByOwner(User user);

    @Query("SELECT i FROM Item i WHERE i.name LIKE %:search_query% OR i.description LIKE %:search_query%")
    List<Item> searchByText(@Param("search_query") String search_query);
}