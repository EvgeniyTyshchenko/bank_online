package ru.bankonline.project.utils.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnteringSavingsAccountDataExceptionTest {

    @Test
    void testEnteringSavingsAccountDataException() {
        String message = "Ввод данных сберегательного счета";
        EnteringSavingsAccountDataException exception = new EnteringSavingsAccountDataException(message);
        assertEquals(message, exception.getMessage());
    }
}
