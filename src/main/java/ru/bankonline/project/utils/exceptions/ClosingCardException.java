package ru.bankonline.project.utils.exceptions;

/***
 * Исключение, которое возникает при неправильном использовании банковской карты
 * (зависит от контекста ситуации)
 */
public class ClosingCardException extends RuntimeException {

    public ClosingCardException(String msg) {
        super(msg);
    }
}