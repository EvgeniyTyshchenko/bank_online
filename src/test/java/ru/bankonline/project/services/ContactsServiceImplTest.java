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
import ru.bankonline.project.repositories.ContactsRepository;
import ru.bankonline.project.services.contactsservice.ContactsServiceImpl;
import ru.bankonline.project.services.customersservice.CustomersService;
import ru.bankonline.project.utils.exceptions.NotFoundInBaseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ContactsServiceImplTest {

    @Mock
    private ContactsRepository contactsRepository;
    @Mock
    private CustomersService customersService;
    @InjectMocks
    private ContactsServiceImpl contactsService;
    private static List<Contact> contactList;

    @BeforeAll
    static void setUp() {
        contactList = new ArrayList<>();
        contactList.add(new Contact("89881234565", "test1@mail.ru"));
        contactList.add(new Contact("89894562536", "test2@mail.ru"));
    }

    @Test
    void shouldGetAllCustomerContacts() {
        when(contactsRepository.findAll()).thenReturn(contactList);

        List<Contact> actualContacts = contactsService.getAllCustomerContactsDetails();
        Assertions.assertEquals(contactList, actualContacts);
    }

    @Test
    void shouldBeAnExceptionDueToAnEmptyContactList() {
        when(contactsRepository.findAll()).thenReturn(Collections.emptyList());

        NotFoundInBaseException exception = Assertions.assertThrows(NotFoundInBaseException.class, () -> {
            contactsService.getAllCustomerContactsDetails();
        });
        Assertions.assertEquals("Список контактов пуст.", exception.getMessage());
    }

    @Test
    void shouldUpdateContact() {
        Customer customer = new Customer(7845, 456236, "Нестеров", "Вадим", "Дмитриевич", "27.06.1987",
                new Address("Россия", "Краснодар", "ул.Северная", "8/д", 24), contactList.get(0));

        when(customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(), customer.getPassportNumber())).thenReturn(customer);

        Contact newContact = contactList.get(1);
        contactsService.updateContactsDetails(customer.getPassportSeries(), customer.getPassportNumber(), newContact);
        Customer updatedCustomer = customersService.customerSearchByPassportSeriesAndNumber(customer.getPassportSeries(), customer.getPassportNumber());
        Assertions.assertEquals(customer.getContactDetails(), updatedCustomer.getContactDetails());
        log.info(updatedCustomer.getContactDetails().toString());
    }
}