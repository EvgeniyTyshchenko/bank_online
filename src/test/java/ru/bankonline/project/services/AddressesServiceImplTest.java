package ru.bankonline.project.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bankonline.project.entity.Address;
import ru.bankonline.project.entity.Contact;
import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.repositories.AddressesRepository;
import ru.bankonline.project.repositories.CustomersRepository;
import ru.bankonline.project.services.addressesservice.AddressesServiceImpl;
import ru.bankonline.project.services.customersservice.CustomersService;
import ru.bankonline.project.utils.exceptions.NotFoundInBaseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class AddressesServiceImplTest {

    @Mock
    private CustomersRepository customersRepository;
    @Mock
    private AddressesRepository addressesRepository;
    @Mock
    private CustomersService customersService;
    @InjectMocks
    private AddressesServiceImpl addressesService;
    private static List<Address> addressList;

    @BeforeAll
    static void setUp() {
        addressList = new ArrayList<>();
        addressList.add(new Address("Россия", "Москва", "ул.Мира", "25/Б", 254));
        addressList.add(new Address("Россия", "Казань", "ул.Российская", "7/3", 12));
    }

    @Test
    void shouldGetAllCustomerAddresses() {
        when(addressesRepository.findByAddresses()).thenReturn(addressList);

        List<Address> actualAddresses = addressesService.getAllCustomerAddresses();
        Assertions.assertEquals(addressList, actualAddresses);
    }

    @Test
    void shouldBeAnExceptionDueToAnEmptyAddressList() {
        when(addressesRepository.findByAddresses()).thenReturn(Collections.emptyList());

        NotFoundInBaseException exception = Assertions.assertThrows(NotFoundInBaseException.class, () -> {
            addressesService.getAllCustomerAddresses();
        });
        Assertions.assertEquals("Список адресов пуст.", exception.getMessage());
    }

    @Test
    void shouldUpdateAddress() {
        Customer customer = new Customer(1234, 123456, "Солгалов", "Максим", "Викторович",
                "09.05.1978", addressList.get(0), new Contact("89887894565", "solgalov@mail.ru"));

        when(customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(), customer.getPassportNumber())).thenReturn(customer);

        Address newAddress = addressList.get(1);
        addressesService.updateAddress(customer.getPassportSeries(), customer.getPassportNumber(), newAddress);
        Customer updatedCustomer = customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(), customer.getPassportNumber());
        Assertions.assertEquals(customer.getAddress(), updatedCustomer.getAddress());
        log.info(updatedCustomer.getAddress().toString());
    }
}