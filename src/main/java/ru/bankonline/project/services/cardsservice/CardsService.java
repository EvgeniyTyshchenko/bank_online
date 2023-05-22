package ru.bankonline.project.services.cardsservice;

import org.springframework.transaction.annotation.Transactional;
import ru.bankonline.project.entity.Card;
import ru.bankonline.project.entity.Customer;

import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.util.List;

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

    void saveCardsRepository(Card card);

    List<Card> findByCustomerIdToCardsRepository(Integer customerId);
}