package ru.bankonline.project.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.bankonline.project.dto.AddressDTO;
import ru.bankonline.project.dto.CustomerDTO;
import ru.bankonline.project.entity.Address;
import ru.bankonline.project.repositories.AddressesRepository;
import ru.bankonline.project.services.addressesservice.AddressesService;
import ru.bankonline.project.utils.validators.AddressValidator;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class AddressesControllerTest {

    @Mock
    private AddressesService addressesService;
    @Mock
    private AddressValidator addressValidator;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private AddressesRepository addressesRepository;
    @Autowired
    private MockMvc mockMvc;
    private static List<Address> addresses;
    private static List<AddressDTO> addressDTOs;
    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setPassportSeries(5598);
        customerDTO.setPassportNumber(235645);
        customerDTO.setLastName("Иванов");
        customerDTO.setFirstName("Виктор");
        customerDTO.setPatronymic("Петрович");
        customerDTO.setBirthday("05.08.1971");
        addressDTOs = new ArrayList<>(List.of(new AddressDTO("Россия", "Москва", "ул.Заречная", "2/1", 117)));
        customerDTO.setAddressDTO(addressDTOs.get(0));

        addresses = new ArrayList<>(List.of(new Address("Россия", "Краснодар", "ул.Верхняя", "27/3", 12)));
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldGetAllAddresses() throws Exception {
        when(addressesRepository.findByAddresses()).thenReturn(addresses);

        mockMvc.perform(get("/addresses/getAll"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldUpdateAddressWithInvalidData() throws Exception {
        addressDTOs.get(0).setCountry("");

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/addresses/series/5598/number/235645")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressDTOs.get(0)));
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }
}