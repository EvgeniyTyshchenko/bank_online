package ru.bankonline.project.utils.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ru.bankonline.project.dto.CustomerDTO;

/***
 * Класс FullCustomerValidator предназначен для проверки корректности заполнения
 * CustomerDTO, AddressDTO, ContactDTO
 */
@Component
public class FullCustomerValidator implements Validator {
    private final AddressValidator addressValidator;
    private final ContactValidator contactValidator;
    private final CustomerValidator customerValidator;

    @Autowired
    public FullCustomerValidator(AddressValidator addressValidator, ContactValidator contactValidator,
                                 CustomerValidator customerValidator) {
        this.addressValidator = addressValidator;
        this.contactValidator = contactValidator;
        this.customerValidator = customerValidator;
    }

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
     * Проверяет валидность объекта CustomerDTO
     * @param target объект, который необходимо проверить
     * @param errors объект класса Errors для добавления ошибок
     * В методе происходит вызов валидаторов для объектов внутри CustomerDTO:
     * 1. customerValidator - для проверки самого объекта CustomerDTO;
     * 2. addressValidator - для проверки объекта AddressDTO, который находится
     * внутри CustomerDTO;
     * 3. contactValidator - для проверки объекта ContactDTO, который находится
     * внутри CustomerDTO
     *
     * Метод использует pushNestedPath и popNestedPath для того, чтобы указать,
     * какой именно объект был проверен в текущий момент (чтобы ошибки были правильно записаны
     * в объект Errors)
     */
    @Override
    public void validate(Object target, Errors errors) {
        CustomerDTO customerDTO = (CustomerDTO) target;

        customerValidator.validate(customerDTO, errors);

        errors.pushNestedPath("addressDTO");
        ValidationUtils.invokeValidator(this.addressValidator, customerDTO.getAddressDTO(), errors);
        errors.popNestedPath();

        errors.pushNestedPath("contactDTO");
        ValidationUtils.invokeValidator(this.contactValidator, customerDTO.getContactDTO(), errors);
        errors.popNestedPath();
    }
}