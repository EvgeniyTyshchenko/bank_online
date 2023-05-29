package ru.bankonline.project.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bankonline.project.services.savingsaccountsservice.SavingsAccountsService;

/***
 * Контроллер для работы со сберегательными счетами
 */
@RestController
@RequestMapping("/savingsAccounts")
@Tag(name = "Сберегательные счета", description = "CRUD-операции для работы со сберегательными счетами")
public class SavingsAccountsController {

    private final SavingsAccountsService savingsAccountsService;

    @Autowired
    public SavingsAccountsController(SavingsAccountsService savingsAccountsService) {
        this.savingsAccountsService = savingsAccountsService;
    }

    /***
     * Открывает сберегательный счет клиенту банка
     * @param series серия паспорта
     * @param number номер паспорта
     * @return статус 200 в случае успешного открытия сберегательного счета
     */
    @Operation(summary = "Открытие сберегательного счета",
            description = "Необходимо вводить серию и номер паспорта клиента")
    @PostMapping(path = "/series/{series}/number/{number}")
    public ResponseEntity<HttpStatus> addNewSavingAccountToTheCustomer(@PathVariable Integer series,
                                                                       @PathVariable Integer number) {
        savingsAccountsService.openSavingAccountToTheCustomer(series, number);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    /***
     * Закрывает сберегательный счет и списывает все денежные средства
     * Клиенту необходимо получить денежные средства в кассе банка
     * @param series серия паспорта
     * @param number номер паспорта
     * @param accountNumber номер сберегательного счета
     * @return сообщение об успешном выполнении операции
     */
    @Operation(summary = "Закрытие сберегательного счета и полное списание денежных средств (клиент получает деньги через" +
            "кассу банка)",
            description = "Необходимо вводить серию и номер паспорта клиента, " +
                    "а также номер счета, который требуется закрыть")
    @PatchMapping("/series/{series}/number/{number}/close/{accountNumber}")
    public ResponseEntity<String> closeTheCustomerSavingAccount(@PathVariable Integer series, @PathVariable Integer number,
                                                                @PathVariable String accountNumber) {
        String result = savingsAccountsService.closeSavingsAccount(series, number, accountNumber);
        return ResponseEntity.ok(result + " Сберегательный счет с номером " + accountNumber + " успешно закрыт!");
    }

    /***
     * Проверяет баланс сберегательного счета
     * @param series серия паспорта
     * @param number номер паспорта
     * @param accountNumber номер сберегательного счета, на котором необходимо проверить баланс
     * @return ответ сервера с информацией о балансе сберегательного счета
     */
    @Operation(summary = "Проверка баланса сберегательного счета",
            description = "Необходимо вводить серию и номер паспорта клиента, " +
                    "а также номер счета, где нужно узнать баланс")
    @GetMapping("/series/{series}/number/{number}/checkBalance/{accountNumber}")
    public ResponseEntity<String> checkBalanceSavingsAccount(@PathVariable Integer series, @PathVariable Integer number,
                                                             @PathVariable String accountNumber) {
        return ResponseEntity.ok(savingsAccountsService.checkBalance(series, number, accountNumber));
    }
}