package ru.bankonline.project.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import ru.bankonline.project.entity.Address;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressDTOTest {

    private static ModelMapper modelMapper;
    private static AddressDTO addressDTO;
    private static Address address;

    @BeforeAll
    static void setUp() {
        modelMapper = new ModelMapper();
        addressDTO = new AddressDTO("Россия", "Иваново", "ул.Морская", "2/3", 354);
        address = new Address("Россия", "Казань", "ул.Нижняя", "17/1", 158);
    }

    @Test
    void shouldConvertToAddress() {
        Address address = AddressDTO.convertToAddress(addressDTO, modelMapper);

        assertEquals(addressDTO.getCountry(), address.getCountry());
        assertEquals(addressDTO.getCity(), address.getCity());
        assertEquals(addressDTO.getHouse(), address.getHouse());
        assertEquals(addressDTO.getApartment(), address.getApartment());
    }

    @Test
    void shouldConvertToAddressDTO() {
        AddressDTO addressDTO = AddressDTO.convertToAddressDTO(address, modelMapper);

        assertEquals(address.getCountry(), addressDTO.getCountry());
        assertEquals(address.getCity(), addressDTO.getCity());
        assertEquals(address.getHouse(), addressDTO.getHouse());
        assertEquals(address.getApartment(), addressDTO.getApartment());
    }

    @Test
    void shouldConvertListAddressesToDTO() {
        Address newAddress = new Address("Россия", "Краснодар", "ул.Лунная", "1/Б", 4);
        List<Address> addresses = new ArrayList<>();

        addresses.add(address);
        addresses.add(newAddress);

        List<AddressDTO> addressDTOs = AddressDTO.convertListAddressesToDTO(addresses, modelMapper);

        assertEquals(2, addressDTOs.size());

        assertEquals("Россия", addressDTOs.get(0).getCountry());
        assertEquals("Казань", addressDTOs.get(0).getCity());
        assertEquals("ул.Нижняя", addressDTOs.get(0).getStreet());
        assertEquals("17/1", addressDTOs.get(0).getHouse());
        assertEquals(158, addressDTOs.get(0).getApartment());

        assertEquals("Россия", addressDTOs.get(1).getCountry());
        assertEquals("Краснодар", addressDTOs.get(1).getCity());
        assertEquals("ул.Лунная", addressDTOs.get(1).getStreet());
        assertEquals("1/Б", addressDTOs.get(1).getHouse());
        assertEquals(4, addressDTOs.get(1).getApartment());
    }
}