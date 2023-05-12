package ru.bankonline.project.utils.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerBlockingExceptionTest {

    @Test
    void testCustomerBlockingException() {
        String message = "Блокировка клиента";
        CustomerBlockingException exception = new CustomerBlockingException(message);
        assertEquals(message, exception.getMessage());
    }
}
