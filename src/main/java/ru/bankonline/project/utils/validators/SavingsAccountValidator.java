package ru.bankonline.project.utils.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.bankonline.project.dto.SavingsAccountDTO;
import ru.bankonline.project.entity.SavingsAccount;

import java.math.BigDecimal;

@Component
public class SavingsAccountValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return SavingsAccount.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SavingsAccountDTO savingsAccountDTO = (SavingsAccountDTO) target;
        if (savingsAccountDTO.getAccountNumber() == null || savingsAccountDTO.getAccountNumber().length() != 20) {
            errors.rejectValue("accountNumber", "", "Номер счета не может быть пустым " +
                    "и должен соответствовать 20 символам!");
        }
        if (savingsAccountDTO.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            errors.rejectValue("balance", "", "Баланс не может быть отрицательным!");
        }
    }
}
