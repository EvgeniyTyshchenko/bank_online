package ru.bankonline.project.utils.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotUpdatedExceptionTest {

    @Test
    void testNotUpdatedException() {
        String message = "Не обновлен";
        NotUpdatedException exception = new NotUpdatedException(message);
        assertEquals(message, exception.getMessage());
    }
}
