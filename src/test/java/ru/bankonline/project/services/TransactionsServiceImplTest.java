package ru.bankonline.project.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.constants.Status;
import ru.bankonline.project.constants.TransactionType;
import ru.bankonline.project.entity.*;
import ru.bankonline.project.repositories.TransactionsRepository;
import ru.bankonline.project.services.customersservice.CustomersService;
import ru.bankonline.project.services.transactionsservice.TransactionsServiceImpl;
import ru.bankonline.project.utils.exceptions.NotFoundInBaseException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class TransactionsServiceImplTest {

    @Mock
    private TransactionsRepository transactionsRepository;
    @Mock
    private CustomersService customersService;
    @InjectMocks
    private TransactionsServiceImpl transactionsService;
    private static Customer customer;
    private static Card cardForTestingTransactions;
    private static SavingsAccount savingsAccountForTestingTransactions;
    private static List<Transaction> transactionList;

    @BeforeAll
    static void setUp() {
        customer = new Customer(4321, 987654, "Тыщенко", "Евгений", "Владимирович",
                "01.01.1996", new Address("Россия", "Краснодар", "Российская", "95/д", 259),
                new Contact("89881233223", "eugenityschenko@yandex.ru"));
        customer.setCustomerId(1);

        Card card = new Card(customer.getCustomerId(), "6205598556620014", "128", "96121111111111951256",
                BigDecimal.valueOf(53_000), Currency.RUB);
        List<Card> cards = new ArrayList<>(List.of(card));
        customer.setCards(cards);

        SavingsAccount savingsAccount = new SavingsAccount(customer.getCustomerId(), "20004545778888888811", BigDecimal.valueOf(0),
                Currency.RUB, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
        List<SavingsAccount> savingsAccounts = new ArrayList<>(List.of(savingsAccount));
        customer.setSavingsAccounts(savingsAccounts);

        cardForTestingTransactions = new Card(2, "6000258695441255", "321", "45895556555521000236",
                BigDecimal.valueOf(43_500), Currency.RUB);

        savingsAccountForTestingTransactions= new SavingsAccount(2, "55598523666000004888", BigDecimal.valueOf(0),
                Currency.RUB, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now());

        transactionList = new ArrayList<>();
        transactionList.add(new Transaction(customer.getCustomerId(), "[registration]", "[registration]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.REGISTERCUSTOMER, LocalDateTime.now()));
        transactionList.add(new Transaction(customer.getCustomerId(),"[removal]", "[removal]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.DELETECUSTOMER, LocalDateTime.now()));
    }

    @Test
    void shouldGetAllCustomerTransactions() {
        when(customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(), customer.getPassportNumber())).thenReturn(customer);
        when(transactionsRepository.findAllByCustomerId(customer.getCustomerId())).thenReturn(transactionList);

        List<Transaction> actualTransactions = transactionsService.getTransactionCustomer(customer.getPassportSeries(), customer.getPassportNumber());
        assertEquals(transactionList, actualTransactions);
        log.info("Получение всех транзакций клиента");
    }

    @Test
    void shouldBeAnExceptionDueToAnEmptyTransactionsList() {
        when(customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(), customer.getPassportNumber())).thenReturn(customer);
        when(transactionsRepository.findAllByCustomerId(customer.getCustomerId())).thenReturn(Collections.emptyList());
        Integer passportSeriesCustomer = customer.getPassportSeries();
        Integer passportNumberCustomer = customer.getPassportNumber();

        NotFoundInBaseException exception = Assertions.assertThrows(NotFoundInBaseException.class, () -> {
            transactionsService.getTransactionCustomer(passportSeriesCustomer, passportNumberCustomer);
        });
        assertEquals("Список транзакций пуст.", exception.getMessage());
    }

    @Test
    void shouldBeCreatedAndSavedTransactionToRegisterNewCustomer() {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[registration]", "[registration]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.REGISTERCUSTOMER, LocalDateTime.now());

        when(transactionsRepository.findAllByCustomerId(customer.getCustomerId())).thenReturn(List.of(transaction));

        transactionsService.transactionToRegisterNewCustomer(customer.getCustomerId());
        List<Transaction> transactionReceivedFromTheDatabase = transactionsRepository.findAllByCustomerId(customer.getCustomerId());

        assertNotNull(transactionReceivedFromTheDatabase.get(0));
        assertEquals(transaction, transactionReceivedFromTheDatabase.get(0));
    }

    @Test
    void shouldBeCreatedAndSavedTransactionToDeleteCustomer() {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[removal]", "[removal]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.DELETECUSTOMER, LocalDateTime.now());

        when(transactionsRepository.findAllByCustomerId(customer.getCustomerId())).thenReturn(List.of(transaction));

        transactionsService.transactionToDeleteCustomer(customer.getCustomerId());
        List<Transaction> transactionReceivedFromTheDatabase = transactionsRepository.findAllByCustomerId(customer.getCustomerId());

        assertNotNull(transactionReceivedFromTheDatabase.get(0));
        assertEquals(transaction, transactionReceivedFromTheDatabase.get(0));
    }

    @Test
    void shouldBeCreatedAndSavedTransactionMoneySendingToTheCard() {
        Transaction transaction = new Transaction(customer.getCustomerId(), customer.getCards().get(0).getAccountNumber(),
                cardForTestingTransactions.getAccountNumber(), BigDecimal.valueOf(1_300), Currency.RUB, TransactionType.OUTTRANSFER, LocalDateTime.now());

        when(transactionsRepository.findAllByCustomerId(customer.getCustomerId())).thenReturn(List.of(transaction));

        transactionsService.moneySendingToTheCardTransaction(customer, customer.getCards().get(0), cardForTestingTransactions, BigDecimal.valueOf(2_000));
        List<Transaction> transactionReceivedFromTheDatabase = transactionsRepository.findAllByCustomerId(customer.getCustomerId());

        assertNotNull(transactionReceivedFromTheDatabase.get(0));
        assertEquals(transaction, transactionReceivedFromTheDatabase.get(0));
    }

    @Test
    void shouldBeCreatedAndSavedTransactionMoneyReceiptToTheCard() {
        Transaction transaction = new Transaction(customer.getCustomerId(), customer.getCards().get(0).getAccountNumber(),
                cardForTestingTransactions.getAccountNumber(), BigDecimal.valueOf(4_500), Currency.RUB, TransactionType.INTRANSFER, LocalDateTime.now());

        when(transactionsRepository.findAllByCustomerId(customer.getCustomerId())).thenReturn(List.of(transaction));

        transactionsService.moneyReceiptToTheCardTransaction(customer, customer.getCards().get(0),
                cardForTestingTransactions, BigDecimal.valueOf(4_500));
        List<Transaction> transactionReceivedFromTheDatabase = transactionsRepository.findAllByCustomerId(customer.getCustomerId());

        assertNotNull(transactionReceivedFromTheDatabase.get(0));
        assertEquals(transaction, transactionReceivedFromTheDatabase.get(0));
    }

    @Test
    void shouldBeCreatedAndSavedTransactionToCloseCard() {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[closure]", "[closure]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.CLOSECARD, LocalDateTime.now());

        when(transactionsRepository.findAllByCustomerId(customer.getCustomerId())).thenReturn(List.of(transaction));

        transactionsService.transactionToCloseCard(customer.getCustomerId());
        List<Transaction> transactionReceivedFromTheDatabase = transactionsRepository.findAllByCustomerId(customer.getCustomerId());

        assertNotNull(transactionReceivedFromTheDatabase.get(0));
        assertEquals(transaction, transactionReceivedFromTheDatabase.get(0));
    }

    @Test
    void shouldBeCreatedAndSavedTransactionToUnlockCard() {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[unblocking]", "[unblocking]",
                customer.getCards().get(0).getBalance(), Currency.RUB, TransactionType.UNLOCKINGCARD, LocalDateTime.now());

        when(transactionsRepository.findAllByCustomerId(customer.getCustomerId())).thenReturn(List.of(transaction));

        transactionsService.transactionToUnlockCard(customer.getCustomerId(), customer.getCards().get(0));
        List<Transaction> transactionReceivedFromTheDatabase = transactionsRepository.findAllByCustomerId(customer.getCustomerId());

        assertNotNull(transactionReceivedFromTheDatabase.get(0));
        assertEquals(transaction, transactionReceivedFromTheDatabase.get(0));
    }

    @Test
    void shouldBeCreatedAndSavedTransactionToBlockCard() {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[blocking]", "[blocking]",
                customer.getCards().get(0).getBalance(), Currency.RUB, TransactionType.BLOCKINGCARD, LocalDateTime.now());

        when(transactionsRepository.findAllByCustomerId(customer.getCustomerId())).thenReturn(List.of(transaction));

        transactionsService.transactionToBlockCard(customer.getCustomerId(), customer.getCards().get(0));
        List<Transaction> transactionReceivedFromTheDatabase = transactionsRepository.findAllByCustomerId(customer.getCustomerId());

        assertNotNull(transactionReceivedFromTheDatabase.get(0));
        assertEquals(transaction, transactionReceivedFromTheDatabase.get(0));
    }

    @Test
    void shouldBeCreatedAndSavedTransactionToOpenCard() {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[discovery]", "[discovery]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.OPENCARD, LocalDateTime.now());

        when(transactionsRepository.findAllByCustomerId(customer.getCustomerId())).thenReturn(List.of(transaction));

        transactionsService.transactionToOpenCard(customer);
        List<Transaction> transactionReceivedFromTheDatabase = transactionsRepository.findAllByCustomerId(customer.getCustomerId());

        assertNotNull(transactionReceivedFromTheDatabase.get(0));
        assertEquals(transaction, transactionReceivedFromTheDatabase.get(0));
    }

    @Test
    void shouldBeCreatedAndSavedCardBalanceRequestTransaction() {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[card balance request]", "[card balance request]",
                customer.getCards().get(0).getBalance(), Currency.RUB, TransactionType.CHECKBALANCE, LocalDateTime.now());

        when(transactionsRepository.findAllByCustomerId(customer.getCustomerId())).thenReturn(List.of(transaction));

        transactionsService.cardBalanceRequestTransaction(customer, customer.getCards().get(0));
        List<Transaction> transactionReceivedFromTheDatabase = transactionsRepository.findAllByCustomerId(customer.getCustomerId());

        assertNotNull(transactionReceivedFromTheDatabase.get(0));
        assertEquals(transaction, transactionReceivedFromTheDatabase.get(0));
    }

    @Test
    void shouldBeCreatedAndSavedTransactionMoneySendingToTheAccount() {
        Transaction transaction = new Transaction(customer.getCustomerId(), customer.getCards().get(0).getAccountNumber(),
                customer.getSavingsAccounts().get(0).getAccountNumber(), BigDecimal.valueOf(20_000), Currency.RUB,
                TransactionType.OUTTRANSFER, LocalDateTime.now());

        when(transactionsRepository.findAllByCustomerId(customer.getCustomerId())).thenReturn(List.of(transaction));

        transactionsService.moneySendingToTheAccountTransaction(customer, customer.getCards().get(0),
                customer.getSavingsAccounts().get(0), BigDecimal.valueOf(20_000));
        List<Transaction> transactionReceivedFromTheDatabase = transactionsRepository.findAllByCustomerId(customer.getCustomerId());

        assertNotNull(transactionReceivedFromTheDatabase.get(0));
        assertEquals(transaction, transactionReceivedFromTheDatabase.get(0));
    }

    @Test
    void shouldBeCreatedAndSavedTransactionMoneyReceiptToTheAccount() {
        Transaction transaction = new Transaction(customer.getCustomerId(), customer.getCards().get(0).getAccountNumber(),
                customer.getSavingsAccounts().get(0).getAccountNumber(), BigDecimal.valueOf(20_000), Currency.RUB,
                TransactionType.INTRANSFER, LocalDateTime.now());

        when(transactionsRepository.findAllByCustomerId(customer.getCustomerId())).thenReturn(List.of(transaction));

        transactionsService.moneyReceiptToTheAccountTransaction(customer, customer.getCards().get(0),
                customer.getSavingsAccounts().get(0), BigDecimal.valueOf(20_000));
        List<Transaction> transactionReceivedFromTheDatabase = transactionsRepository.findAllByCustomerId(customer.getCustomerId());

        assertNotNull(transactionReceivedFromTheDatabase.get(0));
        assertEquals(transaction, transactionReceivedFromTheDatabase.get(0));
    }

    @Test
    void shouldBeCreatedAndSavedSavingsAccountBalanceRequestTransaction() {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[SA balance request]", "[SA balance request]",
                customer.getSavingsAccounts().get(0).getBalance(), Currency.RUB, TransactionType.CHECKBALANCE, LocalDateTime.now());

        when(transactionsRepository.findAllByCustomerId(customer.getCustomerId())).thenReturn(List.of(transaction));

        transactionsService.savingsAccountBalanceRequestTransaction(customer, customer.getSavingsAccounts().get(0));
        List<Transaction> transactionReceivedFromTheDatabase = transactionsRepository.findAllByCustomerId(customer.getCustomerId());

        assertNotNull(transactionReceivedFromTheDatabase.get(0));
        assertEquals(transaction, transactionReceivedFromTheDatabase.get(0));
    }

    @Test
    void shouldBeCreatedAndSavedTransactionReplenishmentBalanceThroughTheBankCashDesk() {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[BANK]",
                customer.getSavingsAccounts().get(0).getAccountNumber(), BigDecimal.valueOf(70_000), Currency.RUB,
                TransactionType.INTRANSFER, LocalDateTime.now());

        when(transactionsRepository.findAllByCustomerId(customer.getCustomerId())).thenReturn(List.of(transaction));

        transactionsService.transactionReplenishmentBalanceThroughTheBankCashDesk(customer, customer.getSavingsAccounts().get(0), BigDecimal.valueOf(70_000));
        List<Transaction> transactionReceivedFromTheDatabase = transactionsRepository.findAllByCustomerId(customer.getCustomerId());

        assertNotNull(transactionReceivedFromTheDatabase.get(0));
        assertEquals(transaction, transactionReceivedFromTheDatabase.get(0));
    }

    @Test
    void shouldBeCreatedAndSavedTransactionToCloseSavingsAccount() {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[closure]", "[closure]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.CLOSEACCOUNT, LocalDateTime.now());

        when(transactionsRepository.findAllByCustomerId(customer.getCustomerId())).thenReturn(List.of(transaction));

        transactionsService.transactionToCloseSavingsAccount(customer.getCustomerId());
        List<Transaction> transactionReceivedFromTheDatabase = transactionsRepository.findAllByCustomerId(customer.getCustomerId());

        assertNotNull(transactionReceivedFromTheDatabase.get(0));
        assertEquals(transaction, transactionReceivedFromTheDatabase.get(0));
    }

    @Test
    void shouldBeCreatedAndSavedTransactionWithdrawalMoneyFromSavingsAccountThroughCashier() {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[BANK]", "[cash withdrawal]",
                BigDecimal.valueOf(70_000), Currency.RUB, TransactionType.INTRANSFER, LocalDateTime.now());

        when(transactionsRepository.findAllByCustomerId(customer.getCustomerId())).thenReturn(List.of(transaction));

        transactionsService.transactionWithdrawalMoneyFromSavingsAccountThroughCashier(customer, customer.getSavingsAccounts().get(0));
        List<Transaction> transactionReceivedFromTheDatabase = transactionsRepository.findAllByCustomerId(customer.getCustomerId());

        assertNotNull(transactionReceivedFromTheDatabase.get(0));
        assertEquals(transaction, transactionReceivedFromTheDatabase.get(0));
    }

    @Test
    void shouldBeCreatedAndSavedTransactionToOpenSavingAccount() {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[discovery]", "[discovery]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.OPENACCOUNT, LocalDateTime.now());

        when(transactionsRepository.findAllByCustomerId(customer.getCustomerId())).thenReturn(List.of(transaction));

        transactionsService.transactionToOpenSavingAccount(customer);
        List<Transaction> transactionReceivedFromTheDatabase = transactionsRepository.findAllByCustomerId(customer.getCustomerId());

        assertNotNull(transactionReceivedFromTheDatabase.get(0));
        assertEquals(transaction, transactionReceivedFromTheDatabase.get(0));
    }

    @Test
    void shouldBeCreatedAndSavedTransactionSendingFromAccountToAccount() {
        Transaction transaction = new Transaction(customer.getCustomerId(), customer.getSavingsAccounts().get(0).getAccountNumber(),
                savingsAccountForTestingTransactions.getAccountNumber(), BigDecimal.valueOf(70_000), Currency.RUB,
                TransactionType.OUTTRANSFER, LocalDateTime.now());

        when(transactionsRepository.findAllByCustomerId(customer.getCustomerId())).thenReturn(List.of(transaction));

        transactionsService.transactionSendingFromAccountToAccount(customer, customer.getSavingsAccounts().get(0),
                savingsAccountForTestingTransactions, BigDecimal.valueOf(70_000));
        List<Transaction> transactionReceivedFromTheDatabase = transactionsRepository.findAllByCustomerId(customer.getCustomerId());

        assertNotNull(transactionReceivedFromTheDatabase.get(0));
        assertEquals(transaction, transactionReceivedFromTheDatabase.get(0));
    }

    @Test
    void shouldBeCreatedAndSavedTransactionReceivingFromAccountToAccount() {
        Transaction transaction = new Transaction(customer.getCustomerId(), customer.getSavingsAccounts().get(0).getAccountNumber(),
                savingsAccountForTestingTransactions.getAccountNumber(), BigDecimal.valueOf(70_000), Currency.RUB,
                TransactionType.INTRANSFER, LocalDateTime.now());

        when(transactionsRepository.findAllByCustomerId(customer.getCustomerId())).thenReturn(List.of(transaction));

        transactionsService.transactionReceivingFromAccountToAccount(customer, customer.getSavingsAccounts().get(0),
                savingsAccountForTestingTransactions, BigDecimal.valueOf(70_000));
        List<Transaction> transactionReceivedFromTheDatabase = transactionsRepository.findAllByCustomerId(customer.getCustomerId());

        assertNotNull(transactionReceivedFromTheDatabase.get(0));
        assertEquals(transaction, transactionReceivedFromTheDatabase.get(0));
    }

    @Test
    void shouldBeCreatedAndSavedTransactionAccrualOfInterestOnTheDeposit() {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[BANK]", customer.getSavingsAccounts().get(0).getAccountNumber(),
                BigDecimal.valueOf(254.25), Currency.RUB, TransactionType.CAPITALIZATION, LocalDateTime.now());

        when(transactionsRepository.findAllByCustomerId(customer.getCustomerId())).thenReturn(List.of(transaction));

        transactionsService.transactionAccrualOfInterestOnTheDeposit(customer.getCustomerId(), customer.getSavingsAccounts().get(0),
                BigDecimal.valueOf(254.25));
        List<Transaction> transactionReceivedFromTheDatabase = transactionsRepository.findAllByCustomerId(customer.getCustomerId());

        assertNotNull(transactionReceivedFromTheDatabase.get(0));
        assertEquals(transaction, transactionReceivedFromTheDatabase.get(0));
    }
}