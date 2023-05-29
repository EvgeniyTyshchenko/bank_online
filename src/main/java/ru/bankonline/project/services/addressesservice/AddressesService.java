package ru.bankonline.project.services.addressesservice;

import ru.bankonline.project.entity.Address;

import java.util.List;

/***
 * Интерфейс AddressesService предоставляет методы для работы с адресами клиентов
 */
public interface AddressesService {
    List<Address> getAllCustomerAddresses();

    void updateAddress(Integer passportSeries, Integer passportNumber, Address address);

    void saveAddressesRepository(Address address);
}
