package ru.bankonline.project.services.transactionsservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.entity.Transaction;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.constants.TransactionType;
import ru.bankonline.project.repositories.TransactionsRepository;
import ru.bankonline.project.services.customersservice.CustomersService;
import ru.bankonline.project.utils.exceptions.NotFoundInBaseException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class TransactionsServiceImpl implements TransactionsService {

    private final TransactionsRepository transactionsRepository;
    private final CustomersService customersService;

    @Autowired
    public TransactionsServiceImpl(TransactionsRepository transactionsRepository, CustomersService customersService) {
        this.transactionsRepository = transactionsRepository;
        this.customersService = customersService;
    }

    @Override
    @Transactional
    public List<Transaction> getTransactionCustomer(Integer passportSeries, Integer passportNumber) {
        Customer customer = customersService.customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        List<Transaction> transactions = transactionsRepository.findAllByCustomerId(customer.getCustomerId());
        if (transactions.isEmpty()) {
            throw new NotFoundInBaseException("Список транзакций пуст.");
        }
        transactionCheckingTheList(customer);
        log.info("Запрос по серии {} и номеру {} паспорта, для получения всего списка транзакций " +
                "- произведен.", passportSeries, passportNumber);
        return transactions;
    }

    @Override
    public void saveTransactionsRepository(Transaction transaction) {
        transactionsRepository.save(transaction);
    }

    private void transactionCheckingTheList(Customer customer) {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[general request]", "[general request]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.CHECKTRANSACTIONLIST, LocalDateTime.now());
        saveTransactionsRepository(transaction);
    }
}