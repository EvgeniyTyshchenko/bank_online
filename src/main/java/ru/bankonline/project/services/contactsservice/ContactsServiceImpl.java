package ru.bankonline.project.services.contactsservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bankonline.project.entity.Contact;
import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.repositories.ContactsRepository;
import ru.bankonline.project.services.customersservice.CustomersService;
import ru.bankonline.project.utils.exceptions.NotFoundInBaseException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ContactsServiceImpl implements ContactsService {

    private final ContactsRepository contactsRepository;
    private final CustomersService customersService;

    @Autowired
    public ContactsServiceImpl(ContactsRepository contactsRepository, CustomersService customersService) {
        this.contactsRepository = contactsRepository;
        this.customersService = customersService;
    }

    @Override
    public List<Contact> getAllCustomerContactsDetails() {
        List<Contact> contacts = contactsRepository.findAll();
        if (contacts.isEmpty()) {
            throw new NotFoundInBaseException("Список контактов пуст.");
        }
        return contacts;
    }

    @Override
    @Transactional
    public void updateContactsDetails(Integer passportSeries, Integer passportNumber, Contact contact) {
        Customer existingCustomer = customersService.customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrDeleted(existingCustomer);
        existingCustomer.getContactDetails().setPhoneNumber(contact.getPhoneNumber());
        existingCustomer.getContactDetails().setEmail(contact.getEmail());

        existingCustomer.setUpdateDate(LocalDateTime.now());
        customersService.saveCustomersRepository(existingCustomer);
        log.info(existingCustomer.toString());
    }
}