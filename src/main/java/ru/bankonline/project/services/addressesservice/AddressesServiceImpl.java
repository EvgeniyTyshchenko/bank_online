package ru.bankonline.project.services.addressesservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.bankonline.project.entity.Address;
import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.repositories.AddressesRepository;
import ru.bankonline.project.repositories.CustomersRepository;
import ru.bankonline.project.services.customersservice.CustomersService;
import ru.bankonline.project.utils.exceptions.NotFoundInBaseException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class AddressesServiceImpl implements AddressesService {

    private final CustomersRepository customersRepository;
    private final AddressesRepository addressesRepository;
    private final CustomersService customersService;

    public AddressesServiceImpl(CustomersRepository customersRepository,
                                AddressesRepository addressesRepository, CustomersService customersService) {
        this.customersRepository = customersRepository;
        this.addressesRepository = addressesRepository;
        this.customersService = customersService;
    }

    @Override
    public List<Address> getAllCustomerAddresses() {
        List<Address> addresses = addressesRepository.findByAddresses();
        if (addresses.isEmpty()) {
            throw new NotFoundInBaseException("Список адресов пуст.");
        }
        return addresses;
    }

    @Override
    public void updateAddress(Integer passportSeries, Integer passportNumber, Address address) {
        Customer existingCustomer = customersService
                .customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(existingCustomer);
        existingCustomer.getAddress().setCountry(address.getCountry());
        existingCustomer.getAddress().setCity(address.getCity());
        existingCustomer.getAddress().setStreet(address.getStreet());
        existingCustomer.getAddress().setHouse(address.getHouse());
        existingCustomer.getAddress().setApartment(address.getApartment());

        existingCustomer.setUpdateDate(LocalDateTime.now());
        customersRepository.save(existingCustomer);
        log.info(existingCustomer.toString());
    }
}