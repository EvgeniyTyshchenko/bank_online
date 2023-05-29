package ru.bankonline.project.utils.exceptions;

/***
 * Исключение, которое возникает при ошибочном вводе данных карты
 */
public class EnteringCardDataException extends RuntimeException {

    public EnteringCardDataException(String msg) {
        super(msg);
    }
}