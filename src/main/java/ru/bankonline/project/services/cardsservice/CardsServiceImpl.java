package ru.bankonline.project.services.cardsservice;

import org.springframework.stereotype.Service;
import ru.bankonline.project.entity.Card;
import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.entity.Transaction;
import ru.bankonline.project.entity.enums.Currency;
import ru.bankonline.project.entity.enums.Status;
import ru.bankonline.project.entity.enums.TransactionType;
import ru.bankonline.project.repositories.CardsRepository;
import ru.bankonline.project.repositories.TransactionsRepository;
import ru.bankonline.project.services.customersservice.CustomersService;
import ru.bankonline.project.utils.exceptions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class CardsServiceImpl implements CardsService {

    private final CardsRepository cardsRepository;
    private final TransactionsRepository transactionsRepository;
    private final CustomersService customersService;

    public CardsServiceImpl(CardsRepository cardsRepository, TransactionsRepository transactionsRepository,
                            CustomersService customersService) {
        this.cardsRepository = cardsRepository;
        this.transactionsRepository = transactionsRepository;
        this.customersService = customersService;
    }

    @Override
    public void openCardToTheCustomer(Integer passportSeries, Integer passportNumber) {
        Customer existingCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(existingCustomer);

        String uniqueCardNumber = UUID.randomUUID().toString().replaceAll("[^0-9]", "0").substring(0, 16);
        String uniqueCVV = String.valueOf((int)(Math.random() * 900) + 100);
        String uniqueAccountNumber = UUID.randomUUID().toString().replaceAll("[^0-9]", "0").substring(0, 20);

        Card card = new Card(existingCustomer.getCustomerId(), uniqueCardNumber, uniqueCVV, uniqueAccountNumber,
                BigDecimal.valueOf(0), Currency.RUB, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now());

        cardsRepository.save(card);
        transactionToOpenCard(existingCustomer);
    }

    @Override
    public void closeCard(Integer passportSeries, Integer passportNumber, String cardNumber) {
        Customer existingCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(existingCustomer);

        Card card = checkCardExists(existingCustomer, cardNumber);
        checkIfTheCardIsNotClosedOrBlocked(card);

        checkCardBalance(card);
        enrichCardForClosure(card);
        transactionToClose(existingCustomer.getCustomerId());
    }

    @Override
    public void blockCard(Integer passportSeries, Integer passportNumber, String cardNumber) {
        Customer existingCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(existingCustomer);

        Card card = checkCardExists(existingCustomer, cardNumber);
        checkIfTheCardIsNotClosedOrBlocked(card);
        enrichCardForBlocking(card);
        transactionToBlock(existingCustomer.getCustomerId(), card);
    }

    @Override
    public void unlockCard(Integer passportSeries, Integer passportNumber, String cardNumber) {
        Customer existingCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(existingCustomer);

        Card card = checkCardExists(existingCustomer, cardNumber);
        checkIfTheCardIsClosedOrActive(card);
        enrichCardForUnlock(card);
        transactionToUnlock(existingCustomer.getCustomerId(), card);
    }

    @Override
    public String checkBalance(Integer passportSeries, Integer passportNumber, String cardNumber) {
        Customer existingCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(existingCustomer);

        Card card = checkCardExists(existingCustomer, cardNumber);
        checkIfTheCardIsNotClosedOrBlocked(card);
        transactionBalanceRequest(existingCustomer, card);
        return String.format("Баланс: %.2f %s", card.getBalance(), card.getCurrency().toString());
    }

    @Override
    public void transferBetweenCards(Integer passportSeries, Integer passportNumber,
                                     String senderCardNumber, String recipientCardNumber, BigDecimal amount) {
        Customer senderCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(senderCustomer);

        Card senderCard = checkCardExists(senderCustomer, senderCardNumber);
        checkIfTheCardIsNotClosedOrBlocked(senderCard);

        Customer recipientCustomer = customersService.getCustomerByCardNumber(recipientCardNumber);
        Card recipientCard = checkCardExists(recipientCustomer, recipientCardNumber);
        checkIfTheCardIsNotClosedOrBlocked(recipientCard);

        if (senderCard.getBalance().compareTo(amount) >= 0) {
            senderCard.setBalance(senderCard.getBalance().subtract(amount));
            moneySendingToTheCardTransaction(senderCustomer, senderCard, recipientCard, amount);

            recipientCard.setBalance(recipientCard.getBalance().add(amount));
            moneyReceiptToTheCardTransaction(recipientCustomer, senderCard, recipientCard, amount);
            cardsRepository.save(senderCard);
            cardsRepository.save(recipientCard);
        } else {
            throw new InsufficientFundsException("Недостаточно денежных средств для совершения транзакции!");
        }
    }

    @Override
    public Card getCardDetails(Integer passportSeries, Integer passportNumber, String cardNumber) {
        Customer existingCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(existingCustomer);

        return checkCardExists(existingCustomer, cardNumber);
    }

    @Override
    public Card checkCardExists(Customer customer, String cardNumber) {
        for (Card card : customer.getCards()) {
            if (card.getCardNumber().equals(cardNumber)) {
                return card;
            }
        }
        throw new EnteringCardDataException("Номер карты, который вы вводите отсутствует у клиента "
                + customer.getLastName() + " " + customer.getFirstName() + " " + customer.getPatronymic()
                + " Проверьте реквизиты карты и попробуйте снова.");
    }

    @Override
    public void checkIfTheCardIsNotClosedOrBlocked(Card card) {
        if (card.getStatus() == Status.CLOSED || card.getStatus() == Status.BLOCKED) {
            throw new ClosingCardException("Данная карта закрыта или заблокирована! " +
                    "Убедитесь, что Вы ввели правильные реквизиты карты!");
        }
    }

//    private Card checkAccountNumberCardExists(Customer existingCustomer, String accountNumber) {
//        for (Card card : existingCustomer.getCards()) {
//            if (card.getAccountNumber().equals(accountNumber)) {
//                return card;
//            }
//        }
//        throw new EnteringCardDataException("Клиент " + existingCustomer.getLastName() + " " + existingCustomer.getFirstName()
//                + " " + existingCustomer.getPatronymic() + " не имеет указанных реквизитов! " +
//                "Проверьте введенные данные и попробуйте снова.");
//    }

    private void moneySendingToTheCardTransaction(Customer senderCustomer, Card senderCard, Card recipientCard, BigDecimal amount) {
        Transaction transaction = new Transaction(senderCustomer.getCustomerId(), senderCard.getAccountNumber(), recipientCard.getAccountNumber(),
                amount, senderCard.getCurrency(), TransactionType.OUTTRANSFER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    private void moneyReceiptToTheCardTransaction(Customer recipientCustomer, Card senderCard, Card recipientCard, BigDecimal amount) {
        Transaction transaction = new Transaction(recipientCustomer.getCustomerId(), senderCard.getAccountNumber(), recipientCard.getAccountNumber(),
                amount, senderCard.getCurrency(), TransactionType.INTRANSFER, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    private void transactionBalanceRequest(Customer customer, Card card) {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[card balance request]", "[card balance request]",
                card.getBalance(), Currency.RUB, TransactionType.CHECKBALANCE, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    private void enrichCardForUnlock(Card card) {
        card.setStatus(Status.ACTIVE);
        card.setUpdateDate(LocalDateTime.now());
        cardsRepository.save(card);
    }

    private static void checkIfTheCardIsClosedOrActive(Card card) {
        if (card.getStatus() == Status.CLOSED || card.getStatus() == Status.ACTIVE) {
            throw new ClosingCardException("Данная карта закрыта или активна! " +
                    "Убедитесь, что Вы ввели правильные реквизиты карты!");
        }
    }

    private void checkCardBalance(Card card) {
        if (card.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new ClosingCardException("Ошибка в закрытии карты! Пожалуйста, убедитесь, что баланс равен 0. " +
                    "Вы можете сделать заявку на снятие денег в кассе, снять деньги в банкомате или же перевести оставшуюся сумму на другой счет.");
        }
    }

    private void enrichCardForClosure(Card card) {
        card.setStatus(Status.CLOSED);
        card.setUpdateDate(LocalDateTime.now());
        cardsRepository.save(card);
    }

    private void enrichCardForBlocking(Card card) {
        card.setStatus(Status.BLOCKED);
        card.setUpdateDate(LocalDateTime.now());
        cardsRepository.save(card);
    }

    private void transactionToClose(Integer customerId) {
        Transaction transaction = new Transaction(customerId, "[closure]", "[closure]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.CLOSECARD, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    private void transactionToUnlock(Integer customerId, Card card) {
        Transaction transaction = new Transaction(customerId, "[unblocking]", "[unblocking]",
                card.getBalance(), Currency.RUB, TransactionType.UNLOCKINGCARD, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    private void transactionToBlock(Integer customerId, Card card) {
        Transaction transaction = new Transaction(customerId, "[blocking]", "[blocking]",
                card.getBalance(), Currency.RUB, TransactionType.BLOCKINGCARD, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }

    private void transactionToOpenCard(Customer customer) {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[discovery]", "[discovery]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.OPENCARD, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }
}