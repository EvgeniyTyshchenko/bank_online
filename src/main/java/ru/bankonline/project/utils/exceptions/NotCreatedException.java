package ru.bankonline.project.utils.exceptions;

/***
 * Исключение, которое возникает при допущении ошибок в создании пользователя
 */
public class NotCreatedException extends RuntimeException {

    public NotCreatedException(String msg) {
        super(msg);
    }
}