package ru.bankonline.project.services.transactionsservice;

import ru.bankonline.project.entity.Transaction;

import java.util.List;

public interface TransactionsService {

    List<Transaction> getTransactionCustomer(Integer passportSeries, Integer passportNumber);

    void saveTransactionsRepository(Transaction transaction);
}