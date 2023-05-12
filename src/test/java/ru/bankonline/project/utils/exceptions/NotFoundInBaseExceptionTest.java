package ru.bankonline.project.utils.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotFoundInBaseExceptionTest {

    @Test
    void testNotFoundInBaseException() {
        String message = "Не найден в базе";
        NotFoundInBaseException exception = new NotFoundInBaseException(message);
        assertEquals(message, exception.getMessage());
    }
}
