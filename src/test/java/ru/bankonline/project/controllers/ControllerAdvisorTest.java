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

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BankOnlineApplication.class)
@AutoConfigureMockMvc
class ControllerAdvisorTest {

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
        customer = new Customer(8282, 121252, "Иванов", "Петр", "Владимирович", "15.12.1990",
                new Address("Россия", "Кисловодск", "ул.Первая", "11/Б", 3),
                new Contact("89880004575", "petrivanoov@yandex.ru"));
        List<Card> cards = new ArrayList<>();
        customer.setCards(cards);

        Card cardOne = new Card(customer.getCustomerId(), "2220545895236002", "110",
                "78541002669999854529", BigDecimal.valueOf(0), Currency.RUB);
        cards.add(cardOne);

        Card cardTwo = new Card(customer.getCustomerId(), "5548625489520056", "117",
                "4585962036598520025695", BigDecimal.valueOf(0), Currency.RUB);
        cards.add(cardTwo);

        SavingsAccount savingsAccount = new SavingsAccount(customer.getCustomerId(), "45851200569999925111", BigDecimal.valueOf(0),
                Currency.RUB, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
        customer.setSavingsAccounts(new ArrayList<>(List.of(savingsAccount)));

        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldHandleCustomerMissingFromDBException() throws Exception {
        String errorMessage = "Клиент отсутсвует в базе!";

        mockMvc.perform(get("/customers/series/1234/number/567890"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }

    @Test
    void shouldHandleNotCreatedException() throws Exception {
        customersRepository.save(customer);

        String errorMessage = "Клиент с такими серией и номером паспорта уже существует!";
        CustomerDTO customerDTO = CustomerDTO.convertToDTOCustomer(customer, modelMapper);

        mockMvc.perform(post("/customers/add")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }

    @Test
    void shouldHandleNotUpdatedException() throws Exception {
        customersRepository.save(customer);

        String errorMessage = "Серия паспорта не может быть null и должна соответствовать 4 символам!";
        Customer newCustomer = new Customer(333, 222458, "Тыщенко", "Евгений", "Владимирович", "21.01.1994",
                new Address("Россия", "Краснодар", "ул.Мира", "12/1", 12),
                new Contact("89885551245", "eugenity1994@yandex.ru"));

        CustomerDTO newCustomerDTO = CustomerDTO.convertToDTOCustomer(newCustomer, modelMapper);

        mockMvc.perform(patch("/customers/series/{series}/number/{number}",
                        customer.getPassportSeries(), customer.getPassportNumber())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCustomerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }

    @Test
    void shouldHandleEnteringCardDataException() throws Exception {
        customersRepository.save(customer);

        String errorMessage = "Номер карты, который вы вводите отсутствует у клиента "
                + customer.getLastName() + " " + customer.getFirstName() + " " + customer.getPatronymic()
                + " Проверьте реквизиты карты и попробуйте снова.";
        String nonExistentCardNumber = "6025854002369544";

        mockMvc.perform(get("/cards/series/{series}/number/{number}/checkBalance/{cardNumber}",
                        customer.getPassportSeries(), customer.getPassportNumber(), nonExistentCardNumber))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }

    @Test
    void shouldHandleClosingCardException() throws Exception {
        customer.getCards().get(0).setBalance(BigDecimal.valueOf(1_000));
        customer.getCards().get(0).setStatus(Status.ACTIVE);
        customer.getCards().get(0).setCustomerId(customer.getCustomerId());
        customersRepository.save(customer);

        String errorMessage = "Ошибка в закрытии карты! Пожалуйста, убедитесь, что баланс равен 0. " +
                "Вы можете сделать заявку на снятие денег в кассе, снять деньги в банкомате или же перевести оставшуюся сумму на другой счет.";

        mockMvc.perform(patch("/cards/series/{series}/number/{number}/close/{cardNumber}",
                                customer.getPassportSeries(), customer.getPassportNumber(),
                        customer.getCards().get(0).getCardNumber()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }

    @Test
    void shouldHandleCustomerBlockingException() throws Exception {
        customer.setStatus(Status.CLOSED);
        customersRepository.save(customer);

        String errorMessage = "Клиент заблокирован или удален! " +
                "Для точного уточнения статуса, сделайте общий запрос по клиенту.";

        mockMvc.perform(patch("/cards/series/{series}/number/{number}/block/{cardNumber}",
                        customer.getPassportSeries(), customer.getPassportNumber(),
                        customer.getCards().get(0).getCardNumber()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }

    @Test
    void shouldHandleInsufficientFundsException() throws Exception {
        customer.getCards().get(1).setCustomerId(customer.getCustomerId());
        customersRepository.save(customer);

        String errorMessage = "Недостаточно денежных средств для совершения транзакции!";

        mockMvc.perform(patch("/cards/{series}/{number}/{senderCardNumber}/{recipientCardNumber}/{amount}",
                        customer.getPassportSeries(), customer.getPassportNumber(), customer.getCards().get(0).getCardNumber(),
                        customer.getCards().get(1).getCardNumber(), BigDecimal.valueOf(5_000)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }

    @Test
    void shouldHandleEnteringSavingsAccountDataException() throws Exception {
        customersRepository.save(customer);

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
    void shouldHandleClosingSavingsAccountException() throws Exception {
        customer.getSavingsAccounts().get(0).setStatus(Status.CLOSED);
        customersRepository.save(customer);

        String errorMessage = "Сберегательный счет " + customer.getSavingsAccounts().get(0).getAccountNumber()
                + " закрыт или заблокирован! Убедитесь, что Вы ввели правильные реквизиты!";

        mockMvc.perform(patch("/savingsAccounts/series/{series}/number/{number}/close/{accountNumber}",
                        customer.getPassportSeries(), customer.getPassportNumber(),
                        customer.getSavingsAccounts().get(0).getAccountNumber()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }

    @Test
    void shouldHandleCustomerBalanceNotZeroException() throws Exception {
        customer.setStatus(Status.ACTIVE);
        customer.getCards().get(0).setBalance(BigDecimal.valueOf(15_000));
        customer.getCards().get(0).setCustomerId(customer.getCustomerId());
        customersRepository.save(customer);

        String errorMessage = "Ошибка в удалении аккаунта! У клиента " + customer.getLastName() + " "
                + customer.getFirstName() + " " + customer.getPatronymic() + " на картах и/или счетах имеются денежные средства. " +
                "Для корректного выполнения операции, Вам необходимо снять/перевести ВСЕ денежные средства со своих счетов и/или карт.";

        mockMvc.perform(delete("/customers/series/{series}/number/{number}",
                        customer.getPassportSeries(), customer.getPassportNumber()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }

    @Test
    void shouldHandleViolationTermsDepositException() throws Exception {
        customer.getSavingsAccounts().get(0).setStatus(Status.ACTIVE);
        customer.getSavingsAccounts().get(0).setBalance(BigDecimal.valueOf(40_000));
        customer.getSavingsAccounts().get(0).setCustomerId(customer.getCustomerId());
        customersRepository.save(customer);

        String errorMessage = "Нарушение условий сберегательного счета! " +
                "Данный сберегательный счет предполагает открытие и разовое пополнение.";

        mockMvc.perform(patch("/savingsAccounts/{series}/{number}/{accountNumber}/{amount}",
                        customer.getPassportSeries(), customer.getPassportNumber(),
                        customer.getSavingsAccounts().get(0).getAccountNumber(), BigDecimal.valueOf(25_000)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }

    @Test
    void shouldHandlePassportDuplicateException() throws Exception {
        customersRepository.save(customer);

        Customer customerTwo = new Customer(4585, 789995,
                "Петренко", "Сергей", "Борисович", "16.11.1970",
                new Address("Россия", "Москва", "ул.Большая", "117/12", 258),
                new Contact("89881254585", "sergeypetrenko1970@yandex.ru"));
        customersRepository.save(customerTwo);

        Customer customerToUpdate = new Customer(customerTwo.getPassportSeries(), customerTwo.getPassportNumber(),
                "Иванов", "Петр", "Евгеньевич", "17.02.1991",
                new Address("Россия", "Кисловодск", "ул.Первая", "11/Б", 3),
                new Contact("89880004575", "petrivanoov@yandex.ru"));

        CustomerDTO customerDTO = CustomerDTO.convertToDTOCustomer(customerToUpdate, modelMapper);

        String errorMessage = "Паспорт с указанными серией и номером уже есть в базе.";

        mockMvc.perform(patch("/customers/series/{series}/number/{number}",
                        customer.getPassportSeries(), customer.getPassportNumber())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").value(notNullValue()));
    }
}