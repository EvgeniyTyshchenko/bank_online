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
import ru.bankonline.project.entity.*;
import ru.bankonline.project.repositories.CardsRepository;
import ru.bankonline.project.repositories.CustomersRepository;
import ru.bankonline.project.repositories.SavingsAccountsRepository;
import ru.bankonline.project.repositories.TransactionsRepository;
import ru.bankonline.project.services.customersservice.CustomersServiceImpl;
import ru.bankonline.project.utils.exceptions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CustomersServiceImplTest {

    @Mock
    private CustomersRepository customersRepository;
    @Mock
    private TransactionsRepository transactionsRepository;
    @Mock
    private CardsRepository cardsRepository;
    @Mock
    private SavingsAccountsRepository savingsAccountsRepository;
    @Mock
    private MailSender mailSender;
    @InjectMocks
    private CustomersServiceImpl customersService;
    private static Customer customer;
    private static Customer customerToUpdate;
    private static List<Card> cards;
    private static List<SavingsAccount> savingsAccounts;
    private static final Integer newPassportSeries = 1234;
    private static final Integer newPassportNumber = 567890;

    @BeforeAll
    static void setUp() {
        customer = new Customer(4321, 987654, "Тыщенко", "Евгений", "Владимирович", "01.01.1968",
                new Address("Россия", "Краснодар", "Российская", "95/д", 259),
                new Contact("89881233223", "eugenityschenko@yandex.ru"));

        Card card = new Card(customer.getCustomerId(), "2586325685456920", "564", "45859623005200470087",
                BigDecimal.valueOf(5000), Currency.RUB);
        cards = new ArrayList<>(List.of(card));
        customer.setCards(cards);

        SavingsAccount savingsAccount = new SavingsAccount(customer.getCustomerId(), "36925814775395140094",
                BigDecimal.valueOf(7500), Currency.RUB, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
        savingsAccounts = new ArrayList<>(List.of(savingsAccount));
        customer.setSavingsAccounts(savingsAccounts);

        customerToUpdate = new Customer();
        customerToUpdate.setPassportSeries(1234);
        customerToUpdate.setPassportNumber(4567890);
        customerToUpdate.setLastName("Петров");
        customerToUpdate.setFirstName("Петр");
        customerToUpdate.setPatronymic("Петрович");
        customerToUpdate.setBirthday("12.12.1965");
    }

    @Test
    void shouldBeAnExceptionAddingCustomerWithAlreadySavedPassportData() {
        when(customersRepository.findByPassportSeriesAndPassportNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(Optional.ofNullable(customer));

        customerToUpdate.setPassportSeries(customer.getPassportSeries());
        customerToUpdate.setPassportNumber(customer.getPassportNumber());
        Assertions.assertThrows(NotCreatedException.class, () -> customersService.addNewCustomer(customerToUpdate));
    }

    @Test
    void shouldAddNewCustomer() {
        customersService.addNewCustomer(customer);

        verify(customersRepository, times(1))
                .findByPassportSeriesAndPassportNumber(customer.getPassportSeries(), customer.getPassportNumber());
        verify(customersRepository, times(1)).save(customer);
        verify(transactionsRepository, times(1)).save(any());

        String expectedMessage = "Здравствуйте, " + customer.getFirstName() + " " + customer.getPatronymic() + "! \n"
                + "Добро пожаловать в наш банк!";
        verify(mailSender, times(1)).sendEmail(customer.getContactDetails()
                .getEmail(), "Регистрация в банке", expectedMessage);
        log.info(customer.toString());
    }
    //---

    @Test
    void shouldReceiveTheCustomerAccordingToPassportData() {
        when(customersRepository.findByPassportSeriesAndPassportNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(Optional.ofNullable(customer));

        Customer foundCustomer = customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(), customer.getPassportNumber());
        Assertions.assertNotNull(foundCustomer);
        Assertions.assertEquals(customer, foundCustomer);
        log.info(foundCustomer.toString());
    }

    @Test
    void shouldGenerateAnExceptionIfTheCustomerIsNotInTheDatabase() {
        Assertions.assertThrows(CustomerMissingFromDBException.class, () -> {
            customersService.customerSearchByPassportSeriesAndNumber(newPassportSeries, newPassportNumber);
        });
    }
    //---

    @Test
    void shouldBeAnExceptionWhenDeletingBlockedCustomer() {
        customer.setStatus(Status.BLOCKED);

        when(customersRepository.findByPassportSeriesAndPassportNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(Optional.ofNullable(customer));

        CustomerBlockingException exception = Assertions.assertThrows(CustomerBlockingException.class,
                () -> customersService.deleteCustomer(customer.getPassportSeries(), customer.getPassportNumber()));
        Assertions.assertEquals("Клиент заблокирован или удален!", exception.getMessage());
    }

    @Test
    void shouldBeAnExceptionWhenDeletingClosedCustomer() {
        customer.setStatus(Status.CLOSED);

        when(customersRepository.findByPassportSeriesAndPassportNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(Optional.ofNullable(customer));

        CustomerBlockingException exception = Assertions.assertThrows(CustomerBlockingException.class,
                () -> customersService.deleteCustomer(customer.getPassportSeries(), customer.getPassportNumber()));
        Assertions.assertEquals("Клиент заблокирован или удален!", exception.getMessage());
    }

    @Test
    void shouldDeleteCustomer() {
        customer.getCards().forEach(card -> card.setBalance(BigDecimal.ZERO));
        customer.getSavingsAccounts().forEach(savingsAccount -> savingsAccount.setBalance((BigDecimal.ZERO)));

        when(customersRepository.findByPassportSeriesAndPassportNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(Optional.ofNullable(customer));
        when(cardsRepository.findByCustomerId(customer.getCustomerId())).thenReturn(cards);
        when(savingsAccountsRepository.findByCustomerId(customer.getCustomerId())).thenReturn(savingsAccounts);

        Customer foundCustomer = customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(), customer.getPassportNumber());
        Assertions.assertDoesNotThrow(() -> customersService.deleteCustomer(foundCustomer.getPassportSeries(), foundCustomer.getPassportNumber()));

        Assertions.assertEquals(Status.CLOSED, customer.getStatus());
        Assertions.assertEquals(Status.CLOSED, cards.get(0).getStatus());
        Assertions.assertEquals(Status.CLOSED, savingsAccounts.get(0).getStatus());
        log.info(customer.getStatus().toString());
    }

    @Test
    void shouldBeAnExceptionWhenDeletingIfThereIsMoneyOnCardOrSavingsAccount() {
        customer.setStatus(Status.ACTIVE);

        when(customersRepository.findByPassportSeriesAndPassportNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(Optional.ofNullable(customer));
        when(cardsRepository.findByCustomerId(customer.getCustomerId())).thenReturn(cards);
        when(savingsAccountsRepository.findByCustomerId(customer.getCustomerId())).thenReturn(savingsAccounts);

        Customer foundCustomer = customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(),
                customer.getPassportNumber());
        CustomerBalanceNotZeroException exception = Assertions.assertThrows(CustomerBalanceNotZeroException.class, () -> customersService
                .deleteCustomer(foundCustomer.getPassportSeries(), foundCustomer.getPassportNumber()));
        Assertions.assertEquals("Ошибка в удалении аккаунта! У клиента " + customer.getLastName() + " "
                + customer.getFirstName() + " " + customer.getPatronymic() + " на картах и/или счетах имеются денежные средства. " +
                "Для корректного выполнения операции, Вам необходимо снять/перевести ВСЕ денежные средства со своих счетов и/или карт.",
                exception.getMessage());
    }
    //---

    @Test
    void shouldUpdateCustomer() {
        when(customersRepository.findByPassportSeriesAndPassportNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(Optional.ofNullable(customer));
        when(customersRepository.findPassportDuplicates(customerToUpdate.getPassportSeries(), customerToUpdate.getPassportNumber()))
                .thenReturn(Optional.of(1));

        customersService.updateCustomer(customer.getPassportSeries(), customer.getPassportNumber(), customerToUpdate);

        Assertions.assertEquals(customerToUpdate.getPassportSeries(), customer.getPassportSeries());
        Assertions.assertEquals(customerToUpdate.getPassportNumber(), customer.getPassportNumber());
        Assertions.assertEquals(customerToUpdate.getLastName(), customer.getLastName());
        Assertions.assertEquals(customerToUpdate.getFirstName(), customer.getFirstName());
        Assertions.assertEquals(customerToUpdate.getPatronymic(), customer.getPatronymic());
        Assertions.assertEquals(customerToUpdate.getBirthday(), customer.getBirthday());
        log.info(customer.toString());
    }

    @Test
    void shouldBeAnExceptionIfTheCustomerDuplicatesThePassportData() {
        when(customersRepository.findByPassportSeriesAndPassportNumber(customerToUpdate.getPassportSeries(), customerToUpdate.getPassportNumber()))
                .thenReturn(Optional.ofNullable(customer));
        when(customersRepository.findPassportDuplicates(customerToUpdate.getPassportSeries(), customerToUpdate.getPassportNumber()))
                .thenReturn(Optional.of(2));

        Assertions.assertThrows(PassportDuplicateException.class, () -> customersService.updateCustomer(customerToUpdate.getPassportSeries(),
                customerToUpdate.getPassportNumber(), customerToUpdate));
    }
    //---

    @Test
    void shouldGetTheCustomerByCardNumber() {
        when(customersRepository.findByCardNumber(customer.getCards().get(0).getCardNumber()))
                .thenReturn(Optional.ofNullable(customer));

        Customer result = customersService.getCustomerByCardNumber(customer.getCards().get(0).getCardNumber());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(customer.getCards().get(0).getCardNumber(), result.getCards().get(0).getCardNumber());
        log.info(result.toString());
    }

    @Test
    void shouldGetExceptionIfTheCustomerIsNotFoundByCardNumber() {
        Assertions.assertThrows(CustomerMissingFromDBException.class, () -> {
            customersService.getCustomerByCardNumber(anyString());
        });
    }
    //---

    @Test
    void shouldReceiveTheCustomerByAccountNumber() {
        when(customersRepository.findBySavingAccountNumber(customer.getSavingsAccounts().get(0).getAccountNumber()))
                .thenReturn(Optional.ofNullable(customer));

        Customer result = customersService.getCustomerBySavingAccountNumber(customer.getSavingsAccounts().get(0)
                .getAccountNumber());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(customer, result);
        log.info(result.toString());
    }

    @Test
    void shouldGetExceptionIfTheCustomerIsNotFoundByAccountNumber() {
        Assertions.assertThrows(CustomerMissingFromDBException.class, () -> {
            customersService.getCustomerBySavingAccountNumber(anyString());
        });
    }
    //---

    @Test
    void shouldCheckThatTheCustomerIsNotBlockedOrClosed() {
        customer.setStatus(Status.ACTIVE);
        Assertions.assertDoesNotThrow(() -> customersService.checkIfTheCustomerIsBlockedOrDeleted(customer));
    }

    @Test
    void shouldBeAnExceptionWhenTheCustomerIsBlocked() {
        customer.setStatus(Status.BLOCKED);

        Assertions.assertThrows(CustomerBlockingException.class,
                () -> customersService.checkIfTheCustomerIsBlockedOrDeleted(customer));
    }

    @Test
    void shouldBeAnExceptionWhenTheCustomerIsClosed() {
        customer.setStatus(Status.CLOSED);

        Assertions.assertThrows(CustomerBlockingException.class,
                () -> customersService.checkIfTheCustomerIsBlockedOrDeleted(customer));
    }
}