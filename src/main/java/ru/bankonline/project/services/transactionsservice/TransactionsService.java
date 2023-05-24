package ru.bankonline.project.services.transactionsservice;

import ru.bankonline.project.entity.Card;
import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.entity.SavingsAccount;
import ru.bankonline.project.entity.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionsService {

    List<Transaction> getTransactionCustomer(Integer passportSeries, Integer passportNumber);

    void transactionToRegisterNewCustomer(Integer customerId);

    void transactionToDeleteCustomer(Integer customerId);

    void moneySendingToTheCardTransaction(Customer senderCustomer, Card senderCard, Card recipientCard, BigDecimal amount);

    void moneyReceiptToTheCardTransaction(Customer recipientCustomer, Card senderCard, Card recipientCard, BigDecimal amount);

    void transactionToCloseCard(Integer customerId);

    void transactionToUnlockCard(Integer customerId, Card card);

    void transactionToBlockCard(Integer customerId, Card card);

    void transactionToOpenCard(Customer customer);

    void cardBalanceRequestTransaction(Customer customer, Card card);

    void moneySendingToTheAccountTransaction(Customer senderCustomer, Card senderCard, SavingsAccount recipientAccount, BigDecimal amount);

    void moneyReceiptToTheAccountTransaction(Customer recipientCustomer, Card senderCard, SavingsAccount recipientAccount, BigDecimal amount);

    void savingsAccountBalanceRequestTransaction(Customer customer, SavingsAccount savingsAccount);

    void transactionReplenishmentBalanceThroughTheBankCashDesk(Customer customer, SavingsAccount savingsAccount, BigDecimal amount);

    void transactionToCloseSavingsAccount(Integer customerId);

    void transactionWithdrawalMoneyFromSavingsAccountThroughCashier(Customer customer, SavingsAccount savingsAccount);

    void transactionToOpenSavingAccount(Customer customer);

    void transactionSendingFromAccountToAccount(Customer senderCustomer, SavingsAccount senderSavingsAccount,
                                                SavingsAccount recipientSavingsAccount, BigDecimal amount);

    void transactionReceivingFromAccountToAccount(Customer recipientCustomer, SavingsAccount senderSavingsAccount,
                                                  SavingsAccount recipientSavingsAccount, BigDecimal amount);

    void transactionAccrualOfInterestOnTheDeposit(Integer customerId, SavingsAccount savingsAccount, BigDecimal amountAccruedInterest);
}