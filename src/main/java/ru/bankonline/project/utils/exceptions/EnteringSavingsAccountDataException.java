package ru.bankonline.project.utils.exceptions;

/***
 * Исключение, которое возникает при ошибочном вводе данных сберегательного счета
 */
public class EnteringSavingsAccountDataException extends RuntimeException {

    public EnteringSavingsAccountDataException(String msg) {
        super(msg);
    }
}
