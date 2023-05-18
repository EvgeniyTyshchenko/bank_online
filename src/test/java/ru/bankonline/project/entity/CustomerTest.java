package ru.bankonline.project.entity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.constants.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private static Customer customer;

    @BeforeAll
    static void setUp() {
        customer = new Customer(1285, 459658, "Романова", "Валентина", "Петровна", "21.11.1977",
                new Address("Россия", "Челябинск", "ул.Красная", "254/5", 453),
                new Contact("89054518596", "romanovaaa@yandex.ru"));
    }

    @Test
    void shouldCreateCustomerWithoutCardsAndSavingsAccounts() {
        assertNotNull(customer);
        assertEquals(1285, customer.getPassportSeries().intValue());
        assertEquals(459658, customer.getPassportNumber().intValue());
        assertEquals("Романова", customer.getLastName());
        assertEquals("Валентина", customer.getFirstName());
        assertEquals("Петровна", customer.getPatronymic());
        assertEquals("21.11.1977", customer.getBirthday());
        assertEquals("Челябинск", customer.getAddress().getCity());
        assertEquals("ул.Красная", customer.getAddress().getStreet());
        assertEquals("254/5", customer.getAddress().getHouse());
        assertEquals(453, customer.getAddress().getApartment());
        assertEquals("89054518596", customer.getContactDetails().getPhoneNumber());
        assertEquals("romanovaaa@yandex.ru", customer.getContactDetails().getEmail());
        customer.setCards(null);
        customer.setSavingsAccounts(null);
        assertNull(customer.getCards());
        assertNull(customer.getSavingsAccounts());
    }

    @Test
    void shouldCreateCustomerWithCardsAndSavingsAccounts() {
        List<Card> cards = new ArrayList<>(List.of(new Card(customer.getCustomerId(), "5552360008596445", "475",
                "50048887555694412305", BigDecimal.valueOf(0), Currency.RUB)));
        List<SavingsAccount> savingsAccounts = new ArrayList<>(List.of(new SavingsAccount(customer.getCustomerId(),"44458999902256884177",
                BigDecimal.valueOf(0), Currency.RUB, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now())));
        customer.setCards(cards);
        customer.setSavingsAccounts(savingsAccounts);

        assertNotNull(customer);
        assertEquals(cards, customer.getCards());
        assertEquals(savingsAccounts, customer.getSavingsAccounts());
    }
}