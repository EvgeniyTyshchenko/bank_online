package ru.bankonline.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bankonline.project.entity.SavingsAccount;

import java.util.List;

/***
 * Репозиторий для работы со сберегательными счетами
 */
@Repository
public interface SavingsAccountsRepository extends JpaRepository<SavingsAccount, Integer> {

    /***
     * Возвращает список сберегательных счетов, принадлежащих указанному идентификатору клиента
     * @param customerId ID клиента
     * @return список сберегательных счетов
     */
    List<SavingsAccount> findByCustomerId(Integer customerId);
}