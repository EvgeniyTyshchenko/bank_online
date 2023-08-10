package ru.bankonline.project.utils.exceptions;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessages {

    public static final String CLOSED_CARD_BALANCE_IS_NOT_ZERO = "Ошибка в закрытии карты! Пожалуйста, убедитесь, что баланс равен 0. " +
            "Вы можете сделать заявку на снятие денег в кассе, снять деньги в банкомате или же перевести оставшуюся сумму на другой счет.";
}
