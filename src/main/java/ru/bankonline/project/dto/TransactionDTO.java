package ru.bankonline.project.dto;

import lombok.*;

import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.constants.TransactionType;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TransactionDTO extends DTO {

    private String sendersAccountNumber;
    private String recipientAccountNumber;
    private BigDecimal amount;
    private Currency currency;
    private TransactionType transactionType;
    private Date dateTimeTransaction;
}