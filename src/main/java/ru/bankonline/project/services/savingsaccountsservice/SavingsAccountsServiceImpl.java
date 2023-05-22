package ru.bankonline.project.services.savingsaccountsservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.entity.SavingsAccount;
import ru.bankonline.project.entity.Transaction;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.constants.Status;
import ru.bankonline.project.constants.TransactionType;
import ru.bankonline.project.repositories.SavingsAccountsRepository;
import ru.bankonline.project.services.customersservice.CustomersService;
import ru.bankonline.project.services.transactionsservice.TransactionsService;
import ru.bankonline.project.utils.exceptions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
public class SavingsAccountsServiceImpl implements SavingsAccountsService {

    private final SavingsAccountsRepository savingsAccountsRepository;
    private final CustomersService customersService;
    private final TransactionsService transactionsService;

    @Autowired
    public SavingsAccountsServiceImpl(SavingsAccountsRepository savingsAccountsRepository, CustomersService customersService,
                                      TransactionsService transactionsService) {
        this.savingsAccountsRepository = savingsAccountsRepository;
        this.customersService = customersService;
        this.transactionsService = transactionsService;
    }

    @Override
    @Transactional
    public void openSavingAccountToTheCustomer(Integer passportSeries, Integer passportNumber) {
        Customer existingCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(existingCustomer);

        String uniqueAccountNumber = UUID.randomUUID().toString().replaceAll("\\D", "0").substring(0, 20);

        SavingsAccount savingsAccount = new SavingsAccount(existingCustomer.getCustomerId(), uniqueAccountNumber, BigDecimal.valueOf(0),
                Currency.RUB, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now());

        savingsAccountsRepository.save(savingsAccount);
        transactionToOpenSavingAccount(existingCustomer);
        log.info("Открытие сберегательного счета. Номер счета: " + savingsAccount.getAccountNumber());
    }

    @Override
    @Transactional
    public String closeAccountAndWithdrawMoneyThroughCashier(Integer passportSeries, Integer passportNumber, String accountNumber) {
        Customer existingCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(existingCustomer);

        SavingsAccount savingsAccountExisting = checkSavingAccountExists(existingCustomer, accountNumber);
        checkIfTheSavingAccountIsNotClosedOrBlocked(savingsAccountExisting);
        String result = checkSavingAccountBalance(existingCustomer, savingsAccountExisting);
        enrichSavingAccountForClosure(savingsAccountExisting);
        transactionToClose(existingCustomer.getCustomerId());

        log.info("Закрытие сберегательного счета. Номер счета: " + savingsAccountExisting.getAccountNumber());
        return result;
    }

    @Override
    @Transactional
    public String addMoneyToTheAccountThroughTheCashier(Integer passportSeries, Integer passportNumber,
                                                        String accountNumber, BigDecimal amount) {
        Customer existingCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(existingCustomer);

        SavingsAccount savingsAccountExisting = checkSavingAccountExists(existingCustomer, accountNumber);
        checkIfTheSavingAccountIsNotClosedOrBlocked(savingsAccountExisting);
        checkIfThereIsMoneyOnTheSavingAccount(savingsAccountExisting);

        BigDecimal currentBalance = savingsAccountExisting.getBalance();
        BigDecimal newBalance = currentBalance.add(amount);
        savingsAccountExisting.setBalance(newBalance);
        savingsAccountsRepository.save(savingsAccountExisting);
        transactionBalanceReplenishment(existingCustomer, savingsAccountExisting, amount);

        log.info("Пополнение сберегательного счета через кассу. Номер счета: " + savingsAccountExisting.getAccountNumber()
                + ", сумма: " + amount + savingsAccountExisting.getCurrency());
        return String.format("%.2f %s", savingsAccountExisting.getBalance(), savingsAccountExisting.getCurrency());
    }

    @Override
    @Transactional
    public String checkBalance(Integer passportSeries, Integer passportNumber, String savingsAccountNumber) {
        Customer existingCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(existingCustomer);

        SavingsAccount savingsAccountExisting = checkSavingAccountExists(existingCustomer, savingsAccountNumber);
        checkIfTheSavingAccountIsNotClosedOrBlocked(savingsAccountExisting);
        transactionBalanceRequest(existingCustomer, savingsAccountExisting);

        log.info("Проверка баланса сберегательного счета. Номер счета: " + savingsAccountExisting.getAccountNumber());
        return String.format("Баланс: %.2f %s", savingsAccountExisting.getBalance(), savingsAccountExisting.getCurrency().toString());
    }

    @Override
    @Transactional
    public void transferFromSavingsAccountToSavingsAccount(Integer passportSeries, Integer passportNumber,
                                                           String senderSavingsAccountNumber, String recipientSavingsAccountNumber, BigDecimal amount) {
        Customer senderCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(senderCustomer);

        SavingsAccount accountSenderExists = checkSavingAccountExists(senderCustomer, senderSavingsAccountNumber);
        checkIfTheSavingAccountIsNotClosedOrBlocked(accountSenderExists);

        Customer recipientCustomer = customersService.getCustomerBySavingAccountNumber(recipientSavingsAccountNumber);
        SavingsAccount recipientAccountExisting = checkSavingAccountExists(recipientCustomer, recipientSavingsAccountNumber);
        checkIfTheSavingAccountIsNotClosedOrBlocked(recipientAccountExisting);
        checkIfThereIsMoneyOnTheSavingAccount(recipientAccountExisting);

        if (accountSenderExists.getBalance().compareTo(amount) >= 0) {
            accountSenderExists.setBalance(accountSenderExists.getBalance().subtract(amount));
            transactionSendingFromAccountToAccount(senderCustomer, accountSenderExists, recipientAccountExisting, amount);

            recipientAccountExisting.setBalance(recipientAccountExisting.getBalance().add(amount));
            transactionReceivingFromAccountToAccount(recipientCustomer, accountSenderExists, recipientAccountExisting, amount);
            savingsAccountsRepository.save(accountSenderExists);
            savingsAccountsRepository.save(recipientAccountExisting);

            log.info("Перевод между сберегательными счетами. Номер счета отправителя: " + accountSenderExists.getAccountNumber()
                    + ", номер счета получателя: " + recipientAccountExisting.getAccountNumber() + ", сумма: " + amount + accountSenderExists.getCurrency());
        } else {
            throw new InsufficientFundsException("На сберегательном счете недостататочно денежных средств для совершения транзакции! " +
                    "Пожалуйста, проверьте баланс и попробуйте снова.");
        }
    }

