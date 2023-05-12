package ru.bankonline.project.utils.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PassportDuplicateExceptionTest {

    @Test
    void testPassportDuplicateException() {
        String message = "Дубликаты паспортов";
        PassportDuplicateException exception = new PassportDuplicateException(message);
        assertEquals(message, exception.getMessage());
    }
}
