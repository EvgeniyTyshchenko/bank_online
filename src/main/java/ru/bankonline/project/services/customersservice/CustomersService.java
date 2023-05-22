package ru.bankonline.project.services.customersservice;

import ru.bankonline.project.entity.Customer;

public interface CustomersService {

    void addNewCustomer(Customer customer);

    Customer customerSearchByPassportSeriesAndNumber(Integer passportSeries, Integer passportNumber);

    void deleteCustomer(Integer passportSeries, Integer passportNumber);

    void updateCustomer(Integer passportSeries, Integer passportNumber, Customer customer);

    Customer getCustomerByCardNumber(String cardNumber);

    Customer getCustomerBySavingAccountNumber(String savingAccountNumber);

    void checkIfTheCustomerIsBlockedOrDeleted(Customer customer);

    void saveCustomersRepository(Customer customer);
}