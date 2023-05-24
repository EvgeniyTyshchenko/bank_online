package ru.bankonline.project.services.contactsservice;

import ru.bankonline.project.entity.Contact;

import java.util.List;

public interface ContactsService {

    List<Contact> getAllCustomerContactsDetails();

    void updateContactsDetails(Integer passportSeries, Integer passportNumber, Contact contact);

    void saveContactsRepository(Contact contact);
}
