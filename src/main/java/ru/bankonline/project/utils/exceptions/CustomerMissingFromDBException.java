package ru.bankonline.project.utils.exceptions;

/***
 * Исключение, которое возникает при отсутствии клиента в базе данных банка
 */
public class CustomerMissingFromDBException extends RuntimeException {

    public CustomerMissingFromDBException(String msg) {
        super(msg);
    }
}
