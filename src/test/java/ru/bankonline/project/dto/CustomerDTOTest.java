package ru.bankonline.project.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.constants.Status;
import ru.bankonline.project.entity.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerDTOTest {

    private static ModelMapper modelMapper;
    private static CustomerDTO customerDTO;
    private static Customer customer;

    @BeforeAll
    static void setUp() {
        modelMapper = new ModelMapper();
        List<Card> cards = new ArrayList<>(List.of(new Card(0,"4445800009652334", "988",
                "40000859636556335987", BigDecimal.valueOf(0), Currency.RUB)));
        List<SavingsAccount> savingsAccounts = new ArrayList<>(List.of(new SavingsAccount(0,"63580000789000589647",
                BigDecimal.valueOf(0), Currency.RUB, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now())));

        customerDTO = new CustomerDTO(4544, 448595, "Борисенко", "Яков", "Александрович", "17.09.1959",
                new AddressDTO("Россия", "Самара", "ул.Южная", "27/5", 12),
                new ContactDTO("89881237898", "yakovb@yandex.ru"));
        customer = new Customer(5685, 666253, "Яковлева", "Ирина", "Игоревна", "25.01.1974",
                new Address("Россия", "Челябинск", "ул.Родничанская", "27/В", 124),
                new Contact("89074521212", "yakovlevairinaa@yandex.ru"), cards, savingsAccounts);
    }

    @Test
    void shouldConvertToCustomer() {
        Customer customer = CustomerDTO.convertToCustomer(customerDTO, modelMapper);

        assertEquals(customerDTO.getPassportNumber(), customer.getPassportNumber());
        assertEquals(customerDTO.getLastName(), customer.getLastName());
        assertEquals(customerDTO.getAddressDTO().getStreet(), customer.getAddress().getStreet());
        assertEquals(customerDTO.getContactDTO().getPhoneNumber(), customer.getContactDetails().getPhoneNumber());
    }

    @Test
    void shouldConvertToDTOCustomerCardsAndAccounts() {
        CustomerDTO customerDTO = CustomerDTO.convertToDTOCustomerCardsAndAccounts(customer, modelMapper);

        assertEquals(customer.getPassportNumber(), customerDTO.getPassportNumber());
        assertEquals(customer.getLastName(), customerDTO.getLastName());
        assertEquals(customer.getAddress().getStreet(), customerDTO.getAddressDTO().getStreet());
        assertEquals(customer.getContactDetails().getPhoneNumber(), customerDTO.getContactDTO().getPhoneNumber());
        assertEquals(customer.getCards().get(0).getAccountNumber(), customerDTO.getCardDTO().get(0).getAccountNumber());
        assertEquals(customer.getSavingsAccounts().get(0).getAccountNumber(), customerDTO
                .getSavingsAccountDTO().get(0).getAccountNumber());
    }

    @Test
    void shouldConvertToDTOCustomer() {
        CustomerDTO customerDTO = CustomerDTO.convertToDTOCustomer(customer, modelMapper);

        assertEquals(customer.getPassportSeries(), customerDTO.getPassportSeries());
        assertEquals(customer.getFirstName(), customerDTO.getFirstName());
        assertEquals(customer.getAddress().getCity(), customerDTO.getAddressDTO().getCity());
        assertEquals(customer.getContactDetails().getEmail(), customerDTO.getContactDTO().getEmail());
    }
}