package ru.bankonline.project.utils.exceptions;

/***
 * Исключение, которое возникает при нарушении условий сберегательного счета
 */
public class ViolationTermsDepositException extends RuntimeException {

    public ViolationTermsDepositException(String msg) {
        super(msg);
    }
}