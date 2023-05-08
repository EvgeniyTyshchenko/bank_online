package ru.bankonline.project.utils.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.bankonline.project.dto.TransactionDTO;
import ru.bankonline.project.entity.Transaction;

import java.math.BigDecimal;

@Component
public class TransactionValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Transaction.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        TransactionDTO transactionDTO = (TransactionDTO) target;
        if (transactionDTO.getSendersAccountNumber() == null || transactionDTO.getSendersAccountNumber().length() != 20) {
            errors.rejectValue("sendersAccountNumber", "", "Номер счета отправителя не может быть пустым " +
                    "и должен соответствовать 20 символам!");
        }

        if (transactionDTO.getRecipientAccountNumber() == null || transactionDTO.getRecipientAccountNumber().length() != 20) {
            errors.rejectValue("recipientAccountNumber", "", "Номер счета получателя не может быть пустым " +
                    "и должен соответствовать 20 символам!");
        }

        if (transactionDTO.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            errors.rejectValue("amount", "", "Сумма не может быть отрицательной!");
        }
    }
}
