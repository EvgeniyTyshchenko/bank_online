package ru.bankonline.project.services.savingsaccountsservice;

import org.springframework.stereotype.Service;
import ru.bankonline.project.entity.Card;
import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.entity.SavingsAccount;
import ru.bankonline.project.entity.Transaction;
import ru.bankonline.project.entity.enums.Currency;
import ru.bankonline.project.entity.enums.Status;
import ru.bankonline.project.entity.enums.TransactionType;
import ru.bankonline.project.repositories.CardsRepository;
import ru.bankonline.project.repositories.SavingsAccountsRepository;
import ru.bankonline.project.repositories.TransactionsRepository;
import ru.bankonline.project.services.cardsservice.CardsService;
import ru.bankonline.project.services.customersservice.CustomersService;
import ru.bankonline.project.utils.exceptions.ClosingSavingsAccountException;
import ru.bankonline.project.utils.exceptions.EnteringSavingsAccountDataException;
import ru.bankonline.project.utils.exceptions.InsufficientFundsException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SavingsAccountsServiceImpl implements SavingsAccountsService {

    private final SavingsAccountsRepository savingsAccountsRepository;
    private final CardsRepository cardsRepository;
    private final TransactionsRepository transactionsRepository;
    private final CustomersService customersService;
    private final CardsService cardsService;

    public SavingsAccountsServiceImpl(SavingsAccountsRepository savingsAccountsRepository, CardsRepository cardsRepository,
                                      TransactionsRepository transactionsRepository, CustomersService customersService,
                                      CardsService cardsService) {
        this.savingsAccountsRepository = savingsAccountsRepository;
        this.cardsRepository = cardsRepository;
        this.transactionsRepository = transactionsRepository;
        this.customersService = customersService;
        this.cardsService = cardsService;
    }

    @Override
    public void openSavingAccountToTheCustomer(Integer passportSeries, Integer passportNumber) {
        Customer existingCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(existingCustomer);

        String uniqueAccountNumber = UUID.randomUUID().toString().replaceAll("[^0-9]", "0").substring(0, 20);

        SavingsAccount savingsAccount = new SavingsAccount(existingCustomer.getCustomerId(), uniqueAccountNumber, BigDecimal.valueOf(0),
                Currency.RUB, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now());

        savingsAccountsRepository.save(savingsAccount);
        transactionToOpenSavingAccount(existingCustomer);
    }

    @Override
    public void closeSavingAccount(Integer passportSeries, Integer passportNumber, String accountNumber) {
        Customer existingCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(existingCustomer);

        SavingsAccount savingsAccount = checkSavingAccountExists(existingCustomer, accountNumber);
        checkIfTheSavingAccountIsNotClosedOrBlocked(savingsAccount);
        checkSavingAccountBalance(savingsAccount);
        enrichSavingAccountForClosure(savingsAccount);
        transactionToClose(existingCustomer.getCustomerId());
    }

    @Override
    public String addMoneyToTheAccountThroughTheCashier(Integer passportSeries, Integer passportNumber,
                                                        String accountNumber, BigDecimal amount) {
        Customer existingCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(existingCustomer);

        SavingsAccount savingsAccount = checkSavingAccountExists(existingCustomer, accountNumber);
        checkIfTheSavingAccountIsNotClosedOrBlocked(savingsAccount);
        BigDecimal currentBalance = savingsAccount.getBalance();
        BigDecimal newBalance = currentBalance.add(amount);
        savingsAccount.setBalance(newBalance);
        savingsAccountsRepository.save(savingsAccount);
        transactionBalanceReplenishment(existingCustomer, savingsAccount, amount);
        return String.format("%.2f %s", savingsAccount.getBalance(), savingsAccount.getCurrency());
    }

    @Override
    public void transferFromCardToSavingsAccount(Integer passportSeries, Integer passportNumber,
                                                 String senderCardNumber, String recipientSavingsAccountNumber, BigDecimal amount) {
        Customer senderCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(senderCustomer);

        Card senderCard = cardsService.checkCardExists(senderCustomer, senderCardNumber);
        cardsService.checkIfTheCardIsNotClosedOrBlocked(senderCard);

        Customer recipientCustomer = customersService.getCustomerBySavingAccountNumber(recipientSavingsAccountNumber);
        SavingsAccount savingsAccountExists = checkSavingAccountExists(recipientCustomer, recipientSavingsAccountNumber);
        checkIfTheSavingAccountIsNotClosedOrBlocked(savingsAccountExists);

        if (senderCard.getBalance().compareTo(amount) >= 0) {
            senderCard.setBalance(senderCard.getBalance().subtract(amount));
            moneySendingToTheAccountTransaction(senderCustomer, senderCard, savingsAccountExists, amount);

            savingsAccountExists.setBalance(savingsAccountExists.getBalance().add(amount));
            moneyReceiptToTheAccountTransaction(recipientCustomer, senderCard, savingsAccountExists, amount);
            cardsRepository.save(senderCard);
            savingsAccountsRepository.save(savingsAccountExists);
        } else {
            throw new InsufficientFundsException("Недостаточно денежных средств! "
                    + "Пожалуйста, проверьте баланс на карте " + senderCard.getCardNumber()
                    + " и попробуйте снова.");
        }
    }

    @Override
    public String checkBalance(Integer passportSeries, Integer passportNumber, String savingsAccountNumber) {
        Customer existingCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(existingCustomer);

        SavingsAccount savingsAccountExists = checkSavingAccountExists(existingCustomer, savingsAccountNumber);
        checkIfTheSavingAccountIsNotClosedOrBlocked(savingsAccountExists);
        transactionBalanceRequest(existingCustomer, savingsAccountExists);
        return String.format("Баланс: %.2f %s", savingsAccountExists.getBalance(), savingsAccountExists.getCurrency().toString());
    }

    private void transactionBalanceRequest(Customer customer, SavingsAccount savingsAccount) {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[SA balance request]", "[SA balance request]",
                savingsAccount.getBalance(), Currency.RUB, TransactionType.CHECKBALANCE, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    private void transactionBalanceReplenishment(Customer customer, SavingsAccount savingsAccount, BigDecimal amount) {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[BANK]", savingsAccount.getAccountNumber(),
                amount, savingsAccount.getCurrency(), TransactionType.INTRANSFER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    private void moneySendingToTheAccountTransaction(Customer senderCustomer, Card senderCard, SavingsAccount recipientAccount, BigDecimal amount) {
        Transaction transaction = new Transaction(senderCustomer.getCustomerId(), senderCard.getAccountNumber(), recipientAccount.getAccountNumber(),
                amount, senderCard.getCurrency(), TransactionType.OUTTRANSFER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    private void moneyReceiptToTheAccountTransaction(Customer recipientCustomer, Card senderCard, SavingsAccount recipientAccount, BigDecimal amount) {
        Transaction transaction = new Transaction(recipientCustomer.getCustomerId(), senderCard.getAccountNumber(), recipientAccount.getAccountNumber(),
                amount, senderCard.getCurrency(), TransactionType.INTRANSFER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    private void transactionToClose(Integer customerId) {
        Transaction transaction = new Transaction(customerId, "[closure]", "[closure]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.CLOSEACCOUNT, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    private void enrichSavingAccountForClosure(SavingsAccount savingsAccount) {
        savingsAccount.setStatus(Status.CLOSED);
        savingsAccount.setUpdateDate(LocalDateTime.now());
        savingsAccountsRepository.save(savingsAccount);
    }

    private void checkSavingAccountBalance(SavingsAccount savingsAccount) {
        if (savingsAccount.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new ClosingSavingsAccountException("Ошибка в закрытии сберегательного счета! Пожалуйста, убедитесь, что баланс равен 0. " +
                    "Вы можете сделать заявку на снятие денег в кассе или же перевести оставшуюся сумму на другой счет.");
        }
    }

    public void checkIfTheSavingAccountIsNotClosedOrBlocked(SavingsAccount savingsAccount) {
        if (savingsAccount.getStatus() == Status.CLOSED || savingsAccount.getStatus() == Status.BLOCKED) {
            throw new ClosingSavingsAccountException("Сберегательный счет закрыт или заблокирован! " +
                    "Убедитесь, что Вы ввели правильные реквизиты!");
        }
    }

    public SavingsAccount checkSavingAccountExists(Customer customer, String accountNumber) {
        for (SavingsAccount savingsAccount : customer.getSavingsAccounts()) {
            if (savingsAccount.getAccountNumber().equals(accountNumber)) {
                return savingsAccount;
            }
        }
        throw new EnteringSavingsAccountDataException("Номер счета, который вы вводите отсутствует у клиента "
                + customer.getLastName() + " " + customer.getFirstName() + " " + customer.getPatronymic()
                + " Проверьте реквизиты сберегательного счета и попробуйте снова.");
    }

    private void transactionToOpenSavingAccount(Customer customer) {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[discovery]", "[discovery]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.OPENACCOUNT, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }
}