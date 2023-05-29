package ru.bankonline.project.services.savingsaccountsservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.entity.SavingsAccount;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.constants.Status;
import ru.bankonline.project.repositories.SavingsAccountsRepository;
import ru.bankonline.project.services.customersservice.CustomersService;
import ru.bankonline.project.services.transactionsservice.TransactionsService;
import ru.bankonline.project.utils.exceptions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/***
 * Сервис для работы со сберегательными счетами
 */
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

    /***
     * Открывает сберегательный счет клиенту
     * @param passportSeries серия паспорта
     * @param passportNumber номер паспорта
     */
    @Override
    @Transactional
    public void openSavingAccountToTheCustomer(Integer passportSeries, Integer passportNumber) {
        Customer existingCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrClosed(existingCustomer);

        String uniqueAccountNumber = UUID.randomUUID().toString().replaceAll("\\D", "0").substring(0, 20);

        SavingsAccount savingsAccount = new SavingsAccount(existingCustomer.getCustomerId(), uniqueAccountNumber, BigDecimal.valueOf(0),
                Currency.RUB, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now());

        savingsAccountsRepository.save(savingsAccount);
        transactionsService.transactionToOpenSavingAccount(existingCustomer);
        log.info("Открытие сберегательного счета. Номер счета: " + savingsAccount.getAccountNumber());
    }

    /***
     * Закрывает сберегательный счет и обнуляет баланс (денежные средства выдаются через кассу банка)
     * @param passportSeries серия паспорта
     * @param passportNumber номер паспорта
     * @param accountNumber номер сберегательного счета
     * @return сообщение об успешном списании денежных средств и необходимости клиенту получить деньги в кассе банка
     */
    @Override
    @Transactional
    public String closeSavingsAccount(Integer passportSeries, Integer passportNumber, String accountNumber) {
        Customer existingCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrClosed(existingCustomer);

        SavingsAccount savingsAccountExisting = checkWhetherTheSavingsAccountBelongsToTheCustomer(existingCustomer, accountNumber);
        checkIfTheSavingAccountIsNotClosedOrBlocked(savingsAccountExisting);
        String result = checkBalanceSavingAccountAndWriteOffTheMoney(existingCustomer, savingsAccountExisting);
        enrichSavingAccountForClosure(savingsAccountExisting);
        transactionsService.transactionToCloseSavingsAccount(existingCustomer.getCustomerId());

        log.info("Закрытие сберегательного счета. Номер счета: " + savingsAccountExisting.getAccountNumber());
        return result;
    }

    /***
     * Пополняет сберегательный счет через кассу банка
     * @param passportSeries серия паспорта
     * @param passportNumber номер паспорта
     * @param accountNumber номер сберегательного счета
     * @param amount количество
     * @return баланс с учетом внесенных денежных средств через кассу банка
     */
    @Override
    @Transactional
    public String addMoneyToTheAccountThroughTheCashier(Integer passportSeries, Integer passportNumber,
                                                        String accountNumber, BigDecimal amount) {
        Customer existingCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrClosed(existingCustomer);

        SavingsAccount savingsAccountExisting = checkWhetherTheSavingsAccountBelongsToTheCustomer(existingCustomer, accountNumber);
        checkIfTheSavingAccountIsNotClosedOrBlocked(savingsAccountExisting);
        checkIfThereIsMoneyOnTheSavingAccount(savingsAccountExisting);

        BigDecimal currentBalance = savingsAccountExisting.getBalance();
        BigDecimal newBalance = currentBalance.add(amount);
        savingsAccountExisting.setBalance(newBalance);
        savingsAccountsRepository.save(savingsAccountExisting);
        transactionsService.transactionOfReceiptOfFundsToSavingsAccountThroughTheBankCashDesk(existingCustomer, savingsAccountExisting, amount);

        log.info("Пополнение сберегательного счета через кассу. Номер счета: " + savingsAccountExisting.getAccountNumber()
                + ", сумма: " + amount + savingsAccountExisting.getCurrency());
        return String.format("%.2f %s", savingsAccountExisting.getBalance(), savingsAccountExisting.getCurrency());
    }

    /***
     * Проверяет баланс сберегательного счета
     * @param passportSeries серия паспорта
     * @param passportNumber номер паспорта
     * @param savingsAccountNumber номер сберегательного счета
     * @return сообщение с актуальным балансом
     */
    @Override
    @Transactional
    public String checkBalance(Integer passportSeries, Integer passportNumber, String savingsAccountNumber) {
        Customer existingCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrClosed(existingCustomer);

        SavingsAccount savingsAccountExisting = checkWhetherTheSavingsAccountBelongsToTheCustomer(existingCustomer, savingsAccountNumber);
        checkIfTheSavingAccountIsNotClosedOrBlocked(savingsAccountExisting);
        transactionsService.savingsAccountBalanceRequestTransaction(existingCustomer, savingsAccountExisting);

        log.info("Проверка баланса сберегательного счета. Номер счета: " + savingsAccountExisting.getAccountNumber());
        return String.format("Баланс: %.2f %s", savingsAccountExisting.getBalance(), savingsAccountExisting.getCurrency().toString());
    }

    /***
     * Переводит денежные средства между сберегательными счетами клиентов банка
     * @param passportSeries серия паспорта
     * @param passportNumber номер паспорта
     * @param senderSavingsAccountNumber номер сберегательного счета отправителя
     * @param recipientSavingsAccountNumber номер сберегательного счета получателя
     * @param amount количество
     * @throws InsufficientFundsException если недостаточно денежных средств у отправителя (для совершения транзакции)
     */
    @Override
    @Transactional
    public void transferFromSavingsAccountToSavingsAccount(Integer passportSeries, Integer passportNumber,
                                                           String senderSavingsAccountNumber, String recipientSavingsAccountNumber, BigDecimal amount) {
        Customer senderCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrClosed(senderCustomer);

        SavingsAccount accountSenderExists = checkWhetherTheSavingsAccountBelongsToTheCustomer(senderCustomer, senderSavingsAccountNumber);
        checkIfTheSavingAccountIsNotClosedOrBlocked(accountSenderExists);

        Customer recipientCustomer = customersService.getCustomerBySavingAccountNumber(recipientSavingsAccountNumber);
        SavingsAccount recipientAccountExisting = checkWhetherTheSavingsAccountBelongsToTheCustomer(recipientCustomer, recipientSavingsAccountNumber);
        checkIfTheSavingAccountIsNotClosedOrBlocked(recipientAccountExisting);
        checkIfThereIsMoneyOnTheSavingAccount(recipientAccountExisting);

        if (accountSenderExists.getBalance().compareTo(amount) >= 0) {
            accountSenderExists.setBalance(accountSenderExists.getBalance().subtract(amount));
            transactionsService.moneyTransferTransactionFromSavingsAccountToSavingsAccount(senderCustomer, accountSenderExists, recipientAccountExisting, amount);

            recipientAccountExisting.setBalance(recipientAccountExisting.getBalance().add(amount));
            transactionsService.transactionOfReceiptOfFundsFromSavingsAccountToSavingsAccount(recipientCustomer, accountSenderExists, recipientAccountExisting, amount);
            savingsAccountsRepository.save(accountSenderExists);
            savingsAccountsRepository.save(recipientAccountExisting);

            log.info("Перевод между сберегательными счетами. Номер счета отправителя: " + accountSenderExists.getAccountNumber()
                    + ", номер счета получателя: " + recipientAccountExisting.getAccountNumber() + ", сумма: " + amount + accountSenderExists.getCurrency());
        } else {
            throw new InsufficientFundsException("На сберегательном счете недостаточно денежных средств для совершения транзакции! " +
                    "Пожалуйста, проверьте баланс и попробуйте снова.");
        }
    }

    /***
     * Проверяет на закрытие или блокировку сберегательного счета
     * @param savingsAccount объект SavingsAccount
     * @throws ClosingSavingsAccountException если сберегательный счет closed/blocked
     */
    @Override
    public void checkIfTheSavingAccountIsNotClosedOrBlocked(SavingsAccount savingsAccount) {
        if (savingsAccount.getStatus() == Status.CLOSED || savingsAccount.getStatus() == Status.BLOCKED) {
            throw new ClosingSavingsAccountException("Сберегательный счет " + savingsAccount.getAccountNumber() + " закрыт или заблокирован! " +
                    "Убедитесь, что Вы ввели правильные реквизиты!");
        }
    }

    /***
     * Проверяет принадлежность сберегательного счета клиенту
     * @param customer объект Customer
     * @param accountNumber номер сберегательного счета
     * @return объект SavingsAccount
     * @throws EnteringSavingsAccountDataException если указанный номер счета отсутствует у клиента банка
     */
    @Override
    public SavingsAccount checkWhetherTheSavingsAccountBelongsToTheCustomer(Customer customer, String accountNumber) {
        for (SavingsAccount savingsAccount : customer.getSavingsAccounts()) {
            if (savingsAccount.getAccountNumber().equals(accountNumber)) {
                return savingsAccount;
            }
        }
        throw new EnteringSavingsAccountDataException("Номер счета, который вы вводите отсутствует у клиента "
                + customer.getLastName() + " " + customer.getFirstName() + " " + customer.getPatronymic()
                + " Проверьте реквизиты сберегательного счета и попробуйте снова.");
    }

    /***
     * Проверяет наличие денежных средств на сберегательном счете
     * @param savingsAccount объект SavingsAccount
     * @throws ViolationTermsDepositException если нарушаются условия сберегательного счета.
     * Условия: при открытии сберегательного счета, клиент имеет право пополнить данный счет один раз.
     */
    @Override
    public void checkIfThereIsMoneyOnTheSavingAccount(SavingsAccount savingsAccount) {
        if (savingsAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new ViolationTermsDepositException("Нарушение условий сберегательного счета! " +
                    "Данный сберегательный счет предполагает открытие и разовое пополнение.");
        }
    }

    /***
     * Закрывает все сберегательные счета
     * @param savingsAccounts список сберегательных счетов
     */
    @Override
    public void closeAllSavingsAccountsInTheList(List<SavingsAccount> savingsAccounts) {
        for (SavingsAccount savingsAccount : savingsAccounts) {
            savingsAccount.setStatus(Status.CLOSED);
            savingsAccount.setUpdateDate(LocalDateTime.now());
            savingsAccountsRepository.save(savingsAccount);
        }
    }

    /***
     * Получает список сберегательных счетов, принадлежащих указанному идентификатору клиента
     * @param customerId ID клиента
     * @return список сберегательных счетов
     */
    @Override
    public List<SavingsAccount> findByCustomerIdToSavingsAccountsRepository(Integer customerId) {
        return savingsAccountsRepository.findByCustomerId(customerId);
    }

    /***
     * Сохраняет сберегательный счет в репозиторий
     * @param savingsAccount сберегательный счет, который нужно сохранить
     */
    @Override
    public void saveRepositorySavingsAccounts(SavingsAccount savingsAccount) {
        savingsAccountsRepository.save(savingsAccount);
    }

    /***
     * Получает все сберегательные счета клиентов банка
     * @return список сберегательных счетов
     */
    @Override
    public List<SavingsAccount> findAllToSavingsAccountsRepository() {
        return savingsAccountsRepository.findAll();
    }

    /***
     * Получает сберегательный счет по указанному идентификатору
     * @param accountId ID сберегательного счета
     * @return объект Optional, содержащий найденный сберегательный счет или null, если счет не найден
     */
    @Override
    public Optional<SavingsAccount> findByIdToSavingsAccountsRepository(Integer accountId) {
        return savingsAccountsRepository.findById(accountId);
    }

    /***
     * Проверяет баланс сберегательного счета и списывает деньги
     * @param customer объект Customer
     * @param savingsAccount объект SavingsAccount
     * @return сообщение об успешном списании денежных средств со сберегательного счета
     * и необходимости клиенту получить денежные средства в кассе банка
     */
    private String checkBalanceSavingAccountAndWriteOffTheMoney(Customer customer, SavingsAccount savingsAccount) {
        if (savingsAccount.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            transactionsService.transactionWithdrawalMoneyFromSavingsAccountThroughCashier(customer, savingsAccount);
            savingsAccount.setBalance(savingsAccount.getBalance().subtract(savingsAccount.getBalance()));
            savingsAccountsRepository.save(savingsAccount);
        }
        return "Со сберегательного счета произведено полное списание денежных средств. " +
                "Клиенту требуется получить деньги в кассе.";
    }

    /***
     * Закрывает сберегательный счет
     * @param savingsAccount объект SavingsAccount
     */
    private void enrichSavingAccountForClosure(SavingsAccount savingsAccount) {
        savingsAccount.setStatus(Status.CLOSED);
        savingsAccount.setUpdateDate(LocalDateTime.now());
        savingsAccountsRepository.save(savingsAccount);
    }
}