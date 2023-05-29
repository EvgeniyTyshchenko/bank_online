package ru.bankonline.project.entity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressTest {

    private static Address address;

    @BeforeAll
    static void setUp() {
        address = new Address("Россия", "Красноярск",
                "ул.Морозная", "75/1", 119);
    }

    @Test
    void shouldTestConstructorAndGetters() {
        assertEquals("Россия", address.getCountry());
        assertEquals("Красноярск", address.getCity());
        assertEquals("ул.Морозная", address.getStreet());
        assertEquals("75/1", address.getHouse());
        assertEquals(119, address.getApartment());
    }

    @Test
    void shouldTestSetters() {
        address.setCity("Москва");
        address.setStreet("ул.Лермонтова");
        address.setHouse("215/В");
        address.setApartment(17);
        assertEquals("Москва", address.getCity());
        assertEquals("ул.Лермонтова", address.getStreet());
        assertEquals("215/В", address.getHouse());
        assertEquals(Integer.valueOf(17), address.getApartment());
    }
}