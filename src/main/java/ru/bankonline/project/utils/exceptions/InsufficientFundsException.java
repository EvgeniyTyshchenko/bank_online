package ru.bankonline.project.utils.exceptions;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String msg) {
        super(msg);
    }
}
