package ru.bankonline.project.utils.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.bankonline.project.dto.CustomerDTO;
import ru.bankonline.project.entity.Customer;

@Component
public class CustomerValidator implements Validator {
    private final AddressValidator addressValidator;
    private final ContactValidator contactValidator;

    @Autowired
    public CustomerValidator(AddressValidator addressValidator, ContactValidator contactValidator) {
        this.addressValidator = addressValidator;
        this.contactValidator = contactValidator;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Customer.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CustomerDTO customerDTO = (CustomerDTO) target;
        if (customerDTO.getPassportSeries() == null || customerDTO.getPassportSeries().toString().length() != 4) {
            errors.rejectValue("passportSeries", "", "Серия паспорта не может быть null " +
                    "и должна соответствовать 4 символам!");
        }
        if (customerDTO.getPassportSeries() == null || customerDTO.getPassportNumber().toString().length() != 6) {
            errors.rejectValue("passportNumber", "", "Номер паспорта не может быть null " +
                    "и должен соответствовать 6 символам!");
        }
        if (customerDTO.getLastName() == null || customerDTO.getLastName().isEmpty()) {
            errors.rejectValue("lastName", "", "Фамилия обязательна для заполнения!");
        }
        if (customerDTO.getFirstName() == null || customerDTO.getFirstName().isEmpty()) {
            errors.rejectValue("firstName", "", "Имя обязательно для заполнения!");
        }
        if (customerDTO.getPatronymic() == null || customerDTO.getPatronymic().isEmpty()) {
            errors.rejectValue("patronymic", "", "Отчество обязательно для заполнения!");
        }
        if (customerDTO.getPatronymic() == null || customerDTO.getBirthday().length() != 10) {
            errors.rejectValue("birthday", "", "Дата дня рождения обязательна для заполнения! " +
                    "Пример ввода: 01.01.2000");
        }
        addressValidator.validate(customerDTO.getAddressDTO(), errors);
        contactValidator.validate(customerDTO.getContactDTO(), errors);
    }
}