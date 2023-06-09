package ru.bankonline.project.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.constants.Status;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/***
 * Класс, представляющий сущность "Карта"
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "cards")
public class Card {

    @Id
    @Column(name = "card_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cardId;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "cvv")
    private String cvv;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "opening_date_time",
            columnDefinition = "TIMESTAMP")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime openingDate;

    @Column(name = "update_date_time",
            columnDefinition = "TIMESTAMP")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime updateDate;

    /***
     * Конструктор для создания объекта Card
     * @param customerId ID клиента
     * @param cardNumber номер карты
     * @param cvv CVV
     * @param accountNumber номер счета
     * @param balance баланс
     * @param currency валюта
     */
    public Card(Integer customerId, String cardNumber, String cvv,
                String accountNumber, BigDecimal balance, Currency currency) {
        this.customerId = customerId;
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.currency = currency;
        this.status = Status.ACTIVE;
        this.openingDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
    }
}