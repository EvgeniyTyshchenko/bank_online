package ru.bankonline.project.utils.validators;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import ru.bankonline.project.dto.AddressDTO;
import ru.bankonline.project.dto.ContactDTO;
import ru.bankonline.project.dto.CustomerDTO;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FullCustomerValidatorTest {

    private static final AddressValidator addressValidator = new AddressValidator();
    private static final ContactValidator contactValidator = new ContactValidator();
    private static final CustomerValidator customerValidator = new CustomerValidator();
    private static FullCustomerValidator fullCustomerValidator;
    private static CustomerDTO customerDTO;
    private static Errors errors;

    @BeforeAll
    static void setUp() {
        fullCustomerValidator = new FullCustomerValidator(addressValidator,
                contactValidator, customerValidator);
        customerDTO = new CustomerDTO(7845, 965520, "Водолажский", "Дмитрий", "Евгеньевич", "12.07.1981",
                new AddressDTO("Россия", "Москва", "Советская", "15/1", 13),
                new ContactDTO("89051254585", "dmitriyv@yandex.ru"));
        errors = new BeanPropertyBindingResult(customerDTO, "customerDTO");
    }

    @Test
    void shouldReturnAnErrorForInvalidCustomerDTO() {
        customerDTO.setPatronymic("");
        fullCustomerValidator.validate(customerDTO, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    void shouldReturnAnErrorForInvalidAddressDTO() {
        customerDTO.getAddressDTO().setCountry(null);
        fullCustomerValidator.validate(customerDTO, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    void shouldReturnAnErrorForInvalidContactDTO() {
        customerDTO.getContactDTO().setPhoneNumber("abc8988");
        fullCustomerValidator.validate(customerDTO, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    void shouldReturnTrueIfTheClassCustomerDTO() {
        FullCustomerValidator fullCustomerValidator = new FullCustomerValidator(addressValidator,
                contactValidator, customerValidator);
        boolean result = fullCustomerValidator.supports(customerDTO.getClass());
        assertTrue(result);
    }
}