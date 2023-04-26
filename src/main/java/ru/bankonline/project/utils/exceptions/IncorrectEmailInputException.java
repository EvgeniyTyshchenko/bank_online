package ru.bankonline.project.utils.exceptions;

public class IncorrectEmailInputException extends RuntimeException {

    public IncorrectEmailInputException(String msg) {
        super(msg);
    }
}