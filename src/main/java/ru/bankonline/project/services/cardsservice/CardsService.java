package ru.bankonline.project.services.cardsservice;

import ru.bankonline.project.entity.Card;
import ru.bankonline.project.entity.Customer;

import javax.mail.MessagingException;
import java.math.BigDecimal;

public interface CardsService {

    void openCardToTheCustomer(Integer passportSeries, Integer passportNumber) throws MessagingException;

    void closeCard(Integer passportSeries, Integer passportNumber, String cardNumber) throws MessagingException;

    void blockCard(Integer passportSeries, Integer passportNumber, String cardNumber);

    void unlockCard(Integer passportSeries, Integer passportNumber, String cardNumber);

    String checkBalance(Integer passportSeries, Integer passportNumber, String cardNumber);

    void transferBetweenCards(Integer passportSeries, Integer passportNumber,
                              String senderCardNumber, String recipientCardNumber, BigDecimal amount);

    Card getCardDetails(Integer passportSeries, Integer passportNumber, String cardNumber);

    Card checkCardExists(Customer customer, String cardNumber);

    void checkIfTheCardIsNotClosedOrBlocked(Card card);
}