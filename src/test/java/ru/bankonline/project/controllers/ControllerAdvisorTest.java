package ru.bankonline.project.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
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

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BankOnlineApplication.class)
@AutoConfigureMockMvc
class ControllerAdvisorTest {

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
        customer = new Customer(8282, 121252, "Иванов", "Петр", "Владимирович", "15.12.1990",
                new Address("Россия", "Кисловодск", "ул.Первая", "11/Б", 3),
                new Contact("89880004575", "petrivanoov@yandex.ru"));

        SavingsAccount savingsAccount = new SavingsAccount(customer.getCustomerId(), "45851200569999925111", BigDecimal.valueOf(0),
                Currency.RUB, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
        customer.setSavingsAccounts(new ArrayList<>(List.of(savingsAccount)));

        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldHandleCustomerMissingFromDBException() throws Exception {
        String errorMessage = "Клиент отсутствует в базе!";

        mockMvc.perform(get("/customers/series/1234/number/567890"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }

    @Test
    void shouldHandleNotCreatedException() throws Exception {
        customersService.saveCustomersRepository(customer);

        String errorMessage = "Клиент с такими серией и номером паспорта уже существует!";
        CustomerDTO customerDTO = CustomerDTO.convertToDTOCustomerWithAddressAndContacts(customer, modelMapper);

        mockMvc.perform(post("/customers/add")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }

    @Test
    void shouldHandleNotUpdatedException() throws Exception {
        customersService.saveCustomersRepository(customer);

        String errorMessage = "Серия паспорта не может быть null и должна соответствовать 4 символам!";
        Customer newCustomer = new Customer(333, 222458, "Тыщенко", "Евгений", "Владимирович", "21.01.1994",
                new Address("Россия", "Краснодар", "ул.Мира", "12/1", 12),
                new Contact("89885551245", "eugenity1994@yandex.ru"));

        CustomerDTO newCustomerDTO = CustomerDTO.convertToDTOCustomerWithAddressAndContacts(newCustomer, modelMapper);

        mockMvc.perform(patch("/customers/update/series/{series}/number/{number}",
                        customer.getPassportSeries(), customer.getPassportNumber())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCustomerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }

    @Test
    void shouldHandleEnteringSavingsAccountDataException() throws Exception {
        customersService.saveCustomersRepository(customer);

        String errorMessage = "Номер счета, который вы вводите отсутствует у клиента "
                + customer.getLastName() + " " + customer.getFirstName() + " " + customer.getPatronymic()
                + " Проверьте реквизиты сберегательного счета и попробуйте снова.";
        String nonExistentAccountNumber = "45859625636655210470";

        mockMvc.perform(patch("/savingsAccounts/series/{series}/number/{number}/close/{accountNumber}",
                        customer.getPassportSeries(), customer.getPassportNumber(), nonExistentAccountNumber))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }

    @Test
    void shouldHandlePassportDuplicateException() throws Exception {
        customersService.saveCustomersRepository(customer);

        Customer customerTwo = new Customer(4585, 789995,
                "Петренко", "Сергей", "Борисович", "16.11.1970",
                new Address("Россия", "Москва", "ул.Большая", "117/12", 258),
                new Contact("89881254585", "sergeypetrenko1970@yandex.ru"));
        customersService.saveCustomersRepository(customerTwo);

        Customer customerToUpdate = new Customer(customerTwo.getPassportSeries(), customerTwo.getPassportNumber(),
                "Иванов", "Петр", "Евгеньевич", "17.02.1991",
                new Address("Россия", "Кисловодск", "ул.Первая", "11/Б", 3),
                new Contact("89880004575", "petrivanoov@yandex.ru"));

        CustomerDTO customerDTO = CustomerDTO.convertToDTOCustomerWithAddressAndContacts(customerToUpdate, modelMapper);

        String errorMessage = "Паспорт с указанными серией и номером уже есть в базе.";

        mockMvc.perform(patch("/customers/update/series/{series}/number/{number}",
                        customer.getPassportSeries(), customer.getPassportNumber())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }
}