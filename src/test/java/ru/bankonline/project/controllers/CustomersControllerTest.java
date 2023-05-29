package ru.bankonline.project.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.bankonline.project.BankOnlineApplication;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.constants.Status;
import ru.bankonline.project.dto.CustomerDTO;
import ru.bankonline.project.entity.*;
import ru.bankonline.project.services.customersservice.CustomersService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BankOnlineApplication.class)
@AutoConfigureMockMvc
class CustomersControllerTest {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CustomersService customersService;
    @Autowired
    private MockMvc mockMvc;
    private static Customer customer;
    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() {
        customer = new Customer(8596, 120562, "Тыщенко", "Евгений", "Владимирович", "05.05.1997",
                new Address("Россия", "Краснодар", "ул.Московская", "12/3", 204),
                new Contact("89887444565", "eugenityschenko@yandex.ru"));
        customer.setCustomerId(1);

        Card card = new Card(customer.getCustomerId(), "5020607845129600", "880",
                "89526520550555548896", BigDecimal.valueOf(0), Currency.RUB);
        customer.setCards(new ArrayList<>(List.of(card)));

        SavingsAccount savingsAccount = new SavingsAccount(customer.getCustomerId(), "44458599652005891102", BigDecimal.valueOf(0),
                Currency.RUB, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
        customer.setSavingsAccounts(new ArrayList<>(List.of(savingsAccount)));

        objectMapper = new ObjectMapper();
    }

    @Test
    @Transactional
    void shouldGetCustomerByPassportSeriesAndNumber() throws Exception {
        customersService.saveCustomersRepository(customer);

        mockMvc.perform(get("/customers/series/{series}/number/{number}",
                        customer.getPassportSeries(), customer.getPassportNumber()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passportSeries").value(8596))
                .andExpect(jsonPath("$.passportNumber").value(120562))
                .andExpect(jsonPath("$.lastName").value("Тыщенко"))
                .andExpect(jsonPath("$.firstName").value("Евгений"))
                .andExpect(jsonPath("$.patronymic").value("Владимирович"))
                .andExpect(jsonPath("$.birthday").value("05.05.1997"))
                .andExpect(jsonPath("$.addressDTO.city").value("Краснодар"))
                .andExpect(jsonPath("$.addressDTO.street").value("ул.Московская"))
                .andExpect(jsonPath("$.addressDTO.house").value("12/3"))
                .andExpect(jsonPath("$.addressDTO.apartment").value(204))
                .andExpect(jsonPath("$.contactDTO.phoneNumber").value("89887444565"))
                .andExpect(jsonPath("$.contactDTO.email").value("eugenityschenko@yandex.ru"));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void shouldAddNewCustomer() throws Exception {
        CustomerDTO customerDTO = CustomerDTO.convertToDTOCustomerWithAddressAndContacts(customer, modelMapper);

        mockMvc.perform(post("/customers/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void shouldUpdateCustomer() throws Exception {
        customersService.saveCustomersRepository(customer);
        Customer newCustomer = new Customer(7745, 100055, "Тыщенко", "Евгений", "Владимирович", "12.07.1995",
                new Address("Россия", "Краснодар", "ул.Московская", "12/3", 204),
                new Contact("89887444565", "eugenityschenko@yandex.ru"));

        CustomerDTO newCustomerDTO = CustomerDTO.convertToDTOCustomerWithAddressAndContacts(newCustomer, modelMapper);

        mockMvc.perform(patch("/customers/update/series/{series}/number/{number}",
                        customer.getPassportSeries(), customer.getPassportNumber())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCustomerDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldClosingCustomer() throws Exception {
        customersService.saveCustomersRepository(customer);

        mockMvc.perform(patch("/customers/close/series/{series}/number/{number}",
                        customer.getPassportSeries(), customer.getPassportNumber()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetCustomerByCardNumber() throws Exception {
        customer.getCards().get(0).setCustomerId(customer.getCustomerId());
        customersService.saveCustomersRepository(customer);

        mockMvc.perform(get("/customers/cardNumber/{cardNumber}",
                        customer.getCards().get(0).getCardNumber()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passportSeries").value(8596))
                .andExpect(jsonPath("$.passportNumber").value(120562))
                .andExpect(jsonPath("$.lastName").value("Тыщенко"))
                .andExpect(jsonPath("$.firstName").value("Евгений"))
                .andExpect(jsonPath("$.patronymic").value("Владимирович"))
                .andExpect(jsonPath("$.birthday").value("05.05.1997"))
                .andExpect(jsonPath("$.addressDTO.city").value("Краснодар"))
                .andExpect(jsonPath("$.addressDTO.street").value("ул.Московская"))
                .andExpect(jsonPath("$.addressDTO.house").value("12/3"))
                .andExpect(jsonPath("$.addressDTO.apartment").value(204))
                .andExpect(jsonPath("$.contactDTO.phoneNumber").value("89887444565"))
                .andExpect(jsonPath("$.contactDTO.email").value("eugenityschenko@yandex.ru"));
    }

    @Test
    void shouldGetCustomerBySavingAccountNumber() throws Exception {
        customer.getSavingsAccounts().get(0).setCustomerId(customer.getCustomerId());
        customersService.saveCustomersRepository(customer);

        mockMvc.perform(get("/customers/accountNumber/{accountNumber}",
                        customer.getSavingsAccounts().get(0).getAccountNumber()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passportSeries").value(8596))
                .andExpect(jsonPath("$.passportNumber").value(120562))
                .andExpect(jsonPath("$.lastName").value("Тыщенко"))
                .andExpect(jsonPath("$.firstName").value("Евгений"))
                .andExpect(jsonPath("$.patronymic").value("Владимирович"))
                .andExpect(jsonPath("$.birthday").value("05.05.1997"))
                .andExpect(jsonPath("$.addressDTO.city").value("Краснодар"))
                .andExpect(jsonPath("$.addressDTO.street").value("ул.Московская"))
                .andExpect(jsonPath("$.addressDTO.house").value("12/3"))
                .andExpect(jsonPath("$.addressDTO.apartment").value(204))
                .andExpect(jsonPath("$.contactDTO.phoneNumber").value("89887444565"))
                .andExpect(jsonPath("$.contactDTO.email").value("eugenityschenko@yandex.ru"));
    }
}