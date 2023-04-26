package ru.bankonline.project.services.contactsservice;

import ru.bankonline.project.entity.Contact;

public interface ContactsService {

    Contact searchContactByEmail(String email);
}
