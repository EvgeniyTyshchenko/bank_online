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
                new Contact("89074521212", "yakovlevairinaa@yandex.ru"));
        customer.setCards(cards);
        customer.setSavingsAccounts(savingsAccounts);
    }

    @Test
    void shouldConvertToCustomerWithAddressAndContacts() {
        Customer customer = CustomerDTO.convertToCustomerWithAddressAndContacts(customerDTO, modelMapper);

        assertEquals(customerDTO.getPassportSeries(), customer.getPassportSeries());
        assertEquals(customerDTO.getPassportNumber(), customer.getPassportNumber());
        assertEquals(customerDTO.getLastName(), customer.getLastName());
        assertEquals(customerDTO.getFirstName(), customer.getFirstName());
        assertEquals(customerDTO.getPatronymic(), customer.getPatronymic());
        assertEquals(customerDTO.getBirthday(), customer.getBirthday());
        assertEquals(customerDTO.getAddressDTO().getCountry(), customer.getAddress().getCountry());
        assertEquals(customerDTO.getAddressDTO().getCity(), customer.getAddress().getCity());
        assertEquals(customerDTO.getAddressDTO().getStreet(), customer.getAddress().getStreet());
        assertEquals(customerDTO.getAddressDTO().getHouse(), customer.getAddress().getHouse());
        assertEquals(customerDTO.getAddressDTO().getApartment(), customer.getAddress().getApartment());
        assertEquals(customerDTO.getContactDTO().getPhoneNumber(), customer.getContactDetails().getPhoneNumber());
        assertEquals(customerDTO.getContactDTO().getEmail(), customer.getContactDetails().getEmail());
    }

    @Test
    void shouldConvertToDTOTheEntireCustomerAndCardsAndAccounts() {
        CustomerDTO customerDTO = CustomerDTO.convertToDTOTheEntireCustomerAndCardsAndAccounts(customer, modelMapper);

        assertEquals(customer.getPassportSeries(), customerDTO.getPassportSeries());
        assertEquals(customer.getPassportNumber(), customerDTO.getPassportNumber());
        assertEquals(customer.getLastName(), customerDTO.getLastName());
        assertEquals(customer.getFirstName(), customerDTO.getFirstName());
        assertEquals(customer.getPatronymic(), customerDTO.getPatronymic());
        assertEquals(customer.getAddress().getCountry(), customerDTO.getAddressDTO().getCountry());
        assertEquals(customer.getAddress().getCity(), customerDTO.getAddressDTO().getCity());
        assertEquals(customer.getAddress().getStreet(), customerDTO.getAddressDTO().getStreet());
        assertEquals(customer.getAddress().getHouse(), customerDTO.getAddressDTO().getHouse());
        assertEquals(customer.getAddress().getApartment(), customerDTO.getAddressDTO().getApartment());
        assertEquals(customer.getContactDetails().getPhoneNumber(), customerDTO.getContactDTO().getPhoneNumber());
        assertEquals(customer.getContactDetails().getEmail(), customerDTO.getContactDTO().getEmail());

        assertEquals(customer.getCards().get(0).getCardNumber(), customerDTO.getCardDTO().get(0).getCardNumber());
        assertEquals(customer.getCards().get(0).getCvv(), customerDTO.getCardDTO().get(0).getCvv());
        assertEquals(customer.getCards().get(0).getAccountNumber(), customerDTO.getCardDTO().get(0).getAccountNumber());
        assertEquals(customer.getCards().get(0).getBalance(), customerDTO.getCardDTO().get(0).getBalance());
        assertEquals(customer.getCards().get(0).getCurrency(), customerDTO.getCardDTO().get(0).getCurrency());

        assertEquals(customer.getSavingsAccounts().get(0).getAccountNumber(), customerDTO
                .getSavingsAccountDTO().get(0).getAccountNumber());
        assertEquals(customer.getSavingsAccounts().get(0).getBalance(), customerDTO
                .getSavingsAccountDTO().get(0).getBalance());
        assertEquals(customer.getSavingsAccounts().get(0).getCurrency(), customerDTO
                .getSavingsAccountDTO().get(0).getCurrency());
    }

    @Test
    void shouldConvertToDTOCustomerWithAddressAndContacts() {
        CustomerDTO customerDTO = CustomerDTO.convertToDTOCustomerWithAddressAndContacts(customer, modelMapper);

        assertEquals(customer.getPassportSeries(), customerDTO.getPassportSeries());
        assertEquals(customer.getPassportNumber(), customerDTO.getPassportNumber());
        assertEquals(customer.getLastName(), customerDTO.getLastName());
        assertEquals(customer.getFirstName(), customerDTO.getFirstName());
        assertEquals(customer.getPatronymic(), customerDTO.getPatronymic());
        assertEquals(customer.getBirthday(), customerDTO.getBirthday());
        assertEquals(customer.getAddress().getCountry(), customerDTO.getAddressDTO().getCountry());
        assertEquals(customer.getAddress().getCity(), customerDTO.getAddressDTO().getCity());
        assertEquals(customer.getAddress().getStreet(), customerDTO.getAddressDTO().getStreet());
        assertEquals(customer.getAddress().getHouse(), customerDTO.getAddressDTO().getHouse());
        assertEquals(customer.getAddress().getApartment(), customerDTO.getAddressDTO().getApartment());
        assertEquals(customer.getContactDetails().getPhoneNumber(), customerDTO.getContactDTO().getPhoneNumber());
        assertEquals(customer.getContactDetails().getEmail(), customerDTO.getContactDTO().getEmail());
    }
}