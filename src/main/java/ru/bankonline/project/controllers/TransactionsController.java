package ru.bankonline.project.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bankonline.project.entity.Transaction;
import ru.bankonline.project.services.transactionsservice.TransactionsService;


@RestController
@RequestMapping("/transactions")

public class TransactionsController {

    private final TransactionsService transactionsService;

    public TransactionsController(TransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }

    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> addNewTransaction(@RequestBody Transaction transaction) {
        transactionsService.save(transaction);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}