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
import ru.bankonline.project.constants.TransactionType;
import ru.bankonline.project.entity.Address;
import ru.bankonline.project.entity.Contact;
import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.entity.Transaction;
import ru.bankonline.project.repositories.TransactionsRepository;
import ru.bankonline.project.services.customersservice.CustomersService;
import ru.bankonline.project.services.transactionsservice.TransactionsServiceImpl;
import ru.bankonline.project.utils.exceptions.NotFoundInBaseException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private static List<Transaction> transactionList;

    @BeforeAll
    static void setUp() {
        customer = new Customer(4321, 987654, "Тыщенко", "Евгений", "Владимирович",
                "01.01.1996", new Address("Россия", "Краснодар", "Российская", "95/д", 259),
                new Contact("89881233223", "eugenityschenko@yandex.ru"));

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
        Assertions.assertEquals(transactionList, actualTransactions);
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
        Assertions.assertEquals("Список транзакций пуст.", exception.getMessage());
    }
}