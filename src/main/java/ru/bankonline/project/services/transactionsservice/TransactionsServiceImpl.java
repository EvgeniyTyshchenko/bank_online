package ru.bankonline.project.services.transactionsservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bankonline.project.entity.Card;
import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.entity.SavingsAccount;
import ru.bankonline.project.entity.Transaction;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.constants.TransactionType;
import ru.bankonline.project.repositories.TransactionsRepository;
import ru.bankonline.project.services.customersservice.CustomersService;
import ru.bankonline.project.utils.exceptions.NotFoundInBaseException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/***
 * Сервис для работы с транзакциями
 */
@Slf4j
@Service
public class TransactionsServiceImpl implements TransactionsService {

    private final TransactionsRepository transactionsRepository;
    private final CustomersService customersService;
    private static final String closure = "[closure]";
    private static final String discovery = "[discovery]";
    private static final String bank = "[BANK]";

    @Autowired
    public TransactionsServiceImpl(TransactionsRepository transactionsRepository, CustomersService customersService) {
        this.transactionsRepository = transactionsRepository;
        this.customersService = customersService;
    }

    /***
     * Получает транзакции клиента
     * @param passportSeries серия паспорта
     * @param passportNumber номер паспорта
     * @return список транзакций клиента
     * @throws NotFoundInBaseException если список транзакций пуст
     */
    @Override
    @Transactional
    public List<Transaction> getTransactionCustomer(Integer passportSeries, Integer passportNumber) {
        Customer customer = customersService.customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        List<Transaction> transactions = transactionsRepository.findAllByCustomerId(customer.getCustomerId());
        if (transactions.isEmpty()) {
            throw new NotFoundInBaseException("Список транзакций пуст.");
        }
        transactionRequestingTheEntireListOfOperationsOfSpecificCustomer(customer);
        log.info("Запрос по серии {} и номеру {} паспорта, для получения всего списка транзакций " +
                "- произведен.", passportSeries, passportNumber);
        return transactions;
    }

