package ru.bankonline.project.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.constants.Status;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/***
 * Класс, представляющий сущность "Сберегательный счет"
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "savings_accounts")
public class SavingsAccount {

    @Id
    @Column(name = "account_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer accountId;

    @Column(name = "customer_id")
    private Integer customerId;

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
     * Конструктор для создания объекта SavingsAccount
     * @param customerId ID клиента
     * @param accountNumber номер счета
     * @param balance баланс
     * @param currency валюта
     * @param status статус
     * @param openingDate дата открытия
     * @param updateDate дата обновления
     */
    public SavingsAccount(Integer customerId, String accountNumber, BigDecimal balance, Currency currency,
                          Status status, LocalDateTime openingDate, LocalDateTime updateDate) {
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.currency = currency;
        this.status = status;
        this.openingDate = openingDate;
        this.updateDate = updateDate;
    }
}