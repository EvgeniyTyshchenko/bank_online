package ru.bankonline.project.controllers;

import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.bankonline.project.BankOnlineApplication;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.constants.Status;
import ru.bankonline.project.entity.Card;
import ru.bankonline.project.entity.Contact;
import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.repositories.CustomersRepository;
import ru.bankonline.project.services.cardsservice.CardsService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BankOnlineApplication.class)
@AutoConfigureMockMvc
class CardsControllerTest {

    @Mock
    private CardsService cardsService;
    @Mock
    private ModelMapper modelMapper;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private MockMvc mockMvc;
    private static Customer customer;

    @BeforeAll
    static void setUp() {
        customer = new Customer();
        customer.setCustomerId(1);
        customer.setPassportSeries(8596);
        customer.setPassportNumber(120562);
        customer.setContactDetails(new Contact("89884556598", "mottvladiskav@mail.ru"));

        Card card = new Card(customer.getCustomerId(), "4004456500055698", "127",
                "90065845000550002458", BigDecimal.valueOf(0), Currency.RUB);
        customer.setCards(new ArrayList<>(List.of(card)));
    }

    @Test
    void shouldAddNewCardToTheCustomer() throws Exception {
        customersRepository.save(customer);

        mockMvc.perform(post("/cards/series/{series}/number/{number}",
                        customer.getPassportSeries(), customer.getPassportNumber()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteTheCardFromTheCustomer() throws Exception {
        customersRepository.save(customer);

        mockMvc.perform(patch("/cards/series/{series}/number/{number}/close/{cardNumber}",
                customer.getPassportSeries(), customer.getPassportNumber(), customer.getCards().get(0).getCardNumber()))
                .andExpect(status().isOk())
                .andExpect(content().string("Карта с номером "
                        + customer.getCards().get(0).getCardNumber() + " успешно закрыта!"));
    }

    @Test
    void shouldBlockTheCustomerCard() throws Exception {
        customersRepository.save(customer);

        mockMvc.perform(patch("/cards/series/{series}/number/{number}/block/{cardNumber}",
                        customer.getPassportSeries(), customer.getPassportNumber(), customer.getCards().get(0).getCardNumber()))
                .andExpect(status().isOk())
                .andExpect(content().string("Блокировка карты "
                        + customer.getCards().get(0).getCardNumber() + " успешно выполнена!"));
    }

    @Test
    void shouldUnlockTheCustomerCard() throws Exception {
        customer.getCards().get(0).setStatus(Status.BLOCKED);
        customersRepository.save(customer);

        mockMvc.perform(patch("/cards/series/{series}/number/{number}/unlock/{cardNumber}",
                        customer.getPassportSeries(), customer.getPassportNumber(), customer.getCards().get(0).getCardNumber()))
                .andExpect(status().isOk())
                .andExpect(content().string("Произведена разблокировка карты " + customer.getCards().get(0).getCardNumber()));
    }

    @Test
    void shouldCheckBalanceCard() throws Exception {
        customer.getCards().get(0).setStatus(Status.ACTIVE);
        customersRepository.save(customer);

        mockMvc.perform(get("/cards/series/{series}/number/{number}/checkBalance/{cardNumber}",
                        customer.getPassportSeries(), customer.getPassportNumber(), customer.getCards().get(0).getCardNumber()))
                .andExpect(status().isOk())
                .andExpect(content().string("Баланс: 0,00 RUB"));
    }

    @Test
    void shouldTransferBetweenCardsCustomers() throws Exception {
        Customer newCustomer = new Customer();
        newCustomer.setPassportSeries(9956);
        newCustomer.setPassportNumber(789655);
        Card card = new Card(1, "5500895566000044", "990",
                "78888555000047774481", BigDecimal.valueOf(15_000), Currency.RUB);
        newCustomer.setCards(new ArrayList<>(List.of(card)));

        customersRepository.save(customer);
        customersRepository.save(newCustomer);

        mockMvc.perform(MockMvcRequestBuilders.patch("/cards/{series}/{number}/{senderCardNumber}/{recipientCardNumber}/{amount}",
                                newCustomer.getPassportSeries(), newCustomer.getPassportNumber(), newCustomer.getCards().get(0).getCardNumber(),
                        customer.getCards().get(0).getCardNumber(), 5_000))
                .andExpect(status().isOk())
                .andExpect(content().string("Перевод с карты: " + newCustomer.getCards().get(0).getCardNumber() + " на карту: " +
                        customer.getCards().get(0).getCardNumber() + " - выполнен!"));
    }

    @Test
    void shouldGetCardDetails() throws Exception {
        customer.getCards().get(0).setBalance(BigDecimal.ZERO);
        customersRepository.save(customer);

        mockMvc.perform(get("/cards/details/{series}/{number}/{cardNumber}",
                        customer.getPassportSeries(), customer.getPassportNumber(), customer.getCards().get(0).getCardNumber()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardNumber").value("4004456500055698"))
                .andExpect(jsonPath("$.cvv").value("127"))
                .andExpect(jsonPath("$.accountNumber").value("90065845000550002458"))
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.currency").value("RUB"));
    }
}