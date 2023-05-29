package ru.bankonline.project.utils.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerBalanceNotZeroExceptionTest {

    @Test
    void testCustomerBalanceNotZeroException() {
        String message = "Баланс клиента не равен 0";
        CustomerBalanceNotZeroException exception = new CustomerBalanceNotZeroException(message);
        assertEquals(message, exception.getMessage());
    }
}
