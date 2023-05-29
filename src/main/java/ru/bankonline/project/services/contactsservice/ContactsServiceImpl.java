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

/***
 * Сервис для работы с контактами
 */
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

    /***
     * Получает все контактные данные клиентов
     * @return список всех контактов
     * @throws NotFoundInBaseException если список контактов пуст
     */
    @Override
    public List<Contact> getAllCustomerContactsDetails() {
        List<Contact> contacts = contactsRepository.findAll();
        if (contacts.isEmpty()) {
            throw new NotFoundInBaseException("Список контактов пуст.");
        }
        return contacts;
    }

    /***
     * Обновляет контактные данные клиента
     * @param passportSeries серия паспорта
     * @param passportNumber номер паспорта
     * @param contact новые контактные данные клиента
     */
    @Override
    @Transactional
    public void updateContactsDetails(Integer passportSeries, Integer passportNumber, Contact contact) {
        Customer existingCustomer = customersService.customerSearchByPassportSeriesAndNumber(passportSeries, passportNumber);
        customersService.checkIfTheCustomerIsBlockedOrClosed(existingCustomer);
        existingCustomer.getContactDetails().setPhoneNumber(contact.getPhoneNumber());
        existingCustomer.getContactDetails().setEmail(contact.getEmail());

        existingCustomer.setUpdateDate(LocalDateTime.now());
        customersService.saveCustomersRepository(existingCustomer);
        log.info(existingCustomer.toString());
    }

    /***
     * Сохраняет контактные данные в репозиторий
     * @param contact контакты, которые нужно сохранить
     */
    @Override
    public void saveContactsRepository(Contact contact) {
        contactsRepository.save(contact);
    }
}