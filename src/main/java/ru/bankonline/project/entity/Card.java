package ru.bankonline.project.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.bankonline.project.entity.enums.Currency;
import ru.bankonline.project.entity.enums.Status;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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

    public Card(Integer customerId, String cardNumber, String cvv, String accountNumber, BigDecimal balance,
                Currency currency, Status status, LocalDateTime openingDate, LocalDateTime updateDate) {
        this.customerId = customerId;
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.currency = currency;
        this.status = status;
        this.openingDate = openingDate;
        this.updateDate = updateDate;
    }
}