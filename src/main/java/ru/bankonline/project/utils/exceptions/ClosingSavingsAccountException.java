package ru.bankonline.project.utils.exceptions;

/***
 * Исключение, которое возникает при закрытии сберегательного счета (если ранее счет уже был закрыт)
 */
public class ClosingSavingsAccountException extends RuntimeException {

    public ClosingSavingsAccountException(String msg) {
        super(msg);
    }
}
