package ru.bankonline.project.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.bankonline.project.BankOnlineApplication;
import ru.bankonline.project.dto.AddressDTO;
import ru.bankonline.project.entity.Address;
import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.repositories.AddressesRepository;
import ru.bankonline.project.repositories.CustomersRepository;
import ru.bankonline.project.services.addressesservice.AddressesService;
import ru.bankonline.project.utils.validators.AddressValidator;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BankOnlineApplication.class)
@AutoConfigureMockMvc
class AddressesControllerTest {

    @Mock
    private AddressesService addressesService;
    @Mock
    private AddressValidator addressValidator;
    @Mock
    private ModelMapper modelMapper;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private AddressesRepository addressesRepository;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGetAllAddresses() throws Exception {
        Address addressOne = new Address("Россия", "Москва", "ул.Заречная", "2/1", 117);
        Address addressTwo = new Address("Россия", "Краснодар", "ул.Верхняя", "27/3", 12);

        addressesRepository.save(addressOne);
        addressesRepository.save(addressTwo);

        mockMvc.perform(get("/addresses/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].country").value("Россия"))
                .andExpect(jsonPath("$[0].city").value("Москва"))
                .andExpect(jsonPath("$[0].street").value("ул.Заречная"))
                .andExpect(jsonPath("$[0].house").value("2/1"))
                .andExpect(jsonPath("$[0].apartment").value(117))
                .andExpect(jsonPath("$[1].country").value("Россия"))
                .andExpect(jsonPath("$[1].city").value("Краснодар"))
                .andExpect(jsonPath("$[1].street").value("ул.Верхняя"))
                .andExpect(jsonPath("$[1].house").value("27/3"))
                .andExpect(jsonPath("$[1].apartment").value(12));
    }

    @Test
    void shouldUpdateAddress() throws Exception {
        Customer customer = new Customer();
        customer.setPassportSeries(7854);
        customer.setPassportNumber(965523);
        Address address = new Address("Россия", "Москва", "ул.Заречная", "2/1", 117);
        customer.setAddress(address);

        customersRepository.save(customer);

        ObjectMapper objectMapper = new ObjectMapper();
        AddressDTO addressDTO = new AddressDTO("Россия", "Рязань", "ул.Верхняя", "7/1",116);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/addresses/series/{series}/number/{number}",
                        customer.getPassportSeries(), customer.getPassportNumber())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressDTO));
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }
}