package ru.bankonline.project.services.cardsservice;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
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
import ru.bankonline.project.services.MailSender;
import ru.bankonline.project.services.customersservice.CustomersService;
import ru.bankonline.project.services.savingsaccountsservice.SavingsAccountsService;
import ru.bankonline.project.services.transactionsservice.TransactionsService;
import ru.bankonline.project.utils.exceptions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CardsServiceImpl implements CardsService {

    private final CardsRepository cardsRepository;
    private final CustomersService customersService;
    private final TransactionsService transactionsService;
    private final SavingsAccountsService savingsAccountsService;
    private final MailSender mailSender;

    @Autowired
    public CardsServiceImpl(CardsRepository cardsRepository, CustomersService customersService,
                            TransactionsService transactionsService, SavingsAccountsService savingsAccountsService, MailSender mailSender) {
        this.cardsRepository = cardsRepository;
        this.customersService = customersService;
        this.transactionsService = transactionsService;
        this.savingsAccountsService = savingsAccountsService;
        this.mailSender = mailSender;
    }

    @Override
    @Transactional
    public void openCardToTheCustomer(Integer passportSeries, Integer passportNumber) {
        Customer existingCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(existingCustomer);

        String uniqueCardNumber = UUID.randomUUID().toString().replaceAll("\\D", "0").substring(0, 16);
        String uniqueCVV = createRandomThreeDigitNumber();
        String uniqueAccountNumber = UUID.randomUUID().toString().replaceAll("\\D", "0").substring(0, 20);

        Card card = new Card(existingCustomer.getCustomerId(), uniqueCardNumber,
                uniqueCVV, uniqueAccountNumber, BigDecimal.valueOf(0), Currency.RUB);

        cardsRepository.save(card);
        transactionToOpenCard(existingCustomer);

        String message = "Здравствуйте, " + existingCustomer.getFirstName() + " " + existingCustomer.getPatronymic() + "! \n" +
                "Ваша заявка принята. \n" +
                "Карта будет готова в течении 5 рабочих дней. По готовности с Вами свяжется менеджер банка. \n" +
                "Данные Вашей карты: \n" +
                "Номер карты: " + uniqueCardNumber + "\n"
                + "CVV: " + uniqueCVV + "\n"
                + "Номер счета: " + uniqueAccountNumber + "\n"
                + "Активировать карту и задать PIN-код Вам поможет менеджер банка при получении. \n" +
                "Спасибо, что пользуетесь услугами банка!";
        mailSender.sendEmail(existingCustomer.getContactDetails().getEmail(), "Заявка на открытие карты", message);
        log.info("Открытие карты. Номер:" + card.getCardNumber());
    }

    @Override
    @Transactional
    public void closeCard(Integer passportSeries, Integer passportNumber, String cardNumber) {
        Customer existingCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(existingCustomer);

        Card card = checkCardExists(existingCustomer, cardNumber);
        checkIfTheCardIsNotClosedOrBlocked(card);

        checkCardBalance(card);
        enrichCardForClosure(card);
        transactionToClose(existingCustomer.getCustomerId());

        String message = "Здравствуйте, " + existingCustomer.getFirstName() + " " + existingCustomer.getPatronymic() + "! \n" +
                "Закрытие карты произведено успешно! \n" +
                "Данные карты, по которой произведено закрытие: \n" +
                "Номер карты: " + card.getCardNumber() + "\n"
                + "CVV: " + card.getCvv() + "\n"
                + "Номер счета: " + card.getAccountNumber() + "\n";
        mailSender.sendEmail(existingCustomer.getContactDetails().getEmail(), "Закрытие карты", message);
        log.info("Закрытие карты. Номер:" + card.getCardNumber());
    }

    @Override
    @Transactional
    public void blockCard(Integer passportSeries, Integer passportNumber, String cardNumber) {
        Customer existingCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(existingCustomer);

        Card card = checkCardExists(existingCustomer, cardNumber);
        checkIfTheCardIsNotClosedOrBlocked(card);
        enrichCardForBlocking(card);
        transactionToBlock(existingCustomer.getCustomerId(), card);
        log.info("Блокировка карты. Номер:" + card.getCardNumber());
    }

    @Override
    @Transactional
    public void unlockCard(Integer passportSeries, Integer passportNumber, String cardNumber) {
        Customer existingCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(existingCustomer);

        Card card = checkCardExists(existingCustomer, cardNumber);
        checkIfTheCardIsClosedOrActive(card);
        enrichCardForUnlock(card);
        transactionToUnlock(existingCustomer.getCustomerId(), card);
        log.info("Разблокировка карты. Номер:" + card.getCardNumber());
    }

    @Override
    @Transactional
    public String checkBalance(Integer passportSeries, Integer passportNumber, String cardNumber) {
        Customer existingCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(existingCustomer);

        Card card = checkCardExists(existingCustomer, cardNumber);
        checkIfTheCardIsNotClosedOrBlocked(card);
        transactionBalanceRequest(existingCustomer, card);
        log.info("Проверка баланса карты. Номер:" + card.getCardNumber());
        return String.format("Баланс: %.2f %s", card.getBalance(), card.getCurrency().toString());
    }

    @Override
    @Transactional
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

            log.info("Перевод между картами. Карта отправителя: " + senderCard.getCardNumber()
            + ", карта получателя: " + recipientCard.getCardNumber());
        } else {
            throw new InsufficientFundsException("Недостаточно денежных средств для совершения транзакции!");
        }
    }

    @Override
    @Transactional
    public void transferFromCardToSavingsAccount(Integer passportSeries, Integer passportNumber,
                                                 String senderCardNumber, String recipientSavingsAccountNumber, BigDecimal amount) {
        Customer senderCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(senderCustomer);

        Card senderCard = checkCardExists(senderCustomer, senderCardNumber);
        checkIfTheCardIsNotClosedOrBlocked(senderCard);

        Customer recipientCustomer = customersService.getCustomerBySavingAccountNumber(recipientSavingsAccountNumber);
        SavingsAccount recipientAccountExisting = savingsAccountsService.checkSavingAccountExists(recipientCustomer, recipientSavingsAccountNumber);
        savingsAccountsService.checkIfTheSavingAccountIsNotClosedOrBlocked(recipientAccountExisting);
        savingsAccountsService.checkIfThereIsMoneyOnTheSavingAccount(recipientAccountExisting);

        if (senderCard.getBalance().compareTo(amount) >= 0) {
            senderCard.setBalance(senderCard.getBalance().subtract(amount));
            moneySendingToTheAccountTransaction(senderCustomer, senderCard, recipientAccountExisting, amount);

            recipientAccountExisting.setBalance(recipientAccountExisting.getBalance().add(amount));
            moneyReceiptToTheAccountTransaction(recipientCustomer, senderCard, recipientAccountExisting, amount);
            cardsRepository.save(senderCard);
            savingsAccountsService.saveRepositorySavingsAccounts(recipientAccountExisting);
            log.info("Перевод денежных средств с карты на сберегательный счет. Номер счета карты: "
                    + senderCard.getAccountNumber() + ", номер сберегательного счета: " + recipientAccountExisting.getAccountNumber());
        } else {
            throw new InsufficientFundsException("Недостаточно денежных средств! "
                    + "Пожалуйста, проверьте баланс на карте " + senderCard.getCardNumber()
                    + " и попробуйте снова.");
        }
    }

    @Override
    public Card getCardDetails(Integer passportSeries, Integer passportNumber, String cardNumber) {
        Customer existingCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(existingCustomer);

        log.info("Запрос информации по карте: " + cardNumber);
        return checkCardExists(existingCustomer, cardNumber);
    }

    @Override
    public void saveCardsRepository(Card card) {
        cardsRepository.save(card);
    }

    @Override
    public List<Card> findByCustomerIdToCardsRepository(Integer customerId) {
        return cardsRepository.findByCustomerId(customerId);
    }

    private Card checkCardExists(Customer customer, String cardNumber) {
        for (Card card : customer.getCards()) {
            if (card.getCardNumber().equals(cardNumber)) {
                return card;
            }
        }
        throw new EnteringCardDataException("Номер карты, который вы вводите отсутствует у клиента "
                + customer.getLastName() + " " + customer.getFirstName() + " " + customer.getPatronymic()
                + " Проверьте реквизиты карты и попробуйте снова.");
    }

    private void checkIfTheCardIsNotClosedOrBlocked(Card card) {
        if (card.getStatus() == Status.CLOSED || card.getStatus() == Status.BLOCKED) {
            throw new ClosingCardException("Данная карта закрыта или заблокирована! " +
                    "Убедитесь, что Вы ввели правильные реквизиты карты!");
        }
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

    private void moneySendingToTheCardTransaction(Customer senderCustomer, Card senderCard, Card recipientCard, BigDecimal amount) {
        Transaction transaction = new Transaction(senderCustomer.getCustomerId(), senderCard.getAccountNumber(), recipientCard.getAccountNumber(),
                amount, senderCard.getCurrency(), TransactionType.OUTTRANSFER, LocalDateTime.now());
        transactionsService.saveTransactionsRepository(transaction);
    }

    private void moneyReceiptToTheCardTransaction(Customer recipientCustomer, Card senderCard, Card recipientCard, BigDecimal amount) {
        Transaction transaction = new Transaction(recipientCustomer.getCustomerId(), senderCard.getAccountNumber(), recipientCard.getAccountNumber(),
                amount, senderCard.getCurrency(), TransactionType.INTRANSFER, LocalDateTime.now());
        transactionsService.saveTransactionsRepository(transaction);
    }

    private void enrichCardForUnlock(Card card) {
        card.setStatus(Status.ACTIVE);
        card.setUpdateDate(LocalDateTime.now());
        cardsRepository.save(card);
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
        transactionsService.saveTransactionsRepository(transaction);
    }

    private void transactionToUnlock(Integer customerId, Card card) {
        Transaction transaction = new Transaction(customerId, "[unblocking]", "[unblocking]",
                card.getBalance(), Currency.RUB, TransactionType.UNLOCKINGCARD, LocalDateTime.now());
        transactionsService.saveTransactionsRepository(transaction);
    }

    private void transactionToBlock(Integer customerId, Card card) {
        Transaction transaction = new Transaction(customerId, "[blocking]", "[blocking]",
                card.getBalance(), Currency.RUB, TransactionType.BLOCKINGCARD, LocalDateTime.now());
        transactionsService.saveTransactionsRepository(transaction);
    }

    private void transactionToOpenCard(Customer customer) {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[discovery]", "[discovery]",
                BigDecimal.valueOf(0), Currency.RUB, TransactionType.OPENCARD, LocalDateTime.now());
        transactionsService.saveTransactionsRepository(transaction);
    }

    private void transactionBalanceRequest(Customer customer, Card card) {
        Transaction transaction = new Transaction(customer.getCustomerId(), "[card balance request]", "[card balance request]",
                card.getBalance(), Currency.RUB, TransactionType.CHECKBALANCE, LocalDateTime.now());
        transactionsService.saveTransactionsRepository(transaction);
    }

    private void moneySendingToTheAccountTransaction(Customer senderCustomer, Card senderCard, SavingsAccount recipientAccount, BigDecimal amount) {
        Transaction transaction = new Transaction(senderCustomer.getCustomerId(), senderCard.getAccountNumber(), recipientAccount.getAccountNumber(),
                amount, senderCard.getCurrency(), TransactionType.OUTTRANSFER, LocalDateTime.now());
        transactionsService.saveTransactionsRepository(transaction);
    }

    private void moneyReceiptToTheAccountTransaction(Customer recipientCustomer, Card senderCard, SavingsAccount recipientAccount, BigDecimal amount) {
        Transaction transaction = new Transaction(recipientCustomer.getCustomerId(), senderCard.getAccountNumber(), recipientAccount.getAccountNumber(),
                amount, senderCard.getCurrency(), TransactionType.INTRANSFER, LocalDateTime.now());
        transactionsService.saveTransactionsRepository(transaction);
    }

    private String createRandomThreeDigitNumber() {
        int leftLimit = 100;
        int rightLimit = 999;
        RandomDataGenerator randomDataGenerator = new RandomDataGenerator();
        return String.valueOf(randomDataGenerator.nextInt(leftLimit, rightLimit));
    }
}