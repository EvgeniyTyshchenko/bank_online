package ru.bankonline.project.services.cardsservice;

import ru.bankonline.project.entity.Card;

import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.util.List;

/***
 * Интерфейс CardsService предоставляет методы для работы с картами клиентов
 */
public interface CardsService {

    void openCardToTheCustomer(Integer passportSeries, Integer passportNumber) throws MessagingException;

    void closeCard(Integer passportSeries, Integer passportNumber, String cardNumber) throws MessagingException;

    void blockCard(Integer passportSeries, Integer passportNumber, String cardNumber);

    void unlockCard(Integer passportSeries, Integer passportNumber, String cardNumber);

    String checkBalance(Integer passportSeries, Integer passportNumber, String cardNumber);

    void transferBetweenCards(Integer passportSeries, Integer passportNumber,
                              String senderCardNumber, String recipientCardNumber, BigDecimal amount);

    void transferFromCardToSavingsAccount(Integer passportSeries, Integer passportNumber,
                                          String senderCardNumber, String recipientSavingsAccountNumber, BigDecimal amount);

    Card getCardDetails(Integer passportSeries, Integer passportNumber, String cardNumber);

    void closeAllCardsInTheList(List<Card> cards);

    List<Card> findByCustomerIdToCardsRepository(Integer customerId);
}