package ru.bankonline.project.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bankonline.project.services.savingsaccountsservice.SavingsAccountsService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/savingsAccounts")
@Tag(name = "Сберегательные счета", description = "CRUD-операции для работы с сберегательными счетами")
public class SavingsAccountsController {

    private final SavingsAccountsService savingsAccountsService;

    @Autowired
    public SavingsAccountsController(SavingsAccountsService savingsAccountsService) {
        this.savingsAccountsService = savingsAccountsService;
    }

    @Operation(summary = "Открытие сберегательного счета",
            description = "Необходимо вводить серию и номер паспорта клиента")
    @PostMapping(path = "/series/{series}/number/{number}")
    public ResponseEntity<HttpStatus> addNewSavingAccountToTheCustomer(@PathVariable Integer series,
                                                                       @PathVariable Integer number) {
        savingsAccountsService.openSavingAccountToTheCustomer(series, number);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Operation(summary = "Закрытие сберегательного счета",
            description = "Необходимо вводить серию и номер паспорта клиента, " +
                    "а также номер счета, который требуется закрыть")
    @PatchMapping("/series/{series}/number/{number}/close/{accountNumber}")
    public ResponseEntity<String> deleteTheSavingAccountFromTheCustomer(@PathVariable Integer series, @PathVariable Integer number,
                                                                        @PathVariable String accountNumber) {
        String result = savingsAccountsService.closeAccountAndWithdrawMoneyThroughCashier(series, number, accountNumber);
        return ResponseEntity.ok(result + " Сберегательный счет с номером " + accountNumber + " успешно закрыт!");
    }

    @Operation(summary = "Проверка баланса сберегательного счета",
            description = "Необходимо вводить серию и номер паспорта клиента, " +
                    "а также номер счета, где нужно узнать баланс")
    @GetMapping("/series/{series}/number/{number}/checkBalance/{accountNumber}")
    public ResponseEntity<String> checkBalanceSavingsAccount(@PathVariable Integer series, @PathVariable Integer number,
                                                             @PathVariable String accountNumber) {
        return ResponseEntity.ok(savingsAccountsService.checkBalance(series, number, accountNumber));
    }
}