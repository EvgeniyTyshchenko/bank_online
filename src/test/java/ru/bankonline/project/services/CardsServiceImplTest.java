package ru.bankonline.project.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.constants.Status;
import ru.bankonline.project.entity.*;
import ru.bankonline.project.repositories.CardsRepository;
import ru.bankonline.project.repositories.TransactionsRepository;
import ru.bankonline.project.services.cardsservice.CardsServiceImpl;
import ru.bankonline.project.services.customersservice.CustomersService;
import ru.bankonline.project.utils.exceptions.ClosingCardException;
import ru.bankonline.project.utils.exceptions.EnteringCardDataException;
import ru.bankonline.project.utils.exceptions.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CardsServiceImplTest {

    @Mock
    private CardsRepository cardsRepository;
    @Mock
    private TransactionsRepository transactionsRepository;
    @Mock
    private CustomersService customersService;
    @Mock
    private MailSender mailSender;
    @InjectMocks
    private CardsServiceImpl cardsService;
    private static Customer customer;
    private static List<Card> cards;
    private static Customer newCustomer;

    @BeforeAll
    static void setUp() {
        customer = new Customer(8825, 902521, "Медведев", "Максим", "Викторович", "07.09.1970",
                new Address("Россия", "Москва", "Зеленая", "29/Б", 85),
                new Contact("89051237812", "medvedevmv@mail.ru"));

        Card card = new Card(customer.getCustomerId(), "4500450078960561", "104", "96122700003356951256",
                BigDecimal.valueOf(27_000), Currency.RUB);
        cards = new ArrayList<>(List.of(card));
        customer.setCards(cards);

        newCustomer = new Customer();
        newCustomer.setPassportSeries(7702);
        newCustomer.setPassportNumber(159994);
        newCustomer.setLastName("Романов");
        newCustomer.setFirstName("Валентин");
        newCustomer.setPatronymic("Сергеевич");
        newCustomer.setBirthday("17.01.1969");

        Card newCard = new Card(newCustomer.getCustomerId(), "5757000045632589", "885", "55521003688526544152",
                BigDecimal.valueOf(12_000), Currency.RUB);
        List<Card> newCards = new ArrayList<>(List.of(newCard));
        newCustomer.setCards(newCards);
    }

    @Test
    void shouldOpenNewCard() {
        when(customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(customer);

        cardsService.openCardToTheCustomer(customer.getPassportSeries(), customer.getPassportNumber());

        verify(customersService, times(1)).checkIfTheCustomerIsBlockedOrDeleted(customer);
        verify(cardsRepository, times(1)).save(any(Card.class));
        log.info("Открытие карты");
    }

    @Test
    void shouldGetCard() {
        Card resultCard = cardsService.checkCardExists(customer, customer.getCards().get(0).getCardNumber());
        Assertions.assertEquals(customer.getCards().get(0), resultCard);
        log.info(resultCard.getCardNumber());
    }

    @Test
    void shouldBeAnExceptionDueToTheAbsenceOfThisCardFromTheCustomer() {
        String cardNumber = newCustomer.getCards().get(0).getCardNumber();
        EnteringCardDataException exception = Assertions.assertThrows(EnteringCardDataException.class, () -> {
            cardsService.checkCardExists(customer, cardNumber);
        });
        Assertions.assertEquals("Номер карты, который вы вводите отсутствует у клиента "
                + customer.getLastName() + " " + customer.getFirstName() + " " + customer.getPatronymic()
                + " Проверьте реквизиты карты и попробуйте снова.", exception.getMessage());
    }

    @Test
    void shouldBeAnExceptionWhenTheCardIsClosed() {
        cards.get(0).setStatus(Status.CLOSED);
        Card cardCustomer = customer.getCards().get(0);

        Assertions.assertThrows(ClosingCardException.class,
                () -> cardsService.checkIfTheCardIsNotClosedOrBlocked(cardCustomer));
    }

    @Test
    void shouldBeAnExceptionWhenTheCardIsBlocked() {
        cards.get(0).setStatus(Status.BLOCKED);
        Card cardCustomer = customer.getCards().get(0);

        Assertions.assertThrows(ClosingCardException.class,
                () -> cardsService.checkIfTheCardIsNotClosedOrBlocked(cardCustomer));
    }

    @Test
    void shouldSuccessfullyCloseTheCard() {
        customer.getCards().get(0).setStatus(Status.ACTIVE);
        customer.getCards().get(0).setBalance(BigDecimal.ZERO);
        when(customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(customer);

        cardsService.closeCard(customer.getPassportSeries(), customer.getPassportNumber(),
                customer.getCards().get(0).getCardNumber());
        log.info("Закрытие карты");
    }

    @Test
    void shouldSuccessfullyBlockTheCard() {
        customer.getCards().get(0).setStatus(Status.ACTIVE);
        when(customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(customer);

        cardsService.blockCard(customer.getPassportSeries(), customer.getPassportNumber(),
                customer.getCards().get(0).getCardNumber());
    }

    @Test
    void shouldSuccessfullyUnlockTheCard() {
        customer.getCards().get(0).setStatus(Status.BLOCKED);
        when(customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(customer);

        cardsService.unlockCard(customer.getPassportSeries(), customer.getPassportNumber(),
                customer.getCards().get(0).getCardNumber());
    }

    @Test
    void shouldSuccessfullyCheckTheBalance() {
        customer.getCards().get(0).setStatus(Status.ACTIVE);
        when(customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(customer);

        cardsService.checkBalance(customer.getPassportSeries(), customer.getPassportNumber(),
                customer.getCards().get(0).getCardNumber());
        log.info("Проверка баланса карты");
    }

    @Test
    void shouldBeSuccessfulTransferBetweenTheCards() {
        when(customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(customer);
        when(customersService.getCustomerByCardNumber(newCustomer.getCards().get(0).getCardNumber()))
                .thenReturn(newCustomer);
        customer.getCards().get(0).setStatus(Status.ACTIVE);

        cardsService.transferBetweenCards(customer.getPassportSeries(), customer.getPassportNumber(),
                customer.getCards().get(0).getCardNumber(), newCustomer.getCards().get(0).getCardNumber(),
                new BigDecimal(1_000));
        log.info("Перевод денежных средств между картами");
    }

    @Test
    void shouldBeAnExceptionInCaseOfInsufficientFundsDuringTheTransfer() {
        when(customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(customer);
        when(customersService.getCustomerByCardNumber(newCustomer.getCards().get(0).getCardNumber()))
                .thenReturn(newCustomer);

        String cardNumberCustomer = customer.getCards().get(0).getCardNumber();
        String cardNumberNewCustomer = newCustomer.getCards().get(0).getCardNumber();
        BigDecimal balanceCustomerMoreThanAcceptable = customer.getCards().get(0).getBalance().add(BigDecimal.valueOf(1_000));

        Assertions.assertThrows(InsufficientFundsException.class,
                () -> cardsService.transferBetweenCards(customer.getPassportSeries(), customer.getPassportNumber(),
                        cardNumberCustomer, cardNumberNewCustomer, balanceCustomerMoreThanAcceptable));
    }

    @Test
    void shouldGetInformationOnTheCard() {
        when(customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(), customer.getPassportNumber()))
                .thenReturn(customer);

        cardsService.getCardDetails(customer.getPassportSeries(), customer.getPassportNumber(),
                customer.getCards().get(0).getCardNumber());
    }
}