package ru.bankonline.project.constants;

import lombok.AllArgsConstructor;

/***
 * Перечисление типов транзакций, которые могут быть выполнены в банковской системе
 * Каждый тип транзакции имеет своё описание
 */
@AllArgsConstructor
public enum TransactionType {

    REGISTERCUSTOMER("Registering a new customer"),
    CLOSEDCUSTOMER("Closing customer"),
    OPENCARD("Opening the card"),
    CLOSECARD("Closing the card"),
    BLOCKINGCARD("Card blocking"),
    UNLOCKINGCARD("Unlocking the card"),
    INTRANSFER("Incoming transfer"),
    OUTTRANSFER("Outgoing transfer"),
    OPENACCOUNT("Opening an account"),
    CLOSEACCOUNT("Closing an account"),
    CHECKBALANCE("Checking balance"),
    CHECKTRANSACTIONLIST("General request"),
    CAPITALIZATION("Capitalization");

    private final String description;

    @Override
    public String toString() {
        return description;
    }
}