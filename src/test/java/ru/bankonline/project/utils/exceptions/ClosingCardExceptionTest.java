package ru.bankonline.project.utils.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClosingCardExceptionTest {

    @Test
    void testClosingCardException() {
        String message = "Закрытие карты";
        ClosingCardException exception = new ClosingCardException(message);
        assertEquals(message, exception.getMessage());
    }
}