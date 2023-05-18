package ru.bankonline.project.utils.validators;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import ru.bankonline.project.dto.CustomerDTO;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerValidatorTest {

    private static CustomerValidator customerValidator;
    private static CustomerDTO customerDTO;
    private static Errors errors;

    @BeforeAll
    static void setUp() {
        customerValidator = new CustomerValidator();

        customerDTO = new CustomerDTO();
        customerDTO.setPassportSeries(6565);
        customerDTO.setPassportNumber(458565);
        customerDTO.setLastName("Симонов");
        customerDTO.setFirstName("Петр");
        customerDTO.setPatronymic("Валентинович");
        customerDTO.setBirthday("15.06.1961");

        errors = new BeanPropertyBindingResult(customerDTO, "customerDTO");
    }

    @Test
    void shouldBeErrorBecauseThePassportSeriesIsNull() {
        customerDTO.setPassportSeries(null);
        customerValidator.validate(customerDTO, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    void shouldReturnAnErrorBecauseThePassportSeriesShouldBeEqualToFourCharacters() {
        customerDTO.setPassportSeries(123);
        customerValidator.validate(customerDTO, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    void shouldBeErrorBecauseThePassportNumberIsNull() {
        customerDTO.setPassportNumber(null);
        customerValidator.validate(customerDTO, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    void shouldReturnAnErrorBecauseThePassportNumberShouldBeEqualToSixCharacters() {
        customerDTO.setPassportNumber(12345);
        customerValidator.validate(customerDTO, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    void shouldBeErrorBecauseTheLastNameIsNull() {
        customerDTO.setLastName(null);
        customerValidator.validate(customerDTO, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    void shouldBeErrorBecauseTheFirstNameIsNull() {
        customerDTO.setFirstName(null);
        customerValidator.validate(customerDTO, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    void shouldBeErrorBecauseThePatronymicIsNull() {
        customerDTO.setPatronymic(null);
        customerValidator.validate(customerDTO, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    void shouldBeErrorBecauseTheBirthdayIsNull() {
        customerDTO.setBirthday(null);
        customerValidator.validate(customerDTO, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    void shouldReturnAnErrorBecauseAnInvalidBirthdayFormatIsUsed() {
        customerDTO.setBirthday("01-01-1970");
        customerValidator.validate(customerDTO, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    void validCustomerDTOFormatShouldNotReturnErrors() {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setPassportSeries(1234);
        customerDTO.setPassportNumber(123456);
        customerDTO.setLastName("Иванов");
        customerDTO.setFirstName("Иван");
        customerDTO.setPatronymic("Иванович");
        customerDTO.setBirthday("01.01.1960");

        Errors newErrors = new BeanPropertyBindingResult(customerDTO, "customerDTO");
        customerValidator.validate(customerDTO, newErrors);

        assertFalse(newErrors.hasErrors());
    }

    @Test
    void shouldReturnTrueIfTheClassIsCustomerDTO() {
        CustomerValidator customerValidator = new CustomerValidator();
        boolean result = customerValidator.supports(customerDTO.getClass());
        assertTrue(result);
    }
}