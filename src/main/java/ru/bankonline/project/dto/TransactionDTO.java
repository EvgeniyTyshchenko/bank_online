package ru.bankonline.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import ru.bankonline.project.entity.enums.Currency;
import ru.bankonline.project.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionDTO extends DTO {

    private String sendersAccountNumber;
    private String recipientAccountNumber;
    private BigDecimal amount;
    private Currency currency;
    private TransactionType transactionType;
    private Date dateTimeTransaction;


    @Override
    public String toString() {
        return "TransactionDTO{" +
                "sendersAccountNumber='" + sendersAccountNumber + '\'' +
                ", recipientAccountNumber='" + recipientAccountNumber + '\'' +
                ", amount=" + amount +
                ", currency=" + currency +
                ", transactionType=" + transactionType +
                ", dateTimeTransaction=" + dateTimeTransaction +
                '}';
    }
}