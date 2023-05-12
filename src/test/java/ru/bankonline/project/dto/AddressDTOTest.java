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

        assertEquals(addressDTOs.size(), 2);

        assertEquals(addressDTOs.get(0).getCountry(), "Россия");
        assertEquals(addressDTOs.get(0).getCity(), "Казань");
        assertEquals(addressDTOs.get(0).getStreet(), "ул.Нижняя");
        assertEquals(addressDTOs.get(0).getHouse(), "17/1");
        assertEquals(addressDTOs.get(0).getApartment(), 158);

        assertEquals(addressDTOs.get(1).getCountry(), "Россия");
        assertEquals(addressDTOs.get(1).getCity(), "Краснодар");
        assertEquals(addressDTOs.get(1).getStreet(), "ул.Лунная");
        assertEquals(addressDTOs.get(1).getHouse(), "1/Б");
        assertEquals(addressDTOs.get(1).getApartment(), 4);
    }
}