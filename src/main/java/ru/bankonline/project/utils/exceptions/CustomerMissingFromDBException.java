package ru.bankonline.project.utils.exceptions;

public class CustomerMissingFromDBException extends RuntimeException {

    public CustomerMissingFromDBException(String msg) {
        super(msg);
    }
}
