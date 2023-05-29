package ru.bankonline.project.utils.exceptions;

/***
 * Исключение, которое возникает при сохранении в базу данных банка уже имеющихся паспортных данных
 */
public class PassportDuplicateException extends RuntimeException {

    public PassportDuplicateException(String msg) {
        super(msg);
    }
}