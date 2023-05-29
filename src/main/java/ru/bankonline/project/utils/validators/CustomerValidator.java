package ru.bankonline.project.utils.validators;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.bankonline.project.dto.CustomerDTO;

/***
 * Класс CustomerValidator предназначен для проверки корректности заполнения полей CustomerDTO
 * (паспортные данные, ФИО, дата рождения)
 */
@Component
public class CustomerValidator implements Validator {

    /***
     * Проверяет, поддерживает ли данный класс валидацию объектов
     * указанного класса
     * @param clazz класс объекта, который необходимо проверить
     * @return true, если класс поддерживается, иначе - false
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return CustomerDTO.class.equals(clazz);
    }

    /***
     * Проверяет корректность заполнения полей CustomerDTO и добавляет ошибки в объект класса Errors
     * @param target объект, который необходимо проверить
     * @param errors объект класса Errors для добавления ошибок
     */
    @Override
    public void validate(Object target, Errors errors) {
        CustomerDTO customerDTO = (CustomerDTO) target;
        if (customerDTO.getPassportSeries() == null || customerDTO.getPassportSeries().toString().length() != 4) {
            errors.rejectValue("passportSeries", "", "Серия паспорта не может быть null " +
                    "и должна соответствовать 4 символам!");
        }

        if (customerDTO.getPassportNumber() == null || customerDTO.getPassportNumber().toString().length() != 6) {
            errors.rejectValue("passportNumber", "", "Номер паспорта не может быть null " +
                    "и должен соответствовать 6 символам!");
        }

        if (customerDTO.getLastName() == null || StringUtils.isEmpty(customerDTO.getLastName())) {
            errors.rejectValue("lastName", "", "Фамилия обязательна для заполнения!");
        }

        if (customerDTO.getFirstName() == null || StringUtils.isEmpty(customerDTO.getFirstName())) {
            errors.rejectValue("firstName", "", "Имя обязательно для заполнения!");
        }

        if (customerDTO.getPatronymic() == null || StringUtils.isEmpty(customerDTO.getPatronymic())) {
            errors.rejectValue("patronymic", "", "Отчество обязательно для заполнения!");
        }

        if (customerDTO.getBirthday() == null || StringUtils.isEmpty(customerDTO.getBirthday())) {
            errors.rejectValue("birthday", "", "Дата дня рождения обязательна для заполнения!");
        } else if (!customerDTO.getBirthday().matches("^\\d{2}\\.\\d{2}\\.\\d{4}$")) {
            errors.rejectValue("birthday", "", "Некорректный формат даты! " +
                    "Пример ввода: 01.01.2000");
        }
    }
}