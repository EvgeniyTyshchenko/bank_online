package ru.bankonline.project.services.customersservice;

import ru.bankonline.project.entity.Customer;

/***
 * Интерфейс CustomersService предоставляет методы для работы с клиентами
 */
public interface CustomersService {

    void addNewCustomer(Customer customer);

    Customer customerSearchByPassportSeriesAndNumber(Integer passportSeries, Integer passportNumber);

    void closingCustomer(Integer passportSeries, Integer passportNumber);

    void updateCustomer(Integer passportSeries, Integer passportNumber, Customer customer);

    Customer getCustomerByCardNumber(String cardNumber);

    Customer getCustomerBySavingAccountNumber(String savingAccountNumber);

    void checkIfTheCustomerIsBlockedOrClosed(Customer customer);

    void saveCustomersRepository(Customer customer);
}