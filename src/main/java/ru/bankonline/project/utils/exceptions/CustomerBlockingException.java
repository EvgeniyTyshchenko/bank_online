package ru.bankonline.project.utils.exceptions;

/***
 * Исключение, которое возникает при неправильном использовании аккаунта клиента
 * (зависит от контекста ситуации)
 */
public class CustomerBlockingException extends RuntimeException {

    public CustomerBlockingException(String msg) {
        super(msg);
    }
}
