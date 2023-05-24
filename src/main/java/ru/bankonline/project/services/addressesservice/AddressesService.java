package ru.bankonline.project.services.addressesservice;

import ru.bankonline.project.entity.Address;

import java.util.List;

public interface AddressesService {
    List<Address> getAllCustomerAddresses();

    void updateAddress(Integer passportSeries, Integer passportNumber, Address address);

    void saveAddressesRepository(Address address);
}
