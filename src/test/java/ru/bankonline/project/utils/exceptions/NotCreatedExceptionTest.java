package ru.bankonline.project.utils.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotCreatedExceptionTest {

    @Test
    void testNotCreatedException() {
        String message = "Не создано";
        NotCreatedException exception = new NotCreatedException(message);
        assertEquals(message, exception.getMessage());
    }
}
