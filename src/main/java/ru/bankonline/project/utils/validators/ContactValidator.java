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
        }
        if (contactDTO.getEmail() == null || contactDTO.getEmail().isEmpty()) {
            errors.rejectValue("email", "", "Email не может быть null или пустым!");
        }
    }
}
