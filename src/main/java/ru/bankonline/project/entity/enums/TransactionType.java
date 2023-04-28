package ru.bankonline.project.entity.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TransactionType {

    REGISTERCUSTOMER("Registering a new customer"),
    DELETECUSTOMER("removalremoval"),
    OPENCARD("Opening the card"),
    CLOSECARD("Closing the card"),
    BLOCKINGCARD("Card blocking"),
    UNLOCKINGCARD("Unlocking the card"),
    INTRANSFER("Incoming transfer"),
    OUTTRANSFER("Outgoing transfer"),
    OPENACCOUNT("Opening an account"),
    CLOSEACCOUNT("Closing an account"),
    CHECKBALANCE("Checking balance"),
    CHECKTRANSACTIONLIST("general requestgeneral request"),
    CAPITALIZATION("Capitalization");

    private final String description;

    @Override
    public String toString() {
        return description;
    }
}