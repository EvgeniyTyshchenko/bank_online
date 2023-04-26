package ru.bankonline.project.services.contactsservice;

import org.springframework.stereotype.Service;
import ru.bankonline.project.entity.Contact;
import ru.bankonline.project.repositories.ContactsRepository;
import ru.bankonline.project.repositories.CustomersRepository;
import ru.bankonline.project.utils.exceptions.ContactMissingFromDBException;


@Service
public class ContactsServiceImpl implements ContactsService{

    private final ContactsRepository contactsRepository;
    private final CustomersRepository customersRepository;


    public ContactsServiceImpl(ContactsRepository contactsRepository, CustomersRepository customersRepository) {
        this.contactsRepository = contactsRepository;
        this.customersRepository = customersRepository;
    }

    @Override
    public Contact searchContactByEmail(String email) {
        Contact contact = contactsRepository.findByEmail(email);
        if (contact == null)
            throw new ContactMissingFromDBException("Контакт с email " + email + " не найден в базе данных!");
        return contact;
    }
}