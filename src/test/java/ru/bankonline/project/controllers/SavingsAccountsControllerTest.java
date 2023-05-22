package ru.bankonline.project.controllers;

import org.json.JSONObject;
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
import ru.bankonline.project.entity.Card;
import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.entity.SavingsAccount;
import ru.bankonline.project.repositories.CustomersRepository;
import ru.bankonline.project.services.savingsaccountsservice.SavingsAccountsService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BankOnlineApplication.class)
@AutoConfigureMockMvc
class SavingsAccountsControllerTest {

    @Mock
    private SavingsAccountsService savingsAccountsService;
    @Mock
    private ModelMapper modelMapper;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private MockMvc mockMvc;
    private static Customer customer;
    private static List<SavingsAccount> savingsAccounts;

    @BeforeAll
    static void setUp() {
        customer = new Customer();
        customer.setCustomerId(1);
        customer.setPassportSeries(2036);
        customer.setPassportNumber(303550);
        savingsAccounts = new ArrayList<>();
        customer.setSavingsAccounts(savingsAccounts);

        SavingsAccount savingsAccount = new SavingsAccount(customer.getCustomerId(), "20569999544412300058",
                BigDecimal.valueOf(0), Currency.RUB, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
        savingsAccounts.add(savingsAccount);
    }

    @Test
    void shouldAddNewSavingAccountToTheCustomer() throws Exception {
        customersRepository.save(customer);

        mockMvc.perform(post("/savingsAccounts/series/{series}/number/{number}",
                        customer.getPassportSeries(), customer.getPassportNumber()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteTheSavingAccountFromTheCustomer() throws Exception {
        String result = "Со сберегательного счета произведено полное списание денежных средств. " +
                "Клиенту требуется получить деньги в кассе.";
        customersRepository.save(customer);

        mockMvc.perform(patch("/savingsAccounts/series/{series}/number/{number}/close/{accountNumber}",
                        customer.getPassportSeries(), customer.getPassportNumber(), customer.getSavingsAccounts().get(0).getAccountNumber()))
                .andExpect(status().isOk())
                .andExpect(content().string(result + " Сберегательный счет с номером "
                        + customer.getSavingsAccounts().get(0).getAccountNumber() + " успешно закрыт!"));
    }

    @Test
    void shouldAddMoneyToTheAccount() throws Exception {
        customersRepository.save(customer);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("series", customer.getPassportSeries());
        jsonObject.put("number", customer.getPassportNumber());
        jsonObject.put("accountNumber", customer.getSavingsAccounts().get(0).getAccountNumber());
        jsonObject.put("amount", BigDecimal.valueOf(20_000));

        mockMvc.perform(patch("/savingsAccounts/{series}/{number}/{accountNumber}/{amount}",
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
        Card card = new Card(customer.getCustomerId(), "5022236009955001", "332",
                "45885599999666622015", BigDecimal.valueOf(50_000), Currency.RUB);
        customer.setCards(new ArrayList<>(List.of(card)));
        customersRepository.save(customer);

        mockMvc.perform(patch("/savingsAccounts/{series}/{number}/sender/{cardNumber}/recipient/{accountNumber}/{amount}",
                        customer.getPassportSeries(), customer.getPassportNumber(), customer.getCards().get(0).getCardNumber(),
                        customer.getSavingsAccounts().get(0).getAccountNumber(), new BigDecimal(40_000)))
                .andExpect(status().isOk())
                .andExpect(content().string("Перевод с карты: " + customer.getCards().get(0).getCardNumber() + " на сберегательный счет: "
                        + customer.getSavingsAccounts().get(0).getAccountNumber() + " - выполнен!"));
    }

    @Test
    void shouldCheckBalanceSavingsAccount() throws Exception {
        customersRepository.save(customer);

        mockMvc.perform(get("/savingsAccounts/series/{series}/number/{number}/checkBalance/{accountNumber}",
                        customer.getPassportSeries(), customer.getPassportNumber(),
                        customer.getSavingsAccounts().get(0).getAccountNumber()))
                .andExpect(status().isOk())
                .andExpect(content().string("Баланс: 0,00 RUB"));
    }

    @Test
    void shouldTransferFromSavingsAccountToSavingsAccount() throws Exception {
        SavingsAccount newSavingsAccount = new SavingsAccount(customer.getCustomerId(), "77777785965520004587",
                BigDecimal.valueOf(20_000), Currency.RUB, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
        savingsAccounts.add(newSavingsAccount);
        customersRepository.save(customer);

        mockMvc.perform(patch("/savingsAccounts/{series}/{number}/{senderAccountNumber}/{recipientAccountNumber}/{amount}",
                        customer.getPassportSeries(), customer.getPassportNumber(),
                        customer.getSavingsAccounts().get(1).getAccountNumber(), customer.getSavingsAccounts().get(0).getAccountNumber(),
                        new BigDecimal(10_000)))
                        .andExpect(status().isOk())
                        .andExpect(content().string("Перевод со счета: " +
                                customer.getSavingsAccounts().get(1).getAccountNumber() + " на счет: " +
                                customer.getSavingsAccounts().get(0).getAccountNumber() + " - выполнен!"));
    }
}