package ru.bankonline.project.utils.exceptions;

public class CustomerBalanceNotZeroException extends RuntimeException {

    public CustomerBalanceNotZeroException(String msg) {
        super(msg);
    }
}
