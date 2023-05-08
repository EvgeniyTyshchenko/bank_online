package ru.bankonline.project.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bankonline.project.entity.Transaction;
import ru.bankonline.project.services.transactionsservice.TransactionsService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/transactions")

public class TransactionsController {

    private final TransactionsService transactionsService;

    public TransactionsController(TransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }

    @GetMapping("/series/{series}/number/{number}")
    public ResponseEntity<List<Transaction>> getTransactionCustomer(@PathVariable Integer series, @PathVariable Integer number) {
        log.info("Запрос на получение всех транзакций клиента");
        return ResponseEntity.ok((transactionsService.getTransactionCustomer(series, number)));
    }
}