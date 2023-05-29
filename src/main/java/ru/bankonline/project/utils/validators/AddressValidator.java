package ru.bankonline.project.utils.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.bankonline.project.dto.AddressDTO;

/***
 * Класс AddressValidator предназначен для проверки корректности заполнения полей AddressDTO
 */
@Component
public class AddressValidator implements Validator {

    /***
     * Проверяет, поддерживает ли данный класс валидацию объектов
     * указанного класса
     * @param clazz класс объекта, который необходимо проверить
     * @return true, если класс поддерживается, иначе - false
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return AddressDTO.class.equals(clazz);
    }

    /***
     * Проверяет корректность заполнения полей AddressDTO и добавляет ошибки в объект класса Errors
     * @param target объект, который необходимо проверить
     * @param errors объект класса Errors для добавления ошибок
     */
    @Override
    public void validate(Object target, Errors errors) {
        AddressDTO addressDTO = (AddressDTO) target;

        if (addressDTO.getCountry() == null || addressDTO.getCountry().isEmpty()) {
            errors.rejectValue("country", "", "Название страны не должно быть пустым!");
        }

        if (addressDTO.getCity() == null || addressDTO.getCity().isEmpty()) {
            errors.rejectValue("city", "", "Название города не должно быть пустым!");
        }

        if (addressDTO.getStreet() == null || addressDTO.getStreet().isEmpty()) {
            errors.rejectValue("street", "", "Название улицы не должно быть пустым!");
        }

        if (addressDTO.getHouse() == null || addressDTO.getHouse().isEmpty()) {
            errors.rejectValue("house", "", "Номер дома не должен быть пустым!");
        }

        if (addressDTO.getApartment() == null || addressDTO.getApartment().toString().length() > 7) {
            errors.rejectValue("apartment", "",
                    "Номер квартиры не должен быть пустым и превышать 7 символов!");
        }
    }
}