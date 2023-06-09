package ru.bankonline.project.utils.validators;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import ru.bankonline.project.dto.ContactDTO;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ContactValidatorTest {

    private static ContactValidator contactValidator;
    private static ContactDTO contactDTO;
    private static Errors errors;

    @BeforeAll
    static void setUp() {
        contactValidator = new ContactValidator();
        contactDTO = new ContactDTO("89881233232", "test@mail.ru");
        errors = new BeanPropertyBindingResult(contactDTO, "contactDTO");
    }

    @Test
    void shouldReturnAnErrorBecauseThePhoneNumberIsNullOrIsEmptyOrNotMatchTheFormat() {
        contactDTO.setPhoneNumber(null);
        contactValidator.validate(contactDTO, errors);
        assertTrue(errors.hasErrors());

        contactDTO.setPhoneNumber("");
        contactValidator.validate(contactDTO, errors);
        assertTrue(errors.hasErrors());

        contactDTO.setPhoneNumber("abc123");
        contactValidator.validate(contactDTO, errors);
        assertTrue(errors.hasErrors());
    }

    @Test
    void shouldReturnAnErrorBecauseTheEmailIsNullOrIsEmptyOrNotMatchTheFormat() {
        contactDTO.setEmail(null);
        contactValidator.validate(contactDTO, errors);
        assertTrue(errors.hasErrors());

        contactDTO.setEmail("");
        contactValidator.validate(contactDTO, errors);
        assertTrue(errors.hasErrors());

        contactDTO.setEmail("test123");
        contactValidator.validate(contactDTO, errors);
        assertTrue(errors.hasErrors());
    }

    @Test
    void shouldReturnTrueIfTheClassIsContactDTO() {
        ContactValidator contactValidator = new ContactValidator();
        boolean result = contactValidator.supports(ContactDTO.class);
        assertTrue(result);
    }
}