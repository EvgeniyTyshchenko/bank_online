package ru.bankonline.project.utils.exceptions;

/***
 * Исключение, которое возникает при допущении ошибок в обновлении пользователя
 */
public class NotUpdatedException extends RuntimeException {

    public NotUpdatedException(String msg) {
        super(msg);
    }
}