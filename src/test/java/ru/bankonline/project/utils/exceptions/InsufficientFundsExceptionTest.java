package ru.bankonline.project.utils.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsufficientFundsExceptionTest {

    @Test
    void testInsufficientFundsException() {
        String message = "Недостаточно средств";
        InsufficientFundsException exception = new InsufficientFundsException(message);
        assertEquals(message, exception.getMessage());
    }
}
