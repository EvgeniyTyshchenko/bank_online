package ru.bankonline.project.utils.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClosingSavingsAccountExceptionTest {

    @Test
    void testClosingSavingsAccountException() {
        String message = "Закрытие сберегательного счета";
        ClosingSavingsAccountException exception = new ClosingSavingsAccountException(message);
        assertEquals(message, exception.getMessage());
    }
}
