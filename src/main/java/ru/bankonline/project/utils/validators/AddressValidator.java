package ru.bankonline.project.utils.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.bankonline.project.dto.AddressDTO;
import ru.bankonline.project.entity.Address;

@Component
public class AddressValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Address.class.equals(clazz);
    }

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
        if (addressDTO.getApartment() == null || addressDTO.getApartment().toString().length() > 10) {
            errors.rejectValue("apartment", "",
                    "Номер квартиры не должен быть пустым и превышать 10 символов!");
        }
    }
}
