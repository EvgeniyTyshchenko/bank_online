package ru.bankonline.project.services.customersservice;

import org.springframework.stereotype.Service;
import ru.bankonline.project.entity.Card;
import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.entity.SavingsAccount;
import ru.bankonline.project.entity.Transaction;
import ru.bankonline.project.entity.enums.Currency;
import ru.bankonline.project.entity.enums.Status;
import ru.bankonline.project.entity.enums.TransactionType;
import ru.bankonline.project.repositories.CardsRepository;
import ru.bankonline.project.repositories.CustomersRepository;
import ru.bankonline.project.repositories.SavingsAccountsRepository;
import ru.bankonline.project.repositories.TransactionsRepository;
import ru.bankonline.project.utils.exceptions.CustomerBalanceNotZeroException;
import ru.bankonline.project.utils.exceptions.CustomerBlockingException;
import ru.bankonline.project.utils.exceptions.CustomerMissingFromDBException;
import ru.bankonline.project.utils.exceptions.NotCreatedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomersServiceImpl implements CustomersService {

    private final CustomersRepository customersRepository;
    private final TransactionsRepository transactionsRepository;
    private final CardsRepository cardsRepository;
    private final SavingsAccountsRepository savingsAccountsRepository;

    public CustomersServiceImpl(CustomersRepository customersRepository, TransactionsRepository transactionsRepository, CardsRepository cardsRepository, SavingsAccountsRepository savingsAccountsRepository) {
        this.customersRepository = customersRepository;
        this.transactionsRepository = transactionsRepository;
        this.cardsRepository = cardsRepository;
        this.savingsAccountsRepository = savingsAccountsRepository;
    }

    @Override
    public void addNewCustomer(Customer customer) {
        isPassportExists(customer.getPassportSeries(), customer.getPassportNumber());
        enrichCustomer(customer);
        customersRepository.save(customer);
        transactionToRegisterNewCustomer(customer.getCustomerId());
    }

    @Override
    public Customer customerSearchByPassportSeriesAndNumber(Integer passportSeries, Integer passportNumber) {
        return getCustomerByPassportSeriesAndNumber(passportSeries, passportNumber);
    }

    @Override
    public void deleteCustomer(Integer passportSeries, Integer passportNumber) {
        Customer customer = getCustomerByPassportSeriesAndNumber(passportSeries, passportNumber);
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
    }

    @Override
    public void updateCustomer(Integer passportSeries, Integer passportNumber, Customer customer) {
        Customer existingCustomer = getCustomerByPassportSeriesAndNumber(passportSeries, passportNumber);
        existingCustomer.setLastName(customer.getLastName());
        existingCustomer.setFirstName(customer.getFirstName());
        existingCustomer.setPatronymic(customer.getPatronymic());
        existingCustomer.setBirthday(customer.getBirthday());

        existingCustomer.setUpdateDate(LocalDateTime.now());
        customersRepository.save(existingCustomer);
    }

    @Override
    public Customer getCustomerByCardNumber(String cardNumber) {
        Customer customer = customersRepository.findByCardNumber(cardNumber);
        if (customer == null) {
            throw new CustomerMissingFromDBException("По номеру карты: " + cardNumber + " клиент не найден!");
        }
        return customer;
    }

    @Override
    public Customer getCustomerBySavingAccountNumber(String savingAccountNumber) {
        Customer customer = customersRepository.findBySavingAccountNumber(savingAccountNumber);
        if (customer == null) {
            throw new CustomerMissingFromDBException("По номеру сберегательного счета: " + savingAccountNumber + " клиент не найден!");
        }
        return customer;
    }

    private boolean checkIfHasBalanceOnCard(List<Card> cards) {
        for (Card card : cards) {
            if (card.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                return true;
            }
        }
        return false;
    }

    private boolean checkIfHasBalanceOnSavingsAccount(List<SavingsAccount> savingsAccounts) {
        for (SavingsAccount savingsAccount : savingsAccounts) {
            if (savingsAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                return true;
            }
        }
        return false;
    }

    public void closeCards(List<Card> cards) {
        for (Card card : cards) {
            card.setStatus(Status.CLOSED);
            card.setUpdateDate(LocalDateTime.now());
            cardsRepository.save(card);
        }
    }

    public void closeSavingsAccounts(List<SavingsAccount> savingsAccounts) {
        for (SavingsAccount savingsAccount : savingsAccounts) {
            savingsAccount.setStatus(Status.CLOSED);
            savingsAccount.setUpdateDate(LocalDateTime.now());
            savingsAccountsRepository.save(savingsAccount);
        }
    }

    public void closeCustomer(Customer customer) {
        customer.setStatus(Status.CLOSED);
        customer.setUpdateDate(LocalDateTime.now());
        customersRepository.save(customer);
    }

    private List<Card> getCardsByCustomerId(Integer customerId) {
        return cardsRepository.findByCustomerId(customerId);
    }

    private List<SavingsAccount> getSavingsAccountsByCustomerId(Integer customerId) {
        return savingsAccountsRepository.findByCustomerId(customerId);
    }

    private void isPassportExists(Integer passportSeries, Integer passportNumber) {
        Customer existingCustomer = customersRepository.findByPassportSeriesAndPassportNumber(passportSeries, passportNumber);
        if (existingCustomer != null) {
            throw new NotCreatedException("Клиент с такими серией и номером паспорта уже существует!");
        }
    }

    private Customer getCustomerByPassportSeriesAndNumber(Integer passportSeries, Integer passportNumber) {
        if (passportSeries != null && passportNumber != null) {
            Customer customer = customersRepository.findByPassportSeriesAndPassportNumber(passportSeries, passportNumber);
            if (customer != null) {
                return customer;
            }
        }
        throw new CustomerMissingFromDBException("Клиент отсутсвует в базе!");
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

    @Override
    public void checkIfTheCustomerIsBlockedOrDeleted(Customer customer) {
        if (customer.getStatus() == Status.BLOCKED || customer.getStatus() == Status.CLOSED) {
            throw new CustomerBlockingException("Клиент заблокирован или удален!");
        }
    }
}