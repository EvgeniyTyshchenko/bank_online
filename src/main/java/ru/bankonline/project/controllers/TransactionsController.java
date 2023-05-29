package ru.bankonline.project.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bankonline.project.entity.Transaction;
import ru.bankonline.project.services.transactionsservice.TransactionsService;

import java.util.List;

/***
 * Контроллер для работы с транзакциями
 */
@Slf4j
@RestController
@RequestMapping("/transactions")
@Tag(name = "Транзакции", description = "CRUD-операции для работы с транзакциями")
public class TransactionsController {

    private final TransactionsService transactionsService;

    @Autowired
    public TransactionsController(TransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }

    /***
     * Получает все транзакции клиента
     * @param series серия паспорта
     * @param number номер паспорта
     * @return список транзакций по указанному клиенту
     */
    @Operation(summary = "Получить все транзакции клиента",
            description = "Необходимо вводить серию и номер паспорта клиента")
    @GetMapping("/series/{series}/number/{number}")
    public ResponseEntity<List<Transaction>> getTransactionCustomer(@PathVariable Integer series, @PathVariable Integer number) {
        log.info("Запрос на получение всех транзакций клиента");
        return ResponseEntity.ok((transactionsService.getTransactionCustomer(series, number)));
    }
}