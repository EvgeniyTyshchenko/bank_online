package ru.bankonline.project.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.bankonline.project.BankOnlineApplication;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.constants.Status;
import ru.bankonline.project.dto.CustomerDTO;
import ru.bankonline.project.entity.*;
import ru.bankonline.project.repositories.CustomersRepository;
import ru.bankonline.project.services.customersservice.CustomersService;
import ru.bankonline.project.utils.validators.CustomerValidator;
import ru.bankonline.project.utils.validators.FullCustomerValidator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BankOnlineApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class CustomersControllerTest {

    @Mock
    private CustomersService customersService;
    @Mock
    private FullCustomerValidator fullCustomerValidator;
    @Mock
    private CustomerValidator customerValidator;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private MockMvc mockMvc;
    private static Customer customer;
    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() {
        customer = new Customer(8596, 120562, "Тыщенко", "Евгений", "Владимирович", "05.05.1997",
                new Address("Россия", "Краснодар", "ул.Московская", "12/3", 204),
                new Contact("89887444565", "eugenityschenko@yandex.ru"));

        Card card = new Card(customer.getCustomerId(), "5020607845129600", "880",
                "89526520550555548896", BigDecimal.valueOf(0), Currency.RUB);
        customer.setCards(new ArrayList<>(List.of(card)));

        SavingsAccount savingsAccount = new SavingsAccount(customer.getCustomerId(), "44458599652005891102", BigDecimal.valueOf(0),
                Currency.RUB, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
        customer.setSavingsAccounts(new ArrayList<>(List.of(savingsAccount)));

        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldGetCustomerByPassportSeriesAndNumber() throws Exception {
        customersRepository.save(customer);

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
                .andExpect(jsonPath("$.contactDTO.email").value("eugenityschenko@yandex.ru"))
                .andExpect(jsonPath("$.cardDTO[0].accountNumber").value("89526520550555548896"))
                .andExpect(jsonPath("$.savingsAccountDTO[0].accountNumber").value("44458599652005891102"));
    }

    @Test
    void shouldAddNewCustomer() throws Exception {
        CustomerDTO customerDTO = CustomerDTO.convertToDTOCustomer(customer, modelMapper);

        mockMvc.perform(post("/customers/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateCustomer() throws Exception {
        customersRepository.save(customer);
        Customer newCustomer = new Customer(7745, 100055, "Тыщенко", "Евгений", "Владимирович", "12.07.1995",
                new Address("Россия", "Краснодар", "ул.Московская", "12/3", 204),
                new Contact("89887444565", "eugenityschenko@yandex.ru"));

        CustomerDTO newCustomerDTO = CustomerDTO.convertToDTOCustomer(newCustomer, modelMapper);

        mockMvc.perform(patch("/customers/series/{series}/number/{number}",
                        customer.getPassportSeries(), customer.getPassportNumber())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCustomerDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteCustomer() throws Exception {
        customersRepository.save(customer);

        mockMvc.perform(delete("/customers/series/{series}/number/{number}",
                        customer.getPassportSeries(), customer.getPassportNumber()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetCustomerByCardNumber() throws Exception {
        customersRepository.save(customer);

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
        customersRepository.save(customer);

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