package ru.bankonline.project.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import ru.bankonline.project.entity.Contact;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContactDTOTest {

    private static ModelMapper modelMapper;
    private static ContactDTO contactDTO;
    private static Contact contact;

    @BeforeAll
    static void setUp() {
        modelMapper = new ModelMapper();
        contactDTO = new ContactDTO("89054551232", "test1@yandex.ru");
        contact = new Contact("89057774545", "test2@yandex.ru");
    }

    @Test
    void shouldConvertToContact() {
        Contact contact = ContactDTO.convertToContact(contactDTO, modelMapper);

        assertEquals(contactDTO.getPhoneNumber(), contact.getPhoneNumber());
        assertEquals(contactDTO.getEmail(), contact.getEmail());
    }

    @Test
    void shouldConvertToContactDTO() {
        ContactDTO contactDTO = ContactDTO.convertToContactDTO(contact, modelMapper);

        assertEquals(contact.getPhoneNumber(), contactDTO.getPhoneNumber());
        assertEquals(contact.getEmail(), contactDTO.getEmail());
    }

    @Test
    void shouldConvertListContactDetailsToDTO() {
        Contact newContact = new Contact("89034551212", "test3@yandex.ru");
        List<Contact> contacts = new ArrayList<>(List.of(contact, newContact));

        List<ContactDTO> contactDTOs = ContactDTO.convertListContactDetailsToDTO(contacts, modelMapper);

        assertEquals(2, contactDTOs.size());

        assertEquals("89057774545", contactDTOs.get(0).getPhoneNumber());
        assertEquals("test2@yandex.ru", contactDTOs.get(0).getEmail());

        assertEquals("89034551212", contactDTOs.get(1).getPhoneNumber());
        assertEquals("test3@yandex.ru", contactDTOs.get(1).getEmail());
    }
}