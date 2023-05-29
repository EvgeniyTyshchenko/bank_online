package ru.bankonline.project.utils.exceptions;

/***
 * Исключение, которое возникает при отсутствии в базе данных банка ожидаемых объектов
 * (зависит от контекста)
 */
public class NotFoundInBaseException extends RuntimeException {

    public NotFoundInBaseException(String msg) {
        super(msg);
    }
}