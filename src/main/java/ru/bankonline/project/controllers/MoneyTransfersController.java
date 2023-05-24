package ru.bankonline.project.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bankonline.project.services.cardsservice.CardsService;
import ru.bankonline.project.services.savingsaccountsservice.SavingsAccountsService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/moneyTransfers")
public class MoneyTransfersController {

    private final CardsService cardsService;
    private final SavingsAccountsService savingsAccountsService;

    @Autowired
    public MoneyTransfersController(CardsService cardsService, SavingsAccountsService savingsAccountsService) {
        this.cardsService = cardsService;
        this.savingsAccountsService = savingsAccountsService;
    }

    @Operation(summary = "Перевод денежных средств между картами клиентов",
            description = "Необходимо вводить серию, номер паспорта, номер карты клиента (отправителя), " +
                    "номер карты клиента (получателя), а также количество денежных средств для перевода")
    @PatchMapping("/cards/{series}/{number}/{senderCardNumber}/{recipientCardNumber}/{amount}")
    public ResponseEntity<String> transferBetweenCardsCustomers(@PathVariable Integer series, @PathVariable Integer number,
                                                                @PathVariable String senderCardNumber,
                                                                @PathVariable String recipientCardNumber, @PathVariable BigDecimal amount) {
        cardsService.transferBetweenCards(series, number, senderCardNumber, recipientCardNumber, amount);
        return ResponseEntity.ok("Перевод с карты: " + senderCardNumber + " на карту: " + recipientCardNumber + " - выполнен!" );
    }

    @Operation(summary = "Пополнение сберегательного счета через кассу банка",
            description = "Необходимо вводить серию, номер паспорта клиента, номер счета " +
                    "а также количество денежных средств для начисления")
    @PatchMapping("/{series}/{number}/{accountNumber}/{amount}")
    public ResponseEntity<String> addMoneyToTheAccount(@PathVariable Integer series, @PathVariable Integer number,
                                                       @PathVariable String accountNumber, @PathVariable BigDecimal amount) {
        String result = savingsAccountsService.addMoneyToTheAccountThroughTheCashier(series, number, accountNumber, amount);
        return ResponseEntity.ok("Успешно! Счет: " + accountNumber + " пополнен. Баланс: " + result);
    }

    @Operation(summary = "Перевод денежных средств между картой и сберегательным счетом",
            description = "Необходимо вводить серию, номер паспорта, номер карты клиента (отправителя), " +
                    "номер сберегательного счета (получателя), а также количество денежных средств для перевода")
    @PatchMapping("/{series}/{number}/sender/{cardNumber}/recipient/{accountNumber}/{amount}")
    public ResponseEntity<String> transferFromCardToSavingsAccount(@PathVariable Integer series, @PathVariable Integer number,
                                                                   @PathVariable String cardNumber,
                                                                   @PathVariable String accountNumber, @PathVariable BigDecimal amount) {
        cardsService.transferFromCardToSavingsAccount(series, number, cardNumber, accountNumber, amount);
        return ResponseEntity.ok("Перевод с карты: " + cardNumber + " на сберегательный счет: " + accountNumber + " - выполнен!" );
    }

    @Operation(summary = "Перевод денежных средств между сберегательными счетами клиентов",
            description = "Необходимо вводить серию, номер паспорта, номер счета клиента (отправителя), " +
                    "номер счета клиента (получателя), а также количество денежных средств для перевода")
    @PatchMapping("/savingsAccounts/{series}/{number}/{senderAccountNumber}/{recipientAccountNumber}/{amount}")
    public ResponseEntity<String> transferFromSavingsAccountToSavingsAccount(@PathVariable Integer series, @PathVariable Integer number,
                                                                             @PathVariable String senderAccountNumber,
                                                                             @PathVariable String recipientAccountNumber, @PathVariable BigDecimal amount) {
        savingsAccountsService.transferFromSavingsAccountToSavingsAccount(series, number, senderAccountNumber, recipientAccountNumber, amount);
        return ResponseEntity.ok("Перевод со счета: " + senderAccountNumber + " на счет: " + recipientAccountNumber + " - выполнен!" );
    }
}
