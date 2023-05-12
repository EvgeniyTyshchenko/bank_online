package ru.bankonline.project.utils.validators;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import ru.bankonline.project.dto.AddressDTO;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AddressValidatorTest {

    private static AddressValidator addressValidator;
    private static AddressDTO addressDTO;
    private static Errors errors;

    @BeforeAll
    static void setUp() {
        addressValidator = new AddressValidator();
        addressDTO = new AddressDTO("Россия", "Краснодар", "ул.Московская", "254/Д", 56);
        errors = new BeanPropertyBindingResult(addressDTO, "addressDTO");
    }

    @Test
    void shouldBeErrorBecauseTheCountryIsNull() {
        addressDTO.setCountry(null);
        addressValidator.validate(addressDTO, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    void shouldBeErrorBecauseTheCountryIsEmpty() {
        addressDTO.setCountry("");
        addressValidator.validate(addressDTO, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    void shouldBeErrorBecauseTheCityIsNull() {
        addressDTO.setCity(null);
        addressValidator.validate(addressDTO, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    void shouldBeErrorBecauseTheCityIsEmpty() {
        addressDTO.setCity("");
        addressValidator.validate(addressDTO, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    void shouldBeErrorBecauseTheStreetIsNull() {
        addressDTO.setStreet(null);
        addressValidator.validate(addressDTO, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    void shouldBeErrorBecauseTheStreetIsEmpty() {
        addressDTO.setStreet("");
        addressValidator.validate(addressDTO, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    void shouldBeErrorBecauseTheHouseIsNull() {
        addressDTO.setHouse(null);
        addressValidator.validate(addressDTO, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    void shouldBeErrorBecauseTheHouseIsEmpty() {
        addressDTO.setHouse("");
        addressValidator.validate(addressDTO, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    void shouldBeErrorBecauseTheApartmentIsNull() {
        addressDTO.setApartment(null);
        addressValidator.validate(addressDTO, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    void shouldBeErrorIfTheNumberOfCharactersIsMoreThanTenInTheApartmentNumber() {
        addressDTO.setApartment(12345678);
        addressValidator.validate(addressDTO, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    void shouldReturnTrueIfTheClassIsAddressDTO() {
        AddressValidator addressValidator = new AddressValidator();
        boolean result = addressValidator.supports(addressDTO.getClass());
        assertTrue(result);
    }
}