package ru.bankonline.project.services.transactionsservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bankonline.project.entity.Card;
import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.entity.SavingsAccount;
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
        transactionOfRequestingTheEntireListByCustomer(customer);
        log.info("Запрос по серии {} и номеру {} паспорта, для получения всего списка транзакций " +
                "- произведен.", passportSeries, passportNumber);
        return transactions;
    }

    @Override
    public void transactionToRegisterNewCustomer(Integer customerId) {
        Transaction transaction = new Transaction(customerId, "[registration]", "[registration]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.REGISTERCUSTOMER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    @Override
    public void transactionToDeleteCustomer(Integer customerId) {
        Transaction transaction = new Transaction(customerId, "[removal]", "[removal]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.DELETECUSTOMER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    @Override
    public void moneySendingToTheCardTransaction(Customer senderCustomer, Card senderCard, Card recipientCard, BigDecimal amount) {
        Transaction transaction = new Transaction(senderCustomer.getCustomerId(), senderCard.getAccountNumber(), recipientCard.getAccountNumber(),
                amount, senderCard.getCurrency(), TransactionType.OUTTRANSFER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    @Override
    public void moneyReceiptToTheCardTransaction(Customer recipientCustomer, Card senderCard, Card recipientCard, BigDecimal amount) {
        Transaction transaction = new Transaction(recipientCustomer.getCustomerId(), senderCard.getAccountNumber(), recipientCard.getAccountNumber(),
                amount, senderCard.getCurrency(), TransactionType.INTRANSFER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    @Override
    public void transactionToCloseCard(Integer customerId) {
        Transaction transaction = new Transaction(customerId, "[closure]", "[closure]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.CLOSECARD, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    @Override
    public void transactionToUnlockCard(Integer customerId, Card card) {
        Transaction transaction = new Transaction(customerId, "[unblocking]", "[unblocking]",
                card.getBalance(), Currency.RUB, TransactionType.UNLOCKINGCARD, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    @Override
    public void transactionToBlockCard(Integer customerId, Card card) {
        Transaction transaction = new Transaction(customerId, "[blocking]", "[blocking]",
                card.getBalance(), Currency.RUB, TransactionType.BLOCKINGCARD, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    @Override
    public void transactionToOpenCard(Customer customer) {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[discovery]", "[discovery]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.OPENCARD, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    @Override
    public void cardBalanceRequestTransaction(Customer customer, Card card) {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[card balance request]", "[card balance request]",
                card.getBalance(), Currency.RUB, TransactionType.CHECKBALANCE, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    @Override
    public void moneySendingToTheAccountTransaction(Customer senderCustomer, Card senderCard, SavingsAccount recipientAccount, BigDecimal amount) {
        Transaction transaction = new Transaction(senderCustomer.getCustomerId(), senderCard.getAccountNumber(), recipientAccount.getAccountNumber(),
                amount, senderCard.getCurrency(), TransactionType.OUTTRANSFER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    @Override
    public void moneyReceiptToTheAccountTransaction(Customer recipientCustomer, Card senderCard, SavingsAccount recipientAccount, BigDecimal amount) {
        Transaction transaction = new Transaction(recipientCustomer.getCustomerId(), senderCard.getAccountNumber(), recipientAccount.getAccountNumber(),
                amount, senderCard.getCurrency(), TransactionType.INTRANSFER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    @Override
    public void savingsAccountBalanceRequestTransaction(Customer customer, SavingsAccount savingsAccount) {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[SA balance request]", "[SA balance request]",
                savingsAccount.getBalance(), Currency.RUB, TransactionType.CHECKBALANCE, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    @Override
    public void transactionReplenishmentBalanceThroughTheBankCashDesk(Customer customer, SavingsAccount savingsAccount, BigDecimal amount) {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[BANK]", savingsAccount.getAccountNumber(),
                amount, savingsAccount.getCurrency(), TransactionType.INTRANSFER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    @Override
    public void transactionToCloseSavingsAccount(Integer customerId) {
        Transaction transaction = new Transaction(customerId, "[closure]", "[closure]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.CLOSEACCOUNT, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    @Override
    public void transactionWithdrawalMoneyFromSavingsAccountThroughCashier(Customer customer, SavingsAccount savingsAccount) {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[BANK]", "[cash withdrawal]",
                savingsAccount.getBalance(), savingsAccount.getCurrency(), TransactionType.INTRANSFER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    @Override
    public void transactionToOpenSavingAccount(Customer customer) {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[discovery]", "[discovery]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.OPENACCOUNT, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    @Override
    public void transactionSendingFromAccountToAccount(Customer senderCustomer, SavingsAccount senderSavingsAccount,
                                                       SavingsAccount recipientSavingsAccount, BigDecimal amount) {
        Transaction transaction = new Transaction(senderCustomer.getCustomerId(), senderSavingsAccount.getAccountNumber(), recipientSavingsAccount.getAccountNumber(),
                amount, senderSavingsAccount.getCurrency(), TransactionType.OUTTRANSFER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    @Override
    public void transactionReceivingFromAccountToAccount(Customer recipientCustomer, SavingsAccount senderSavingsAccount,
                                                         SavingsAccount recipientSavingsAccount, BigDecimal amount) {
        Transaction transaction = new Transaction(recipientCustomer.getCustomerId(), senderSavingsAccount.getAccountNumber(), recipientSavingsAccount.getAccountNumber(),
                amount, senderSavingsAccount.getCurrency(), TransactionType.INTRANSFER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    @Override
    public void transactionAccrualOfInterestOnTheDeposit(Integer customerId, SavingsAccount savingsAccount, BigDecimal amountAccruedInterest) {
        Transaction transaction = new Transaction(customerId, "[BANK]", savingsAccount.getAccountNumber(),
                amountAccruedInterest, Currency.RUB, TransactionType.CAPITALIZATION, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    private void transactionOfRequestingTheEntireListByCustomer(Customer customer) {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[general request]", "[general request]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.CHECKTRANSACTIONLIST, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }
}