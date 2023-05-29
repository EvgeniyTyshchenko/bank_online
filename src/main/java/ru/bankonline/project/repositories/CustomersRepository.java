package ru.bankonline.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.bankonline.project.entity.Customer;

import java.util.Optional;

/***
 * Репозиторий для работы с клиентами
 */
@Repository
public interface CustomersRepository extends JpaRepository<Customer, Integer> {

    /***
     * Находит клиента по серии и номеру паспорта
     * @param passportSeries серия паспорта
     * @param passportNumber номер паспорта
     * @return клиента (если найден)
     */
    Optional<Customer> findByPassportSeriesAndPassportNumber(Integer passportSeries, Integer passportNumber);

    /***
     * Находит клиента по номеру карты
     * @param cardNumber номер карты
     * @return клиента (если найден)
     */
    @Query("SELECT c FROM Customer c JOIN c.cards card WHERE card.cardNumber = :cardNumber")
    Optional<Customer> findByCardNumber(String cardNumber);

    /***
     * Находит клиента по номеру сберегательного счета
     * @param accountNumber номер сберегательного счета
     * @return клиента (если найден)
     */
    @Query("SELECT c FROM Customer c JOIN c.savingsAccounts savingAccount WHERE savingAccount.accountNumber = :accountNumber")
    Optional<Customer> findBySavingAccountNumber(String accountNumber);

    /***
     * Находит количество дубликатов паспорта (по серии и номеру)
     * @param passportSeries серия паспорта
     * @param passportNumber номер паспорта
     * @return количество дубликатов паспорта
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.passportSeries = :passportSeries AND c.passportNumber = :passportNumber")
    Optional<Integer> findPassportDuplicates(Integer passportSeries, Integer passportNumber);
}