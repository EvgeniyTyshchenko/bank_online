package ru.bankonline.project.utils.exceptions;

/***
 * Исключение, которое возникает при закрытии аккаунта клиента,
 * если на картах и/или сберегательных счетах пользователя имеются денежные средства
 */
public class CustomerBalanceNotZeroException extends RuntimeException {

    public CustomerBalanceNotZeroException(String msg) {
        super(msg);
    }
}