    @Override
    public void checkIfTheSavingAccountIsNotClosedOrBlocked(SavingsAccount savingsAccount) {
        if (savingsAccount.getStatus() == Status.CLOSED || savingsAccount.getStatus() == Status.BLOCKED) {
            throw new ClosingSavingsAccountException("Сберегательный счет " + savingsAccount.getAccountNumber() + " закрыт или заблокирован! " +
                    "Убедитесь, что Вы ввели правильные реквизиты!");
        }
    }

    @Override
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

    @Override
    public void checkIfThereIsMoneyOnTheSavingAccount(SavingsAccount savingsAccount) {
        if (savingsAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new ViolationTermsDepositException("Нарушение условий сберегательного счета! " +
                    "Данный сберегательный счет предполагает открытие и разовое пополнение.");
        }
    }

    @Override
    public void saveRepositorySavingsAccounts(SavingsAccount savingsAccount) {
        savingsAccountsRepository.save(savingsAccount);
    }

    @Override
    public List<SavingsAccount> findAllToSavingsAccountsRepository() {
        return savingsAccountsRepository.findAll();
    }

    @Override
    public List<SavingsAccount> findByCustomerIdToSavingsAccountsRepository(Integer customerId) {
        return savingsAccountsRepository.findByCustomerId(customerId);
    }

    private String checkSavingAccountBalance(Customer customer, SavingsAccount savingsAccount) {
        if (savingsAccount.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            transactionWithdrawalMoneyThroughCashier(customer, savingsAccount);
            savingsAccount.setBalance(savingsAccount.getBalance().subtract(savingsAccount.getBalance()));
            savingsAccountsRepository.save(savingsAccount);
        }
        return "Со сберегательного счета произведено полное списание денежных средств. " +
                "Клиенту требуется получить деньги в кассе.";
    }

    private void enrichSavingAccountForClosure(SavingsAccount savingsAccount) {
        savingsAccount.setStatus(Status.CLOSED);
        savingsAccount.setUpdateDate(LocalDateTime.now());
        savingsAccountsRepository.save(savingsAccount);
    }

    private void transactionBalanceRequest(Customer customer, SavingsAccount savingsAccount) {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[SA balance request]", "[SA balance request]",
                savingsAccount.getBalance(), Currency.RUB, TransactionType.CHECKBALANCE, LocalDateTime.now());
        transactionsService.saveTransactionsRepository(transaction);
    }

    private void transactionBalanceReplenishment(Customer customer, SavingsAccount savingsAccount, BigDecimal amount) {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[BANK]", savingsAccount.getAccountNumber(),
                amount, savingsAccount.getCurrency(), TransactionType.INTRANSFER, LocalDateTime.now());
        transactionsService.saveTransactionsRepository(transaction);
    }

    private void transactionToClose(Integer customerId) {
        Transaction transaction = new Transaction(customerId, "[closure]", "[closure]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.CLOSEACCOUNT, LocalDateTime.now());
        transactionsService.saveTransactionsRepository(transaction);
    }

    private void transactionWithdrawalMoneyThroughCashier(Customer customer, SavingsAccount savingsAccount) {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[BANK]", "[cash withdrawal]",
                savingsAccount.getBalance(), savingsAccount.getCurrency(), TransactionType.INTRANSFER, LocalDateTime.now());
        transactionsService.saveTransactionsRepository(transaction);
    }

    private void transactionToOpenSavingAccount(Customer customer) {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[discovery]", "[discovery]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.OPENACCOUNT, LocalDateTime.now());
        transactionsService.saveTransactionsRepository(transaction);
    }

    private void transactionSendingFromAccountToAccount(Customer senderCustomer, SavingsAccount senderSavingsAccount,
                                                        SavingsAccount recipientSavingsAccount, BigDecimal amount) {
        Transaction transaction = new Transaction(senderCustomer.getCustomerId(), senderSavingsAccount.getAccountNumber(), recipientSavingsAccount.getAccountNumber(),
                amount, senderSavingsAccount.getCurrency(), TransactionType.OUTTRANSFER, LocalDateTime.now());
        transactionsService.saveTransactionsRepository(transaction);
    }

    private void transactionReceivingFromAccountToAccount(Customer recipientCustomer, SavingsAccount senderSavingsAccount,
                                                          SavingsAccount recipientSavingsAccount, BigDecimal amount) {
        Transaction transaction = new Transaction(recipientCustomer.getCustomerId(), senderSavingsAccount.getAccountNumber(), recipientSavingsAccount.getAccountNumber(),
                amount, senderSavingsAccount.getCurrency(), TransactionType.INTRANSFER, LocalDateTime.now());
        transactionsService.saveTransactionsRepository(transaction);
    }
}