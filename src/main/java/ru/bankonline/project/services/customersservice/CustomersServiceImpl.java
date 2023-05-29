package ru.bankonline.project.services.customersservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bankonline.project.entity.Card;
import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.entity.SavingsAccount;
import ru.bankonline.project.constants.Status;
import ru.bankonline.project.repositories.CustomersRepository;
import ru.bankonline.project.services.MailSender;
import ru.bankonline.project.services.cardsservice.CardsService;
import ru.bankonline.project.services.savingsaccountsservice.SavingsAccountsService;
import ru.bankonline.project.services.transactionsservice.TransactionsService;
import ru.bankonline.project.utils.exceptions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/***
 * Сервис для работы с клиентами
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class CustomersServiceImpl implements CustomersService {

    private final CustomersRepository customersRepository;
    private final TransactionsService transactionsService;
    private final CardsService cardsService;
    private final SavingsAccountsService savingsAccountsService;
    private final MailSender mailSender;

    @Autowired
    public CustomersServiceImpl(CustomersRepository customersRepository, @Lazy TransactionsService transactionsService,
                                @Lazy CardsService cardsService, @Lazy SavingsAccountsService savingsAccountsService,
                                MailSender mailSender) {
        this.customersRepository = customersRepository;
        this.transactionsService = transactionsService;
        this.cardsService = cardsService;
        this.savingsAccountsService = savingsAccountsService;
        this.mailSender = mailSender;
    }

    /***
     * Добавляет нового клиента
     * @param customer объект Customer, содержащий данные о клиенте
     */
    @Override
    @Transactional
    public void addNewCustomer(Customer customer) {
        checkTheExistenceOfPassportDataInTheDatabase(customer.getPassportSeries(), customer.getPassportNumber());
        enrichCustomerToActivate(customer);
        customersRepository.save(customer);
        transactionsService.transactionToRegisterNewCustomer(customer.getCustomerId());

        String message = "Здравствуйте, " + customer.getFirstName() + " " + customer.getPatronymic() + "! \n"
                + "Добро пожаловать в наш банк!";
        mailSender.sendEmail(customer.getContactDetails().getEmail(), "Регистрация в банке", message);

        log.info("Клиент {} добавлен", customer.getLastName() + " "
                + customer.getFirstName() + " " + customer.getPatronymic());
    }

    /***
     * Получает информацию о клиенте по серии и номеру паспорта
     * @param passportSeries серия паспорта
     * @param passportNumber номер паспорта
     * @return объект Customer
     * @throws CustomerMissingFromDBException если клиент не найден в базе данных
     */
    @Override
    public Customer customerSearchByPassportSeriesAndNumber(Integer passportSeries, Integer passportNumber) {
        log.info("Поиск клиента по серии {} и номеру {} паспорта", passportSeries, passportNumber);
        return customersRepository.findByPassportSeriesAndPassportNumber(passportSeries, passportNumber)
                .orElseThrow(() -> new CustomerMissingFromDBException("Клиент отсутствует в базе!"));
    }

    /***
     * Закрывает учетную запись клиента, а также реализуется закрытие всех банковских карт и сберегательных счетов
     * указанного клиента
     * @param passportSeries серия паспорта
     * @param passportNumber номер паспорта
     * @throws CustomerBalanceNotZeroException если у клиента на картах и/или сберегательных счета имеются денежные средства
     */
    @Override
    @Transactional
    public void closingCustomer(Integer passportSeries, Integer passportNumber) {
        Customer customer = customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        checkIfTheCustomerIsBlockedOrClosed(customer);

        List<Card> cards = cardsService.findByCustomerIdToCardsRepository(customer.getCustomerId());
        List<SavingsAccount> savingsAccounts = savingsAccountsService
                .findByCustomerIdToSavingsAccountsRepository(customer.getCustomerId());

        boolean hasMoneyOnCard = checkIfThereIsMoneyOnTheCards(cards);
        boolean hasMoneyOnSavingsAccount = checkIfThereIsMoneyInSavingsAccounts(savingsAccounts);

        if (hasMoneyOnCard || hasMoneyOnSavingsAccount) {
            throw new CustomerBalanceNotZeroException("Ошибка в закрытии аккаунта! У клиента " + customer.getLastName() + " "
                    + customer.getFirstName() + " " + customer.getPatronymic() + " на картах и/или счетах имеются денежные средства. " +
                    "Для корректного выполнения операции, Вам необходимо снять/перевести ВСЕ денежные средства со своих счетов и/или карт.");
        } else {
            cardsService.closeAllCardsInTheList(cards);
            savingsAccountsService.closeAllSavingsAccountsInTheList(savingsAccounts);
        }
        enrichCustomerToClose(customer);
        transactionsService.transactionToCloseCustomer(customer.getCustomerId());

        log.info("Учетная запись клиента {} закрыта", customer.getLastName() + " "
                + customer.getFirstName() + " " + customer.getPatronymic());
    }

    /***
     * Обновляет учетную запись клиента
     * @param passportSeries серия паспорта
     * @param passportNumber номер паспорта
     * @param customer объект Customer с обновленной информацией
     */
    @Override
    @Transactional
    public void updateCustomer(Integer passportSeries, Integer passportNumber, Customer customer) {
        Customer existingCustomer = customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        existingCustomer.setPassportSeries(customer.getPassportSeries());
        existingCustomer.setPassportNumber(customer.getPassportNumber());
        existingCustomer.setLastName(customer.getLastName());
        existingCustomer.setFirstName(customer.getFirstName());
        existingCustomer.setPatronymic(customer.getPatronymic());
        existingCustomer.setBirthday(customer.getBirthday());

        existingCustomer.setUpdateDate(LocalDateTime.now());
        customersRepository.save(existingCustomer);
        checkForDuplicatePassportData(existingCustomer.getPassportSeries(), existingCustomer.getPassportNumber());

        log.info("Клиент {} обновлен", customer.getLastName() + " "
                + customer.getFirstName() + " " + customer.getPatronymic());
    }

    /***
     * Получает информацию о клиенте по номеру карты
     * @param cardNumber номер карты
     * @return объект Customer с необходимой информацией
     * @throws CustomerMissingFromDBException если клиент не найден в базе данных банка
     */
    @Override
    public Customer getCustomerByCardNumber(String cardNumber) {
        return customersRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new CustomerMissingFromDBException("По номеру карты: " + cardNumber + " клиент не найден!"));
    }

    /***
     * Получает информацию о клиенте по номеру сберегательного счета
     * @param savingAccountNumber номер сберегательного счета
     * @return объект Customer с необходимой информацией
     * @throws CustomerMissingFromDBException если клиент не найден в базе данных банка
     */
    @Override
    public Customer getCustomerBySavingAccountNumber(String savingAccountNumber) {
        return customersRepository.findBySavingAccountNumber(savingAccountNumber)
                .orElseThrow(() -> new CustomerMissingFromDBException("По номеру сберегательного счета: " + savingAccountNumber + " клиент не найден!"));
    }

    /***
     * Проверяет на блокировку или закрытие клиента
     * @param customer объект Customer
     * @throws CustomerBlockingException если клиент blocked/closed
     */
    @Override
    public void checkIfTheCustomerIsBlockedOrClosed(Customer customer) {
        if (customer.getStatus() == Status.BLOCKED || customer.getStatus() == Status.CLOSED) {
            log.info("Клиент {} заблокирован или закрыт", customer.getLastName() + " "
                    + customer.getFirstName() + " " + customer.getPatronymic());
            throw new CustomerBlockingException("Клиент заблокирован или закрыт!");
        }
    }

    /***
     * Сохраняет клиента в репозиторий
     * @param customer клиент, которого нужно сохранить
     */
    @Override
    public void saveCustomersRepository(Customer customer) {
        customersRepository.save(customer);
    }

    /***
     * Проверяет наличие серии и номера паспорта в базе данных банка
     * @param passportSeries серия паспорта
     * @param passportNumber номер паспорта
     * @throws NotCreatedException если клиент с такими паспортными данными уже есть в базе
     */
    private void checkTheExistenceOfPassportDataInTheDatabase(Integer passportSeries, Integer passportNumber) {
        Optional<Customer> existingCustomer = customersRepository.findByPassportSeriesAndPassportNumber(passportSeries, passportNumber);
        if (existingCustomer.isPresent()) {
            log.info("Клиент с серией паспорта {} и номером {} уже существует", passportSeries, passportNumber);
            throw new NotCreatedException("Клиент с такими серией и номером паспорта уже существует!");
        }
    }

    /***
     * Проверяет наличие денег на картах
     * @param cards список карт
     * @return true, если на одной из карт есть деньги, иначе false
     */
    private boolean checkIfThereIsMoneyOnTheCards(List<Card> cards) {
        for (Card card : cards) {
            if (card.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                log.info("Количество денежных средств на карте > 0");
                return true;
            }
        }
        return false;
    }

    /***
     * Проверяет наличие денег на сберегательных счетах
     * @param savingsAccounts список сберегательных счетов
     * @return true, если на одном из сберегательных счетов есть деньги, иначе false
     */
    private boolean checkIfThereIsMoneyInSavingsAccounts(List<SavingsAccount> savingsAccounts) {
        for (SavingsAccount savingsAccount : savingsAccounts) {
            if (savingsAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                log.info("Количество денежных средств на сберегательном счете > 0");
                return true;
            }
        }
        return false;
    }

    /***
     * Закрывает учетную запись клиента
     * @param customer объект Customer
     */
    private void enrichCustomerToClose(Customer customer) {
        customer.setStatus(Status.CLOSED);
        customer.setUpdateDate(LocalDateTime.now());
        customersRepository.save(customer);
    }

    /***
     * Проверяет наличие дублируемых паспортных данных в базе данных банка (предотвращает ситуацию, где менеджер банка
     * ИЗМЕНЯЕТ серию и номер паспорта клиента, но ошибается в данных и пытается сохранить в базу, где такие серия и номер уже есть)
     * @param passportSeries серия паспорта
     * @param passportNumber номер паспорта
     * @throws PassportDuplicateException если указанные серия и номер паспорта уже есть в базе данных банка
     */
    private void checkForDuplicatePassportData(Integer passportSeries, Integer passportNumber) {
        Integer duplicatesCount = customersRepository.findPassportDuplicates(passportSeries, passportNumber)
                .orElseThrow(() -> new NotUpdatedException("Ошибка в обновлении!"));

        if (duplicatesCount > 1) {
            log.info("Такой паспорт уже есть в базе. Серия: {}, номер: {}", passportSeries, passportNumber);
            throw new PassportDuplicateException("Паспорт с указанными серией и номером уже есть в базе.");
        }
    }

    /***
     * Активирует учетную запись клиента
     * @param customer объект Customer
     */
    private static void enrichCustomerToActivate(Customer customer) {
        customer.setStatus(Status.ACTIVE);
        customer.setCreateDate(LocalDateTime.now());
        customer.setUpdateDate(LocalDateTime.now());
    }
}