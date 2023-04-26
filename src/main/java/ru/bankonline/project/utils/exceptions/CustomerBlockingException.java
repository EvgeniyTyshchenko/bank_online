package ru.bankonline.project.utils.exceptions;

public class CustomerBlockingException extends RuntimeException {

    public CustomerBlockingException(String msg) {
        super(msg);
    }
}
