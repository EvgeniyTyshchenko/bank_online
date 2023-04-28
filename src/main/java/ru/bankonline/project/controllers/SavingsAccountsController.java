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
        String result = savingsAccountsService.closeAccountAndWithdrawMoneyThroughCashier(series, number, accountNumber);
        return ResponseEntity.ok(result + " Сберегательный счет с номером " + accountNumber + " успешно закрыт!");
    }

    @PatchMapping("/{series}/{number}/{accountNumber}/{amount}")
    public ResponseEntity<String> addMoneyToTheAccount(@PathVariable Integer series, @PathVariable Integer number,
                                                       @PathVariable String accountNumber, @PathVariable BigDecimal amount) {
        String result = savingsAccountsService.addMoneyToTheAccountThroughTheCashier(series, number, accountNumber, amount);
        return ResponseEntity.ok("Успешно! Счет: " + accountNumber + " пополнен. Баланс: " + result);
    }

    @PatchMapping("/{series}/{number}/sender/{cardNumber}/recipient/{accountNumber}/{amount}")
    public ResponseEntity<String> transferFromCardToSavingsAccount(@PathVariable Integer series, @PathVariable Integer number,
                                                                   @PathVariable String cardNumber,
                                                                   @PathVariable String accountNumber, @PathVariable BigDecimal amount) {
        savingsAccountsService.transferFromCardToSavingsAccount(series, number, cardNumber, accountNumber, amount);
        return ResponseEntity.ok("Перевод с карты: " + cardNumber + " на сберегательный счет: " + accountNumber + " - выполнен!" );
    }

    @GetMapping("/series/{series}/number/{number}/checkBalance/{accountNumber}")
    public ResponseEntity<String> checkBalanceSavingsAccount(@PathVariable Integer series, @PathVariable Integer number,
                                                             @PathVariable String accountNumber) {
        return ResponseEntity.ok(savingsAccountsService.checkBalance(series, number, accountNumber));
    }

    @PatchMapping("/{series}/{number}/{senderAccountNumber}/{recipientAccountNumber}/{amount}")
    public ResponseEntity<String> transferFromSavingsAccountToSavingsAccount(@PathVariable Integer series, @PathVariable Integer number,
                                                                             @PathVariable String senderAccountNumber,
                                                                             @PathVariable String recipientAccountNumber, @PathVariable BigDecimal amount) {
        savingsAccountsService.transferFromSavingsAccountToSavingsAccount(series, number, senderAccountNumber, recipientAccountNumber, amount);
        return ResponseEntity.ok("Перевод со счета: " + senderAccountNumber + " на счет: " + recipientAccountNumber + " - выполнен!" );
    }
}