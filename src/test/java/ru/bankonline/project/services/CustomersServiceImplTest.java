package ru.bankonline.project.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.constants.Status;
import ru.bankonline.project.entity.*;
import ru.bankonline.project.repositories.CustomersRepository;
import ru.bankonline.project.services.cardsservice.CardsService;
import ru.bankonline.project.services.customersservice.CustomersServiceImpl;
import ru.bankonline.project.services.savingsaccountsservice.SavingsAccountsService;
import ru.bankonline.project.services.transactionsservice.TransactionsService;
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
    private TransactionsService transactionsService;
    @Mock
    private CardsService cardsService;
    @Mock
    private SavingsAccountsService savingsAccountsService;
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
    private static final String nonExistentCardNumber = "6000444433332222";
    private static final String nonExistentAccountNumber = "44441221456500007887";

    @BeforeEach
    void setUp() {
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
        verify(transactionsService, times(1)).transactionToRegisterNewCustomer(any());

        String expectedMessage = "Здравствуйте, " + customer.getFirstName() + " " + customer.getPatronymic() + "! \n"
                + "Добро пожаловать в наш банк!";
        verify(mailSender, times(1)).sendEmail(customer.getContactDetails()
                .getEmail(), "Регистрация в банке", expectedMessage);
        log.info(customer.toString());
    }

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

    @Test
    void shouldBeAnExceptionWhenClosingBlockedCustomer() {
        customer.setStatus(Status.BLOCKED);

        when(customersRepository.findByPassportSeriesAndPassportNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(Optional.ofNullable(customer));

        Integer passportSeriesCustomer = customer.getPassportSeries();
        Integer passportNumberCustomer = customer.getPassportNumber();

        CustomerBlockingException exception = Assertions.assertThrows(CustomerBlockingException.class,
                () -> customersService.closingCustomer(passportSeriesCustomer, passportNumberCustomer));
        Assertions.assertEquals("Клиент заблокирован или закрыт!", exception.getMessage());
    }

    @Test
    void shouldBeAnExceptionWhenDeletingAnAlreadyClosedCustomer() {
        customer.setStatus(Status.CLOSED);

        when(customersRepository.findByPassportSeriesAndPassportNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(Optional.ofNullable(customer));

        Integer passportSeriesCustomer = customer.getPassportSeries();
        Integer passportNumberCustomer = customer.getPassportNumber();

        CustomerBlockingException exception = Assertions.assertThrows(CustomerBlockingException.class,
                () -> customersService.closingCustomer(passportSeriesCustomer, passportNumberCustomer));
        Assertions.assertEquals("Клиент заблокирован или закрыт!", exception.getMessage());
    }

    @Test
    void shouldCloseCustomerAccount() {
        customer.getCards().forEach(card -> card.setBalance(BigDecimal.ZERO));
        customer.getSavingsAccounts().forEach(savingsAccount -> savingsAccount.setBalance((BigDecimal.ZERO)));

        when(customersRepository.findByPassportSeriesAndPassportNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(Optional.ofNullable(customer));
        when(cardsService.findByCustomerIdToCardsRepository(customer.getCustomerId())).thenReturn(cards);
        when(savingsAccountsService.findByCustomerIdToSavingsAccountsRepository(customer.getCustomerId())).thenReturn(savingsAccounts);
        doNothing().when(cardsService).closeAllCardsInTheList(customer.getCards());

        Customer foundCustomer = customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(), customer.getPassportNumber());
        Assertions.assertDoesNotThrow(() -> customersService.closingCustomer(foundCustomer.getPassportSeries(), foundCustomer.getPassportNumber()));

        Assertions.assertEquals(Status.CLOSED, customer.getStatus());
        log.info(customer.getStatus().toString());
    }

    @Test
    void shouldBeAnExceptionWhenClosingTheCustomerHavingMoneyOnTheCardOrSavingsAccount() {
        when(customersRepository.findByPassportSeriesAndPassportNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(Optional.ofNullable(customer));
        when(cardsService.findByCustomerIdToCardsRepository(customer.getCustomerId())).thenReturn(cards);
        when(savingsAccountsService.findByCustomerIdToSavingsAccountsRepository(customer.getCustomerId())).thenReturn(savingsAccounts);

        Customer foundCustomer = customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(),
                customer.getPassportNumber());
        Integer passportSeriesFoundCustomer = foundCustomer.getPassportSeries();
        Integer passportNumberFoundCustomer = foundCustomer.getPassportNumber();

        CustomerBalanceNotZeroException exception = Assertions.assertThrows(CustomerBalanceNotZeroException.class, () -> customersService
                .closingCustomer(passportSeriesFoundCustomer, passportNumberFoundCustomer));
        Assertions.assertEquals("Ошибка в закрытии аккаунта! У клиента " + customer.getLastName() + " "
                + customer.getFirstName() + " " + customer.getPatronymic() + " на картах и/или счетах имеются денежные средства. " +
                "Для корректного выполнения операции, Вам необходимо снять/перевести ВСЕ денежные средства со своих счетов и/или карт.",
                exception.getMessage());
    }

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
    void shouldBeExceptionIfThePassportDataIsDuplicated() {
        when(customersRepository.findByPassportSeriesAndPassportNumber(customerToUpdate.getPassportSeries(), customerToUpdate.getPassportNumber()))
                .thenReturn(Optional.ofNullable(customer));
        when(customersRepository.findPassportDuplicates(customerToUpdate.getPassportSeries(), customerToUpdate.getPassportNumber()))
                .thenReturn(Optional.of(2));

        Integer passportSeriesCustomerToUpdate = customerToUpdate.getPassportSeries();
        Integer passportNumberCustomerToUpdate = customerToUpdate.getPassportNumber();

        Assertions.assertThrows(PassportDuplicateException.class, () -> customersService.updateCustomer(passportSeriesCustomerToUpdate,
                passportNumberCustomerToUpdate, customerToUpdate));
    }

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
            customersService.getCustomerByCardNumber(nonExistentCardNumber);
        });
    }

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
            customersService.getCustomerBySavingAccountNumber(nonExistentAccountNumber);
        });
    }

    @Test
    void shouldCheckThatTheCustomerIsNotBlockedOrClosed() {
        customer.setStatus(Status.ACTIVE);
        Assertions.assertDoesNotThrow(() -> customersService.checkIfTheCustomerIsBlockedOrClosed(customer));
    }

    @Test
    void shouldBeAnExceptionWhenTheCustomerIsBlocked() {
        customer.setStatus(Status.BLOCKED);

        Assertions.assertThrows(CustomerBlockingException.class,
                () -> customersService.checkIfTheCustomerIsBlockedOrClosed(customer));
    }

    @Test
    void shouldBeAnExceptionWhenTheCustomerIsClosed() {
        customer.setStatus(Status.CLOSED);

        Assertions.assertThrows(CustomerBlockingException.class,
                () -> customersService.checkIfTheCustomerIsBlockedOrClosed(customer));
    }
}