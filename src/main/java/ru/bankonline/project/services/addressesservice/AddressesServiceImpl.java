package ru.bankonline.project.services.addressesservice;

import org.springframework.stereotype.Service;
import ru.bankonline.project.entity.Address;
import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.repositories.AddressesRepository;
import ru.bankonline.project.repositories.CustomersRepository;
import ru.bankonline.project.services.customersservice.CustomersService;
import ru.bankonline.project.utils.exceptions.NotFoundInBaseException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AddressesServiceImpl implements AddressesService {

    private final CustomersRepository customersRepository;
    private final AddressesRepository addressesRepository;
    private final CustomersService customersService;

    public AddressesServiceImpl(CustomersRepository customersRepository, AddressesRepository addressesRepository, CustomersService customersService) {
        this.customersRepository = customersRepository;
        this.addressesRepository = addressesRepository;
        this.customersService = customersService;
    }

    @Override
    public List<Address> getAllCustomerAddresses() {
        Optional<List<Address>> optionalAddresses = Optional.of(addressesRepository.findByAddresses());
        return optionalAddresses.orElseThrow(() -> new NotFoundInBaseException("Список адресов пуст."));
    }

    @Override
    public void updateAddress(Integer passportSeries, Integer passportNumber, Address address) {
        Customer existingCustomer = customersService.customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(existingCustomer);
        existingCustomer.getAddress().setCountry(address.getCountry());
        existingCustomer.getAddress().setCity(address.getCity());
        existingCustomer.getAddress().setStreet(address.getStreet());
        existingCustomer.getAddress().setHouse(address.getHouse());
        existingCustomer.getAddress().setApartment(address.getApartment());

        existingCustomer.setUpdateDate(LocalDateTime.now());
        customersRepository.save(existingCustomer);
    }
}