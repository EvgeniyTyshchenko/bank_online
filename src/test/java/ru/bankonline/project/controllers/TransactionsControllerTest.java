package ru.bankonline.project.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.constants.Status;
import ru.bankonline.project.constants.TransactionType;
import ru.bankonline.project.entity.*;
import ru.bankonline.project.repositories.CustomersRepository;
import ru.bankonline.project.repositories.TransactionsRepository;
import ru.bankonline.project.services.customersservice.CustomersService;
import ru.bankonline.project.services.transactionsservice.TransactionsService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TransactionsControllerTest {

    @Mock
    private TransactionsService transactionsService;
    @Mock
    private CustomersService customersService;
    @Mock
    private TransactionsRepository transactionsRepository;
    @Mock
    private CustomersRepository customersRepository;
    @Autowired
    private MockMvc mockMvc;
    private static Customer customer;

    @Test
    public void shouldGetTransactionCustomer() throws Exception {
        customer = new Customer(4321, 987654, "Тыщенко", "Евгений", "Владимирович", "01.01.1968",
                new Address("Россия", "Краснодар", "Российская", "95/д", 259),
                new Contact("89881233223", "eugenityschenko@yandex.ru"));
        customer.setCustomerId(1);

        Card card = new Card(customer.getCustomerId(), "4500450078960561", "104", "96122700003356951256",
                BigDecimal.valueOf(27_000), Currency.RUB);
        List<Card> cards = new ArrayList<>(List.of(card));
        customer.setCards(cards);
        SavingsAccount savingsAccount = new SavingsAccount(customer.getCustomerId(), "20004545888956623511", BigDecimal.valueOf(45_000),
                Currency.RUB, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
        List<SavingsAccount> savingsAccounts = new ArrayList<>(List.of(savingsAccount));
        customer.setSavingsAccounts(savingsAccounts);

        Transaction transaction = new Transaction(customer.getCustomerId(), "[registration]", "[registration]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.REGISTERCUSTOMER, LocalDateTime.now());
        List<Transaction> transactionsList = new ArrayList<>(List.of(transaction));

//        Customer customer1 = customersService.customerSearchByPassportSeriesAndNumber(4321, 987654);
//        assertEquals(customer1.getLastName(), "Тыщенко");

        customersRepository.save(customer);
        transactionsRepository.save(transaction);

        mockMvc.perform(get("/transactions/series/4321/number/987654"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$[0].customerId").value(transactionsList.get(0).getCustomerId()))
                .andExpect((ResultMatcher) jsonPath("$[0].sendersAccountNumber").value(transactionsList.get(0).getSendersAccountNumber()))
                .andExpect((ResultMatcher) jsonPath("$[0].recipientAccountNumber").value(transactionsList.get(0).getRecipientAccountNumber()))
                .andExpect((ResultMatcher) jsonPath("$[0].amount").value(transactionsList.get(0).getAmount()))
                .andExpect((ResultMatcher) jsonPath("$[0].currency").value(transactionsList.get(0).getCurrency()))
                .andExpect((ResultMatcher) jsonPath("$[0].transactionType").value(transactionsList.get(0).getTransactionType()));
    }
}