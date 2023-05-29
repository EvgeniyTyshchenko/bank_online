package ru.bankonline.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bankonline.project.entity.Transaction;

import java.util.List;

/***
 * Репозиторий для работы с транзакциями
 */
@Repository
public interface TransactionsRepository extends JpaRepository<Transaction, Integer> {

    /***
     * Возвращает список транзакций, принадлежащих указанному идентификатору клиента
     * @param customerId ID клиента
     * @return список транзакций
     */
    List<Transaction> findAllByCustomerId(Integer customerId);
}