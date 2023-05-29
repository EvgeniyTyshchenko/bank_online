package ru.bankonline.project.utils.exceptions;

/***
 * Исключение, которое возникает при выполнении переводов
 * (если у отправителя недостаточно денежных средств для совершения транзакции)
 */
public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String msg) {
        super(msg);
    }
}