    /***
     * Создаёт транзакцию для регистрации нового клиента
     * @param customerId идентификатор нового клиента
     */
    @Override
    public void transactionToRegisterNewCustomer(Integer customerId) {
        Transaction transaction = new Transaction(customerId, "[registration]", "[registration]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.REGISTERCUSTOMER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    /***
     * Создаёт транзакцию для закрытия учетной записи клиента
     * @param customerId идентификатор клиента
     */
    @Override
    public void transactionToCloseCustomer(Integer customerId) {
        Transaction transaction = new Transaction(customerId, closure, closure,
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.CLOSEDCUSTOMER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    /***
     * Создаёт транзакцию отправки денежных средств с карты на карту
     * @param senderCustomer объект Customer, отправитель
     * @param senderCard объект Card, карта отправителя
     * @param recipientCard объект Card, карта получателя
     * @param amount количество
     */
    @Override
    public void moneySendingToTheCardTransaction(Customer senderCustomer, Card senderCard, Card recipientCard, BigDecimal amount) {
        Transaction transaction = new Transaction(senderCustomer.getCustomerId(), senderCard.getAccountNumber(), recipientCard.getAccountNumber(),
                amount, senderCard.getCurrency(), TransactionType.OUTTRANSFER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    /***
     * Создаёт транзакцию поступления денежных средств с карты на карту
     * @param recipientCustomer объект Customer, получатель
     * @param senderCard объект Card, карта отправителя
     * @param recipientCard объект Card, карта получателя
     * @param amount количество
     */
    @Override
    public void moneyReceiptToTheCardTransaction(Customer recipientCustomer, Card senderCard, Card recipientCard, BigDecimal amount) {
        Transaction transaction = new Transaction(recipientCustomer.getCustomerId(), senderCard.getAccountNumber(), recipientCard.getAccountNumber(),
                amount, senderCard.getCurrency(), TransactionType.INTRANSFER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    /***
     * Создаёт транзакцию закрытия карты
     * @param customerId идентификатор клиента
     */
    @Override
    public void transactionToCloseCard(Integer customerId) {
        Transaction transaction = new Transaction(customerId, closure, closure,
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.CLOSECARD, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    /***
     * Создаёт транзакцию разблокировки карты
     * @param customerId идентификатор клиента
     * @param card объект Card
     */
    @Override
    public void transactionToUnlockCard(Integer customerId, Card card) {
        Transaction transaction = new Transaction(customerId, "[unblocking]", "[unblocking]",
                card.getBalance(), Currency.RUB, TransactionType.UNLOCKINGCARD, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    /***
     * Создаёт транзакцию блокировки карты
     * @param customerId идентификатор клиента
     * @param card объект Card
     */
    @Override
    public void transactionToBlockCard(Integer customerId, Card card) {
        Transaction transaction = new Transaction(customerId, "[blocking]", "[blocking]",
                card.getBalance(), Currency.RUB, TransactionType.BLOCKINGCARD, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    /***
     * Создаёт транзакцию открытия карты
     * @param customer объект Customer
     */
    @Override
    public void transactionToOpenCard(Customer customer) {
        Transaction transaction = new Transaction(customer.getCustomerId(), discovery, discovery,
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.OPENCARD, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    /***
     * Создаёт транзакцию запроса баланса карты
     * @param customer объект Customer
     * @param card объект Card
     */
    @Override
    public void cardBalanceRequestTransaction(Customer customer, Card card) {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[card balance request]", "[card balance request]",
                card.getBalance(), Currency.RUB, TransactionType.CHECKBALANCE, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    /***
     * Создаёт транзакцию отправки денежных средств с карты на сберегательный счет
     * @param senderCustomer объект Customer, отправитель
     * @param senderCard объект Card, карты отправителя
     * @param recipientAccount объект SavingsAccount, сберегательный счет получателя
     * @param amount количество
     */
    @Override
    public void moneySendingToTheAccountTransaction(Customer senderCustomer, Card senderCard, SavingsAccount recipientAccount, BigDecimal amount) {
        Transaction transaction = new Transaction(senderCustomer.getCustomerId(), senderCard.getAccountNumber(), recipientAccount.getAccountNumber(),
                amount, senderCard.getCurrency(), TransactionType.OUTTRANSFER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    /***
     * Создаёт транзакцию поступления денежных средств с карты на сберегательный счет
     * @param recipientCustomer объект Customer, получатель
     * @param senderCard объект Card, карты отправителя
     * @param recipientAccount объект SavingsAccount, сберегательный счет получателя
     * @param amount количество
     */
    @Override
    public void moneyReceiptToTheAccountTransaction(Customer recipientCustomer, Card senderCard, SavingsAccount recipientAccount, BigDecimal amount) {
        Transaction transaction = new Transaction(recipientCustomer.getCustomerId(), senderCard.getAccountNumber(), recipientAccount.getAccountNumber(),
                amount, senderCard.getCurrency(), TransactionType.INTRANSFER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    /***
     * Создаёт транзакцию запроса баланса сберегательного счета
     * @param customer объект Customer
     * @param savingsAccount объект SavingsAccount
     */
    @Override
    public void savingsAccountBalanceRequestTransaction(Customer customer, SavingsAccount savingsAccount) {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[SA balance request]", "[SA balance request]",
                savingsAccount.getBalance(), Currency.RUB, TransactionType.CHECKBALANCE, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    /***
     * Создаёт транзакцию поступления денежных средств на сберегательный счет через кассу банка
     * @param customer объект Customer
     * @param savingsAccount объект SavingsAccount
     * @param amount количество
     */
    @Override
    public void transactionOfReceiptOfFundsToSavingsAccountThroughTheBankCashDesk(Customer customer, SavingsAccount savingsAccount, BigDecimal amount) {
        Transaction transaction = new Transaction(customer.getCustomerId(), bank, savingsAccount.getAccountNumber(),
                amount, savingsAccount.getCurrency(), TransactionType.INTRANSFER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    /***
     * Создаёт транзакцию для закрытия сберегательного счета
     * @param customerId идентификатор клиента
     */
    @Override
    public void transactionToCloseSavingsAccount(Integer customerId) {
        Transaction transaction = new Transaction(customerId, closure, closure,
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.CLOSEACCOUNT, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    /***
     * Создаёт транзакцию получения денежных средств через кассу банка
     * @param customer объект Customer
     * @param savingsAccount объект SavingsAccount
     */
    @Override
    public void transactionWithdrawalMoneyFromSavingsAccountThroughCashier(Customer customer, SavingsAccount savingsAccount) {
        Transaction transaction = new Transaction(customer.getCustomerId(), bank, "[cash withdrawal]",
                savingsAccount.getBalance(), savingsAccount.getCurrency(), TransactionType.INTRANSFER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    /***
     * Создаёт транзакцию для открытия сберегательного счета
     * @param customer объект Customer
     */
    @Override
    public void transactionToOpenSavingAccount(Customer customer) {
        Transaction transaction = new Transaction(customer.getCustomerId(), discovery, discovery,
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.OPENACCOUNT, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    /***
     * Создаёт транзакцию отправки денежных средств со сберегательного счета
     * на сберегательный счет
     * @param senderCustomer объект Customer, отправитель
     * @param senderSavingsAccount объект SavingsAccount, сберегательный счет отправителя
     * @param recipientSavingsAccount объект SavingsAccount, сберегательный счет получателя
     * @param amount количество
     */
    @Override
    public void moneyTransferTransactionFromSavingsAccountToSavingsAccount(Customer senderCustomer, SavingsAccount senderSavingsAccount,
                                                             SavingsAccount recipientSavingsAccount, BigDecimal amount) {
        Transaction transaction = new Transaction(senderCustomer.getCustomerId(), senderSavingsAccount.getAccountNumber(), recipientSavingsAccount.getAccountNumber(),
                amount, senderSavingsAccount.getCurrency(), TransactionType.OUTTRANSFER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    /***
     * Создаёт транзакцию поступления денежных средств со сберегательного счета
     * на сберегательный счет
     * @param recipientCustomer объект Customer, получатель
     * @param senderSavingsAccount объект SavingsAccount, сберегательный счет отправителя
     * @param recipientSavingsAccount объект SavingsAccount, сберегательный счет получателя
     * @param amount количество
     */
    @Override
    public void transactionOfReceiptOfFundsFromSavingsAccountToSavingsAccount(Customer recipientCustomer, SavingsAccount senderSavingsAccount,
                                                                              SavingsAccount recipientSavingsAccount, BigDecimal amount) {
        Transaction transaction = new Transaction(recipientCustomer.getCustomerId(), senderSavingsAccount.getAccountNumber(), recipientSavingsAccount.getAccountNumber(),
                amount, senderSavingsAccount.getCurrency(), TransactionType.INTRANSFER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    /***
     * Создаёт транзакцию зачисления процентов по сберегательному счету
     * @param customerId идентификатор клиента
     * @param savingsAccount объект SavingsAccount
     * @param transferAmount сумма зачисления
     */
    @Override
    public void transactionAccrualOfInterestOnTheSavingsAccount(Integer customerId, SavingsAccount savingsAccount, BigDecimal transferAmount) {
        Transaction transaction = new Transaction(customerId, bank, savingsAccount.getAccountNumber(),
                transferAmount, Currency.RUB, TransactionType.CAPITALIZATION, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    /***
     * Создаёт транзакцию запроса всего списка банковских операций по конкретному клиенту
     * @param customer объект Customer
     */
    private void transactionRequestingTheEntireListOfOperationsOfSpecificCustomer(Customer customer) {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[general request]", "[general request]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.CHECKTRANSACTIONLIST, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }
}