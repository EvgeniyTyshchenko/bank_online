package ru.bankonline.project.controllers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.bankonline.project.BankOnlineApplication;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.constants.Status;
import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.entity.SavingsAccount;
import ru.bankonline.project.services.customersservice.CustomersService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BankOnlineApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SavingsAccountsControllerTest {

    @Autowired
    private CustomersService customersService;
    @Autowired
    private MockMvc mockMvc;
    private static Customer customer;

    @BeforeAll
    static void setUp() {
        customer = new Customer();
        customer.setCustomerId(1);
        customer.setPassportSeries(2036);
        customer.setPassportNumber(303550);
        List<SavingsAccount> savingsAccounts = new ArrayList<>();
        customer.setSavingsAccounts(savingsAccounts);

        SavingsAccount savingsAccount = new SavingsAccount(customer.getCustomerId(), "20569999544412300058",
                BigDecimal.valueOf(0), Currency.RUB, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
        savingsAccounts.add(savingsAccount);
    }

    @Test
    void shouldAddNewSavingAccountToTheCustomer() throws Exception {
        customersService.saveCustomersRepository(customer);

        mockMvc.perform(post("/savingsAccounts/series/{series}/number/{number}",
                        customer.getPassportSeries(), customer.getPassportNumber()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCloseTheCustomerSavingAccountAndWithdrawMoneyThroughCashier() throws Exception {
        String result = "Со сберегательного счета произведено полное списание денежных средств. " +
                "Клиенту требуется получить деньги в кассе.";
        customersService.saveCustomersRepository(customer);

        mockMvc.perform(patch("/savingsAccounts/series/{series}/number/{number}/close/{accountNumber}",
                        customer.getPassportSeries(), customer.getPassportNumber(), customer.getSavingsAccounts().get(0).getAccountNumber()))
                .andExpect(status().isOk())
                .andExpect(content().string(result + " Сберегательный счет с номером "
                        + customer.getSavingsAccounts().get(0).getAccountNumber() + " успешно закрыт!"));
    }

    @Test
    void shouldCheckBalanceSavingsAccount() throws Exception {
        customersService.saveCustomersRepository(customer);

        mockMvc.perform(get("/savingsAccounts/series/{series}/number/{number}/checkBalance/{accountNumber}",
                        customer.getPassportSeries(), customer.getPassportNumber(),
                        customer.getSavingsAccounts().get(0).getAccountNumber()))
                .andExpect(status().isOk())
                .andExpect(content().string("Баланс: 0,00 RUB"));
    }
}