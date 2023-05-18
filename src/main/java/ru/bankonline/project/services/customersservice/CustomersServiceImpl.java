package ru.bankonline.project.services.customersservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bankonline.project.entity.Card;
import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.entity.SavingsAccount;
import ru.bankonline.project.entity.Transaction;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.constants.Status;
import ru.bankonline.project.constants.TransactionType;
import ru.bankonline.project.repositories.CardsRepository;
import ru.bankonline.project.repositories.CustomersRepository;
import ru.bankonline.project.repositories.SavingsAccountsRepository;
import ru.bankonline.project.repositories.TransactionsRepository;
import ru.bankonline.project.services.MailSender;
import ru.bankonline.project.utils.exceptions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CustomersServiceImpl implements CustomersService {

    private final CustomersRepository customersRepository;
    private final TransactionsRepository transactionsRepository;
    private final CardsRepository cardsRepository;
    private final SavingsAccountsRepository savingsAccountsRepository;
    private final MailSender mailSender;

    public CustomersServiceImpl(CustomersRepository customersRepository, TransactionsRepository transactionsRepository,
                                CardsRepository cardsRepository, SavingsAccountsRepository savingsAccountsRepository,
                                MailSender mailSender) {
        this.customersRepository = customersRepository;
        this.transactionsRepository = transactionsRepository;
        this.cardsRepository = cardsRepository;
        this.savingsAccountsRepository = savingsAccountsRepository;
        this.mailSender = mailSender;
    }

    @Override
    @Transactional
    public void addNewCustomer(Customer customer) {
        isPassportExists(customer.getPassportSeries(), customer.getPassportNumber());
        enrichCustomer(customer);
        customersRepository.save(customer);
        transactionToRegisterNewCustomer(customer.getCustomerId());

        String message = "Здравствуйте, " + customer.getFirstName() + " " + customer.getPatronymic() + "! \n"
                + "Добро пожаловать в наш банк!";
        mailSender.sendEmail(customer.getContactDetails().getEmail(), "Регистрация в банке", message);

        log.info("Клиент {} добавлен", customer.getLastName() + " "
                + customer.getFirstName() + " " + customer.getPatronymic());
    }

    @Override
    public Customer customerSearchByPassportSeriesAndNumber(Integer passportSeries, Integer passportNumber) {
        log.info("Поиск клиента по серии {} и номеру {} паспорта", passportSeries, passportNumber);
        return customersRepository.findByPassportSeriesAndPassportNumber(passportSeries, passportNumber)
                .orElseThrow(() -> new CustomerMissingFromDBException("Клиент отсутсвует в базе!"));
    }

    @Override
    @Transactional
    public void deleteCustomer(Integer passportSeries, Integer passportNumber) {
        Customer customer = customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        checkIfTheCustomerIsBlockedOrDeleted(customer);

        List<Card> cards = getCardsByCustomerId(customer.getCustomerId());
        List<SavingsAccount> savingsAccounts = getSavingsAccountsByCustomerId(customer.getCustomerId());

        boolean hasBalanceOnCard = checkIfHasBalanceOnCard(cards);
        boolean hasBalanceOnSavingsAccount = checkIfHasBalanceOnSavingsAccount(savingsAccounts);

        if (hasBalanceOnCard || hasBalanceOnSavingsAccount) {
            throw new CustomerBalanceNotZeroException("Ошибка в удалении аккаунта! У клиента " + customer.getLastName() + " "
                    + customer.getFirstName() + " " + customer.getPatronymic() + " на картах и/или счетах имеются денежные средства. " +
                    "Для корректного выполнения операции, Вам необходимо снять/перевести ВСЕ денежные средства со своих счетов и/или карт.");
        } else {
            closeCards(cards);
            closeSavingsAccounts(savingsAccounts);
        }
        closeCustomer(customer);
        transactionToDeleteCustomer(customer.getCustomerId());

        log.info("Клиент {} удален", customer.getLastName() + " "
                + customer.getFirstName() + " " + customer.getPatronymic());
    }

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
        areThereDuplicatesOfPassportData(existingCustomer.getPassportSeries(), existingCustomer.getPassportNumber());

        log.info("Клиент {} обновлен", customer.getLastName() + " "
                + customer.getFirstName() + " " + customer.getPatronymic());
    }

    @Override
    public Customer getCustomerByCardNumber(String cardNumber) {
        return customersRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new CustomerMissingFromDBException("По номеру карты: " + cardNumber + " клиент не найден!"));
    }

    @Override
    public Customer getCustomerBySavingAccountNumber(String savingAccountNumber) {
        return customersRepository.findBySavingAccountNumber(savingAccountNumber)
                .orElseThrow(() -> new CustomerMissingFromDBException("По номеру сберегательного счета: " + savingAccountNumber + " клиент не найден!"));
    }

    @Override
    public void checkIfTheCustomerIsBlockedOrDeleted(Customer customer) {
        if (customer.getStatus() == Status.BLOCKED || customer.getStatus() == Status.CLOSED) {
            log.info("Клиент {} заблокирован или удален", customer.getLastName() + " "
                    + customer.getFirstName() + " " + customer.getPatronymic());
            throw new CustomerBlockingException("Клиент заблокирован или удален!");
        }
    }

    private boolean checkIfHasBalanceOnCard(List<Card> cards) {
        for (Card card : cards) {
            if (card.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                log.info("Количество денежных средств на карте > 0");
                return true;
            }
        }
        return false;
    }

    private boolean checkIfHasBalanceOnSavingsAccount(List<SavingsAccount> savingsAccounts) {
        for (SavingsAccount savingsAccount : savingsAccounts) {
            if (savingsAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                log.info("Количество денежных средств на сберегательном счете > 0");
                return true;
            }
        }
        return false;
    }

    private void closeCards(List<Card> cards) {
        for (Card card : cards) {
            card.setStatus(Status.CLOSED);
            card.setUpdateDate(LocalDateTime.now());
            cardsRepository.save(card);
        }
    }

    private void closeSavingsAccounts(List<SavingsAccount> savingsAccounts) {
        for (SavingsAccount savingsAccount : savingsAccounts) {
            savingsAccount.setStatus(Status.CLOSED);
            savingsAccount.setUpdateDate(LocalDateTime.now());
            savingsAccountsRepository.save(savingsAccount);
        }
    }

    private void closeCustomer(Customer customer) {
        customer.setStatus(Status.CLOSED);
        customer.setUpdateDate(LocalDateTime.now());
        customersRepository.save(customer);
    }

    private void areThereDuplicatesOfPassportData(Integer passportSeries, Integer passportNumber) {
        Integer duplicatesCount = customersRepository.findPassportDuplicates(passportSeries, passportNumber)
                .orElseThrow(() -> new NotUpdatedException("Ошибка в обновлении!"));

        if (duplicatesCount > 1) {
            log.info("Такой паспорт уже есть в базе. Серия: {}, номер: {}", passportSeries, passportNumber);
            throw new PassportDuplicateException("Паспорт с указанными серией и номером уже есть в базе.");
        }
    }

    private void isPassportExists(Integer passportSeries, Integer passportNumber) {
        Optional<Customer> existingCustomer = customersRepository.findByPassportSeriesAndPassportNumber(passportSeries, passportNumber);
        if (existingCustomer.isPresent()) {
            log.info("Клиент с серией паспорта {} и номером {} уже существует", passportSeries, passportNumber);
            throw new NotCreatedException("Клиент с такими серией и номером паспорта уже существует!");
        }
    }

    private List<Card> getCardsByCustomerId(Integer customerId) {
        return cardsRepository.findByCustomerId(customerId);
    }

    private List<SavingsAccount> getSavingsAccountsByCustomerId(Integer customerId) {
        return savingsAccountsRepository.findByCustomerId(customerId);
    }

    private void transactionToRegisterNewCustomer(Integer customerId) {
        Transaction transaction = new Transaction(customerId, "[registration]", "[registration]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.REGISTERCUSTOMER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    private void transactionToDeleteCustomer(Integer customerId) {
        Transaction transaction = new Transaction(customerId, "[removal]", "[removal]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.DELETECUSTOMER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    private static void enrichCustomer(Customer customer) {
        customer.setStatus(Status.ACTIVE);
        customer.setCreateDate(LocalDateTime.now());
        customer.setUpdateDate(LocalDateTime.now());
    }
}