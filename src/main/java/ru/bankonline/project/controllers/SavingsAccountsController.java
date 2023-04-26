package ru.bankonline.project.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bankonline.project.services.savingsaccountsservice.SavingsAccountsService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/savingsAccounts")
public class SavingsAccountsController {

    private final SavingsAccountsService savingsAccountsService;

    public SavingsAccountsController(SavingsAccountsService savingsAccountsService) {
        this.savingsAccountsService = savingsAccountsService;
    }

    @PostMapping(path = "/series/{series}/number/{number}")
    public ResponseEntity<HttpStatus> addNewSavingAccountToTheCustomer(@PathVariable Integer series,
                                                                       @PathVariable Integer number) {
        savingsAccountsService.openSavingAccountToTheCustomer(series, number);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/series/{series}/number/{number}/close/{accountNumber}")
    public ResponseEntity<String> deleteTheSavingAccountFromTheCustomer(@PathVariable Integer series, @PathVariable Integer number,
                                                                        @PathVariable String accountNumber) {
        savingsAccountsService.closeSavingAccount(series, number, accountNumber);
        return ResponseEntity.ok("Сберегательный счет с номером " + accountNumber + " успешно закрыт!");
    }

    @PatchMapping("/{series}/{number}/{accountNumber}/{amount}")
    public ResponseEntity<String> addMoneyToTheAccount(@PathVariable Integer series, @PathVariable Integer number,
                                                       @PathVariable String accountNumber, @PathVariable BigDecimal amount) {
        String result = savingsAccountsService.addMoneyToTheAccountThroughTheCashier(series, number, accountNumber, amount);
        return ResponseEntity.ok("Успешно! Счет: " + accountNumber + " пополнен. Баланс: " + result);
    }

    @PatchMapping("/{series}/{number}/{senderCardNumber}/{recipientAccountNumber}/{amount}")
    public ResponseEntity<String> transferFromCardToSavingsAccount(@PathVariable Integer series, @PathVariable Integer number,
                                                                   @PathVariable String senderCardNumber,
                                                                   @PathVariable String recipientAccountNumber, @PathVariable BigDecimal amount) {
        savingsAccountsService.transferFromCardToSavingsAccount(series, number, senderCardNumber, recipientAccountNumber, amount);
        return ResponseEntity.ok("Перевод с карты: " + senderCardNumber + " на сберегательный счет: " + recipientAccountNumber + " - выполнен!" );
    }

    @GetMapping("/series/{series}/number/{number}/checkBalance/{accountNumber}")
    public ResponseEntity<String> checkBalanceSavingsAccount(@PathVariable Integer series, @PathVariable Integer number,
                                                             @PathVariable String accountNumber) {
        return ResponseEntity.ok(savingsAccountsService.checkBalance(series, number, accountNumber));
    }
}