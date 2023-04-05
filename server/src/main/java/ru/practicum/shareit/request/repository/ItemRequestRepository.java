package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    public List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(long requestorId);

    public List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(long requestorId, Pageable pageable);

    public List<ItemRequest> findAllByRequestorIdIsNotOrderByCreatedDesc(long requestorId);

    public List<ItemRequest> findAllByRequestorIdIsNotOrderByCreatedDesc(long requestorId, Pageable pageable);
}
