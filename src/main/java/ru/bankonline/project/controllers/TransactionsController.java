package ru.bankonline.project.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bankonline.project.entity.Transaction;
import ru.bankonline.project.services.transactionsservice.TransactionsService;

import java.util.List;


@RestController
@RequestMapping("/transactions")

public class TransactionsController {

    private final TransactionsService transactionsService;
    private final ModelMapper modelMapper;

    public TransactionsController(TransactionsService transactionsService, ModelMapper modelMapper) {
        this.transactionsService = transactionsService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/series/{series}/number/{number}")
    public ResponseEntity<List<Transaction>> getTransactionCustomer(@PathVariable Integer series, @PathVariable Integer number) {
        return ResponseEntity.ok((transactionsService.getTransactionCustomer(series, number)));
    }
}