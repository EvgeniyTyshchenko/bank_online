package ru.bankonline.project.utils.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.bankonline.project.dto.ContactDTO;
import ru.bankonline.project.entity.Contact;

@Component
public class ContactValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Contact.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ContactDTO contactDTO = (ContactDTO) target;
        if (contactDTO.getPhoneNumber() == null || contactDTO.getPhoneNumber().isEmpty()) {
            errors.rejectValue("phoneNumber", "",
                    "Номер телефона не может быть null или пустым!");
        } else if (contactDTO.getPhoneNumber().matches("(\\+7|8)?\\d{10}")) {
            errors.rejectValue("phoneNumber", "",
                    "Некорректный формат номера телефона! " +
                            "Должно соответствовать формату: +79874563212 или 89786542321");
        }

        if (contactDTO.getEmail() == null || contactDTO.getEmail().isEmpty()) {
            errors.rejectValue("email", "", "Email не может быть null или пустым!");
        } else if (!contactDTO.getEmail().matches("^[A-Za-z0-9._]+@[A-Za-z0-9.]+\\.[A-Za-z]{2,6}$")) {
            errors.rejectValue("email", "", "Некорректный формат email!");
        }
    }
}