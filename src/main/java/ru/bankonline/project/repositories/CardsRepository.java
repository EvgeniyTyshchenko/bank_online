package ru.bankonline.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bankonline.project.entity.Card;

import java.util.List;

/***
 * Репозиторий для работы с картами
 */
@Repository
public interface CardsRepository extends JpaRepository<Card, Integer> {

    /***
     * Находит все карты, принадлежащие заданному идентификатору клиента
     * @param customerId ID клиента
     * @return список карт, принадлежащих заданному клиенту
     */
    List<Card> findByCustomerId(Integer customerId);
}