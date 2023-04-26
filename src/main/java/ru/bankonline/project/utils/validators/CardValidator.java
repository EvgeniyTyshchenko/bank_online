package ru.bankonline.project.utils.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.bankonline.project.dto.CardDTO;
import ru.bankonline.project.entity.Card;

import java.math.BigDecimal;

@Component
public class CardValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Card.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CardDTO cardDTO = (CardDTO) target;
        if (cardDTO.getCardNumber() == null || cardDTO.getCardNumber().length() != 16 ) {
            errors.rejectValue("cardNumber", "", "Номер карты не может быть пустым " +
                    "и должен соответствовать 16 символам!");
        }
//        if (cardDTO.getCvv() == null || cardDTO.getCvv().length() != 3) {
//            errors.rejectValue("cvv", "", "CVV не может быть пустым " +
//                    "и должен соответствовать 3 символам!");
//        }
//        if (cardDTO.getAccountNumber() == null || cardDTO.getAccountNumber().length() != 20) {
//            errors.rejectValue("accountNumber", "", "Номер счета не может быть пустым " +
//                    "и должен соответствовать 20 символам!");
//        }
//        if (cardDTO.getBalance().compareTo(BigDecimal.ZERO) < 0) {
//            errors.rejectValue("balance", "", "Баланс не может быть отрицательным!");
//        }
    }
}