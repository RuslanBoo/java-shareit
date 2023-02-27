package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.TestHelper;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        assertThat(em).isNotNull();
    }

    @Test
    void shouldReturn() {
        EntityManager entityManager = em.getEntityManager();
        TypedQuery<Item> query = entityManager
                .createQuery("select i " +
                        "from Item i " +
                        "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
                        "or upper(i.description) like upper(concat('%', ?1, '%'))) " +
                        "and i.available is true", Item.class);
        User user = TestHelper.createUser(1);
        userRepository.save(user);

        Item item1 = new Item(1L, "test 1", "description 1", true, user, null, null, null, null);
        Item item2 = new Item(2L, "test 2", "description 2", true, user, null, null, null, null);
        Item item3 = new Item(3L, "test 3", "description 3", true, user, null, null, null, null);

        assertThat(query.setParameter(1, "aBc").getResultList()).isEmpty();

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        assertThat(itemRepository.findAllByText("test 1")).hasSize(2);
    }
}
