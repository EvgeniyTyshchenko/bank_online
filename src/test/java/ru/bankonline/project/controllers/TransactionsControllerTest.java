package ru.bankonline.project.controllers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.bankonline.project.BankOnlineApplication;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.constants.TransactionType;
import ru.bankonline.project.entity.*;
import ru.bankonline.project.repositories.CustomersRepository;
import ru.bankonline.project.repositories.TransactionsRepository;
import ru.bankonline.project.services.transactionsservice.TransactionsService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BankOnlineApplication.class)
@AutoConfigureMockMvc
class TransactionsControllerTest {

    @Mock
    private TransactionsService transactionsService;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private TransactionsRepository transactionsRepository;
    @Autowired
    private MockMvc mockMvc;
    private static Customer customer;
    private static Transaction transaction;

    @BeforeAll
    static void setUp() {
        customer = new Customer(1,7788, 459652, "Петренко", "Дмитрий", "Петрович", "12.02.1978",
                new Address("Россия", "Москва", "ул.Мирная", "72/3", 453),
                new Contact("89054128596", "petrenkodmitriiy@yandex.ru"));

        transaction = new Transaction(customer.getCustomerId(), "[registration]", "[registration]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.REGISTERCUSTOMER, LocalDateTime.now());
    }

    @Test
    public void shouldGetTransactionCustomer() throws Exception {
        customersRepository.save(customer);
        transactionsRepository.save(transaction);

        mockMvc.perform(get("/transactions/series/{series}/number/{number}",
                        customer.getPassportSeries(), customer.getPassportNumber()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(1))
                .andExpect(jsonPath("$[0].sendersAccountNumber").value("[registration]"))
                .andExpect(jsonPath("$[0].recipientAccountNumber").value("[registration]"))
                .andExpect(jsonPath("$[0].amount").value(0))
                .andExpect(jsonPath("$[0].currency").value("RUB"))
                .andExpect(jsonPath("$[0].transactionType").value("REGISTERCUSTOMER"));
    }
}