package ru.bankonline.project.utils.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ViolationTermsDepositExceptionTest {

    @Test
    void testViolationTermsDepositException() {
        String message = "Нарушение условий депозита";
        ViolationTermsDepositException exception = new ViolationTermsDepositException(message);
        assertEquals(message, exception.getMessage());
    }
}
