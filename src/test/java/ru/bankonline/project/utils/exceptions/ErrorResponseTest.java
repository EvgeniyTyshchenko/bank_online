package ru.bankonline.project.utils.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import ru.bankonline.project.dto.DTO;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.bankonline.project.utils.exceptions.ErrorResponse.*;

@Slf4j
class ErrorResponseTest {

    private static BindingResult bindingResult;
    private static Validator validator;
    private static DTO dto;
    private static List<FieldError> fieldErrors;

    @BeforeAll
    static void setUp() {
        bindingResult = mock(BindingResult.class);
        validator = mock(Validator.class);
        dto = mock(DTO.class);
        fieldErrors = new ArrayList<>();
    }

    @Test
    void shouldCheckForErrorsInTheCreationOfTheErrorIs() {
        when(bindingResult.hasErrors()).thenReturn(true);

        try {
            checkIfThereErrorInTheCreation(bindingResult, validator, dto);
            fail("Ожидается выброс NotCreatedException");
        } catch (NotCreatedException e) {
            log.info("Обработка исключения NotCreatedException");
        }
    }

    @Test
    void shouldCheckIfThereAreAnyErrorsInTheUpdateThereAreNoErrors() {
        when(bindingResult.hasErrors()).thenReturn(false);
        checkIfThereErrorInTheUpdate(bindingResult, validator, dto);
        assertFalse(bindingResult.hasErrors());
    }

    @Test
    void shouldCheckIfThereAreErrorsInTheUpdateThereAreErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        try {
            checkIfThereErrorInTheUpdate(bindingResult, validator, dto);
            fail("Ожидается выброс NotUpdatedException");
        } catch (NotUpdatedException e) {
            assertTrue(bindingResult.hasErrors());
            log.info("Обработка исключения NotUpdatedException");
        }
    }

    @Test
    void shouldCheckForReceivingAnErrorMessageThereAreErrors() {
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        FieldError error = new FieldError("objectName", "fieldName", "error message");
        fieldErrors.add(error);
        String errorMessage = getErrorMessage(bindingResult);
        assertEquals("error message", errorMessage);
    }
}