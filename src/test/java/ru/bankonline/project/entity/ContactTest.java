package ru.bankonline.project.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContactTest {

    @Test
    void shouldCreateContact() {
        Contact contact = new Contact("89054787888", "test5@example.com");

        assertEquals("89054787888", contact.getPhoneNumber());
        assertEquals("test5@example.com", contact.getEmail());
    }
}