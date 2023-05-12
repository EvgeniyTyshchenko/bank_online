package ru.bankonline.project.utils.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerMissingFromDBExceptionTest {

    @Test
    void testCustomerMissingFromDBException() {
        String message = "Клиент отсутствует в базе данных";
        CustomerMissingFromDBException exception = new CustomerMissingFromDBException(message);
        assertEquals(message, exception.getMessage());
    }
}
