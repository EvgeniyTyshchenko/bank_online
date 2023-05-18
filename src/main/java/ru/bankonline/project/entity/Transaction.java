package ru.bankonline.project.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.constants.TransactionType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "senders_account_number")
    private String sendersAccountNumber;

    @Column(name = "recipient_account_number")
    private String recipientAccountNumber;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "date_time_transaction",
            columnDefinition = "TIMESTAMP")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime dateTimeTransaction;

    public Transaction(Integer customerId, String sendersAccountNumber, String recipientAccountNumber,
                       BigDecimal amount, Currency currency, TransactionType transactionType,
                       LocalDateTime dateTimeTransaction) {
        this.customerId = customerId;
        this.sendersAccountNumber = sendersAccountNumber;
        this.recipientAccountNumber = recipientAccountNumber;
        this.amount = amount;
        this.currency = currency;
        this.transactionType = transactionType;
        this.dateTimeTransaction = dateTimeTransaction;
    }
}