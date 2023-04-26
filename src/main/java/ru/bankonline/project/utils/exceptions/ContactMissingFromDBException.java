package ru.bankonline.project.utils.exceptions;

public class ContactMissingFromDBException extends RuntimeException {

    public ContactMissingFromDBException(String msg) {
        super(msg);
    }
}
