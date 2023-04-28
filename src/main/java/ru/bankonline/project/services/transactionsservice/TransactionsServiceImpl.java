package ru.bankonline.project.services.transactionsservice;

import org.springframework.stereotype.Service;
import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.entity.Transaction;
import ru.bankonline.project.entity.enums.Currency;
import ru.bankonline.project.entity.enums.TransactionType;
import ru.bankonline.project.repositories.TransactionsRepository;
import ru.bankonline.project.services.customersservice.CustomersService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionsServiceImpl implements TransactionsService {

    private final TransactionsRepository transactionsRepository;
    private final CustomersService customersService;

    public TransactionsServiceImpl(TransactionsRepository transactionsRepository, CustomersService customersService) {
        this.transactionsRepository = transactionsRepository;
        this.customersService = customersService;
    }

    @Override
    public List<Transaction> getTransactionCustomer(Integer passportSeries, Integer passportNumber) {
        Customer customer = customersService.customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        transactionCheckingTheList(customer);
        return transactionsRepository.findAllByCustomerId(customer.getCustomerId());
    }

    private void transactionCheckingTheList(Customer customer) {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[general request]", "[general request]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.CHECKTRANSACTIONLIST, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }
}