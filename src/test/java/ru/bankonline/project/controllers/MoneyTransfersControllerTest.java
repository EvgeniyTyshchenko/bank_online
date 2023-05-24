package ru.bankonline.project.controllers;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.bankonline.project.BankOnlineApplication;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.constants.Status;
import ru.bankonline.project.entity.Card;
import ru.bankonline.project.entity.Contact;
import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.entity.SavingsAccount;
import ru.bankonline.project.services.cardsservice.CardsService;
import ru.bankonline.project.services.customersservice.CustomersService;
import ru.bankonline.project.services.savingsaccountsservice.SavingsAccountsService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BankOnlineApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MoneyTransfersControllerTest {

    @Mock
    private CardsService cardsService;
    @Mock
    private SavingsAccountsService savingsAccountsService;
    @Autowired
    private CustomersService customersService;
    @Autowired
    private MockMvc mockMvc;
    private static Customer customer;

    @BeforeAll
    static void setUp() {
        customer = new Customer();
        customer.setCustomerId(1);
        customer.setPassportSeries(4566);
        customer.setPassportNumber(201556);
        customer.setContactDetails(new Contact("89880004555", "test@yandex.ru"));

        Card card = new Card(customer.getCustomerId(), "4004456555555599", "547",
                "47852015555544100014", BigDecimal.valueOf(75_500), Currency.RUB);
        customer.setCards(new ArrayList<>(List.of(card)));

        SavingsAccount savingsAccount = new SavingsAccount(customer.getCustomerId(), "60004569544412300058",
                BigDecimal.valueOf(0), Currency.RUB, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now());

        SavingsAccount newSavingsAccount = new SavingsAccount(customer.getCustomerId(), "77777785965520004587",
                BigDecimal.valueOf(0), Currency.RUB, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
        List<SavingsAccount> savingsAccounts = new ArrayList<>();
        savingsAccounts.add(savingsAccount);
        savingsAccounts.add(newSavingsAccount);
        customer.setSavingsAccounts(savingsAccounts);
    }

    @Test
    void shouldTransferBetweenCardsCustomers() throws Exception {
        Customer newCustomer = new Customer();
        newCustomer.setPassportSeries(9956);
        newCustomer.setPassportNumber(789655);
        newCustomer.setCustomerId(2);
        Card card = new Card(2, "6000895566000044", "854",
                "77778555000047774481", BigDecimal.valueOf(43_000), Currency.RUB);
        newCustomer.setCards(new ArrayList<>(List.of(card)));

        customersService.saveCustomersRepository(customer);
        customersService.saveCustomersRepository(newCustomer);

        mockMvc.perform(MockMvcRequestBuilders.patch("/moneyTransfers/cards/{series}/{number}/{senderCardNumber}/{recipientCardNumber}/{amount}",
                        newCustomer.getPassportSeries(), newCustomer.getPassportNumber(), newCustomer.getCards().get(0).getCardNumber(),
                        customer.getCards().get(0).getCardNumber(), 5_000))
                .andExpect(status().isOk())
                .andExpect(content().string("Перевод с карты: " + newCustomer.getCards().get(0).getCardNumber() + " на карту: " +
                        customer.getCards().get(0).getCardNumber() + " - выполнен!"));
    }

    @Test
    void shouldAddMoneyToTheAccount() throws Exception {
        customer.getSavingsAccounts().get(0).setBalance(BigDecimal.valueOf(0));
        customersService.saveCustomersRepository(customer);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("series", customer.getPassportSeries());
        jsonObject.put("number", customer.getPassportNumber());
        jsonObject.put("accountNumber", customer.getSavingsAccounts().get(0).getAccountNumber());
        jsonObject.put("amount", BigDecimal.valueOf(20_000));

        mockMvc.perform(patch("/moneyTransfers/{series}/{number}/{accountNumber}/{amount}",
                        customer.getPassportSeries(), customer.getPassportNumber(),
                        customer.getSavingsAccounts().get(0).getAccountNumber(), new BigDecimal(20_000))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("Успешно! Счет: "
                        + customer.getSavingsAccounts().get(0).getAccountNumber() + " пополнен. " +
                        "Баланс: 20000,00 RUB"));
    }

    @Test
    void shouldTransferFromCardToSavingsAccount() throws Exception {
        customersService.saveCustomersRepository(customer);

        mockMvc.perform(patch("/moneyTransfers/{series}/{number}/sender/{cardNumber}/recipient/{accountNumber}/{amount}",
                        customer.getPassportSeries(), customer.getPassportNumber(), customer.getCards().get(0).getCardNumber(),
                        customer.getSavingsAccounts().get(0).getAccountNumber(), new BigDecimal(40_000)))
                .andExpect(status().isOk())
                .andExpect(content().string("Перевод с карты: " + customer.getCards().get(0).getCardNumber() + " на сберегательный счет: "
                        + customer.getSavingsAccounts().get(0).getAccountNumber() + " - выполнен!"));
    }

    @Test
    void shouldTransferFromSavingsAccountToSavingsAccount() throws Exception {
        customer.getSavingsAccounts().get(0).setBalance(BigDecimal.valueOf(20_000));
        customersService.saveCustomersRepository(customer);

        mockMvc.perform(patch("/moneyTransfers/savingsAccounts/{series}/{number}/{senderAccountNumber}/{recipientAccountNumber}/{amount}",
                        customer.getPassportSeries(), customer.getPassportNumber(), customer.getSavingsAccounts().get(0).getAccountNumber(),
                        customer.getSavingsAccounts().get(1).getAccountNumber(), new BigDecimal(15_000)))
                .andExpect(status().isOk())
                .andExpect(content().string("Перевод со счета: " +
                        customer.getSavingsAccounts().get(0).getAccountNumber() + " на счет: " +
                        customer.getSavingsAccounts().get(1).getAccountNumber() + " - выполнен!"));
    }
}
