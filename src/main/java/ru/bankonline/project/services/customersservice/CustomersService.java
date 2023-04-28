package ru.bankonline.project.services.customersservice;

import ru.bankonline.project.entity.Customer;

import javax.mail.MessagingException;

public interface CustomersService {

    void addNewCustomer(Customer customer) throws MessagingException;

    Customer customerSearchByPassportSeriesAndNumber(Integer passportSeries, Integer passportNumber);

    void deleteCustomer(Integer passportSeries, Integer passportNumber);

    void updateCustomer(Integer passportSeries, Integer passportNumber, Customer customer);

    Customer getCustomerByCardNumber(String cardNumber);

    Customer getCustomerBySavingAccountNumber(String savingAccountNumber);

    void checkIfTheCustomerIsBlockedOrDeleted(Customer customer);
}