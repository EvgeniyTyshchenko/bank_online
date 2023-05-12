package ru.bankonline.project.utils.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnteringCardDataExceptionTest {

    @Test
    void testEnteringCardDataException() {
        String message = "Ввод данных карты";
        EnteringCardDataException exception = new EnteringCardDataException(message);
        assertEquals(message, exception.getMessage());
    }
}
