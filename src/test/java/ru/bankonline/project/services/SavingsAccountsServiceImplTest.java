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
import ru.bankonline.project.repositories.SavingsAccountsRepository;
import ru.bankonline.project.services.customersservice.CustomersService;
import ru.bankonline.project.services.savingsaccountsservice.SavingsAccountsServiceImpl;
import ru.bankonline.project.services.transactionsservice.TransactionsService;
import ru.bankonline.project.utils.exceptions.ClosingSavingsAccountException;
import ru.bankonline.project.utils.exceptions.EnteringSavingsAccountDataException;
import ru.bankonline.project.utils.exceptions.InsufficientFundsException;
import ru.bankonline.project.utils.exceptions.ViolationTermsDepositException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class SavingsAccountsServiceImplTest {

    @Mock
    private SavingsAccountsRepository savingsAccountsRepository;
    @Mock
    private CustomersService customersService;
    @Mock
    private TransactionsService transactionsService;
    @InjectMocks
    private SavingsAccountsServiceImpl savingsAccountsService;
    private static Customer customer;
    private static Customer newCustomer;

    @BeforeAll
    static void setUp() {
        customer = new Customer(7575, 999563, "Нестеров", "Сергей", "Евгеньевич", "02.11.1961",
                new Address("Россия", "Ставрополь", "Ленина", "27/Д", 324),
                new Contact("89884501256", "nesterovsergey@mail.ru"));

        SavingsAccount savingsAccount = new SavingsAccount(customer.getCustomerId(), "20004545888956623511", BigDecimal.valueOf(45_000),
                Currency.RUB, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
        List<SavingsAccount> savingsAccounts = new ArrayList<>(List.of(savingsAccount));
        customer.setSavingsAccounts(savingsAccounts);

        newCustomer = new Customer();
        newCustomer.setPassportSeries(2154);
        newCustomer.setPassportNumber(751205);
        newCustomer.setLastName("Савидов");
        newCustomer.setFirstName("Михаил");
        newCustomer.setPatronymic("Анатольевич");
        newCustomer.setBirthday("12.09.1971");

        SavingsAccount newSavingsAccount = new SavingsAccount(newCustomer.getCustomerId(), "44402665488451200066",
                BigDecimal.valueOf(0), Currency.RUB, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
        List<SavingsAccount> newSavingsAccounts= new ArrayList<>(List.of(newSavingsAccount));
        newCustomer.setSavingsAccounts(newSavingsAccounts);
    }

    @Test
    void shouldOpenSavingsAccountToACustomer() {
        when(customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(customer);

        savingsAccountsService.openSavingAccountToTheCustomer(customer.getPassportSeries(), customer.getPassportNumber());
        log.info("Открытие сберегательного счета");
    }

    @Test
    void shouldCloseTheCustomerSavingsAccount() {
        when(customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(customer);

        savingsAccountsService.closeSavingsAccount(customer.getPassportSeries(), customer.getPassportNumber(),
                customer.getSavingsAccounts().get(0).getAccountNumber());
        log.info("Закрытие сберегательного счета");
    }

    @Test
    void shouldThrowAnExceptionIfTheCustomerDoesNotHaveTheEnteredAccountNumber() {
        when(customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(customer);

        Integer passportSeriesCustomer = customer.getPassportSeries();
        Integer passportNumberCustomer = customer.getPassportNumber();
        String accountNumberNewCustomer = newCustomer.getSavingsAccounts().get(0).getAccountNumber();

        Assertions.assertThrows(EnteringSavingsAccountDataException.class,
                () -> savingsAccountsService.closeSavingsAccount(passportSeriesCustomer, passportNumberCustomer,
                        accountNumberNewCustomer));
    }

    @Test
    void shouldThrowAnExceptionIfTheCustomerSavingsAccountIsClosedOrBlocked() {
        customer.getSavingsAccounts().get(0).setStatus(Status.CLOSED);
        when(customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(customer);

        Integer passportSeriesCustomer = customer.getPassportSeries();
        Integer passportNumberCustomer = customer.getPassportNumber();
        String accountNumberCustomer = customer.getSavingsAccounts().get(0).getAccountNumber();

        Assertions.assertThrows(ClosingSavingsAccountException.class,
                () -> savingsAccountsService.closeSavingsAccount(passportSeriesCustomer, passportNumberCustomer,
                        accountNumberCustomer));
    }

    @Test
    void shouldAddMoneyToTheAccountThroughTheCashier() {
        newCustomer.getSavingsAccounts().get(0).setBalance(BigDecimal.valueOf(0));
        when(customersService.customerSearchByPassportSeriesAndNumber(newCustomer.getPassportSeries(), newCustomer.getPassportNumber()))
                .thenReturn(newCustomer);

        savingsAccountsService.addMoneyToTheAccountThroughTheCashier(newCustomer.getPassportSeries(), newCustomer.getPassportNumber(),
                newCustomer.getSavingsAccounts().get(0).getAccountNumber(), BigDecimal.valueOf(100_000));
        log.info("Пополнение денежных средств на сберегательный счет через кассу");
    }

    @Test
    void shouldBeAnExceptionIfThereIsMoneyInTheSavingsAccountAndYouNeedToAddMore() {
        when(customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(customer);

        Integer passportSeriesCustomer = customer.getPassportSeries();
        Integer passportNumberCustomer = customer.getPassportNumber();
        String accountNumberCustomer = customer.getSavingsAccounts().get(0).getAccountNumber();
        BigDecimal transferAmount = BigDecimal.valueOf(30_000);

        Assertions.assertThrows(ViolationTermsDepositException.class,
                () -> savingsAccountsService.addMoneyToTheAccountThroughTheCashier(passportSeriesCustomer, passportNumberCustomer,
                        accountNumberCustomer, transferAmount));
    }

    @Test
    void shouldCheckTheBalance() {
        customer.getSavingsAccounts().get(0).setStatus(Status.ACTIVE);
        when(customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(customer);

        savingsAccountsService.checkBalance(customer.getPassportSeries(), customer.getPassportNumber(),
                customer.getSavingsAccounts().get(0).getAccountNumber());
        log.info("Проверка баланса сберегательного счета");
    }

    @Test
    void shouldBeTransferBetweenSavingsAccounts() {
        customer.getSavingsAccounts().get(0).setStatus(Status.ACTIVE);
        customer.getSavingsAccounts().get(0).setBalance(BigDecimal.valueOf(45_000));
        when(customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(customer);
        when(customersService.getCustomerBySavingAccountNumber(newCustomer.getSavingsAccounts().get(0).getAccountNumber()))
                .thenReturn(newCustomer);

        savingsAccountsService.transferFromSavingsAccountToSavingsAccount(customer.getPassportSeries(), customer.getPassportNumber(),
                customer.getSavingsAccounts().get(0).getAccountNumber(), newCustomer.getSavingsAccounts().get(0).getAccountNumber(),
                BigDecimal.valueOf(45_000));
        log.info("Перевод денежных средств между сберегательными счетами");
    }

    @Test
    void shouldBeExceptionIfThereAreNotEnoughFundsInTheSavingsAccountToTransfer() {
        customer.getSavingsAccounts().get(0).setStatus(Status.ACTIVE);
        newCustomer.getSavingsAccounts().get(0).setBalance(BigDecimal.valueOf(0));
        when(customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(customer);
        when(customersService.getCustomerBySavingAccountNumber(newCustomer.getSavingsAccounts().get(0).getAccountNumber()))
                .thenReturn(newCustomer);

        Integer passportSeriesCustomer = customer.getPassportSeries();
        Integer passportNumberCustomer = customer.getPassportNumber();
        String accountNumberCustomer = customer.getSavingsAccounts().get(0).getAccountNumber();
        String accountNumberNewCustomer = newCustomer.getSavingsAccounts().get(0).getAccountNumber();
        BigDecimal balanceCustomerMoreThanAcceptable = customer.getSavingsAccounts().get(0).getBalance().add(BigDecimal.valueOf(1_000));

        Assertions.assertThrows(InsufficientFundsException.class,
                () -> savingsAccountsService.transferFromSavingsAccountToSavingsAccount(passportSeriesCustomer, passportNumberCustomer,
                        accountNumberCustomer, accountNumberNewCustomer, balanceCustomerMoreThanAcceptable));
    }
}