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
import ru.bankonline.project.dto.ContactDTO;
import ru.bankonline.project.dto.CustomerDTO;
import ru.bankonline.project.entity.Contact;
import ru.bankonline.project.repositories.ContactsRepository;
import ru.bankonline.project.services.contactsservice.ContactsService;
import ru.bankonline.project.utils.validators.ContactValidator;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ContactsControllerTest {

    @Mock
    private ContactsService contactsService;
    @Mock
    private ContactValidator contactValidator;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ContactsRepository contactsRepository;
    @Autowired
    private MockMvc mockMvc;
    private static List<Contact> contacts;
    private static List<ContactDTO> contactDTOs;
    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setPassportSeries(6658);
        customerDTO.setPassportNumber(895623);
        customerDTO.setLastName("Шефер");
        customerDTO.setFirstName("Станислав");
        customerDTO.setPatronymic("Станиславович");
        customerDTO.setBirthday("09.03.1991");
        contactDTOs = new ArrayList<>(List.of(new ContactDTO("89054778899", "stanislaavv@yandex.ru")));
        customerDTO.setContactDTO(contactDTOs.get(0));

        contacts = new ArrayList<>(List.of(new Contact("89034551225", "test10@mail.ru")));
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldGetAllContactDetails() throws Exception {
        when(contactsRepository.findByContacts()).thenReturn(contacts);

        mockMvc.perform(get("/contacts/getAll"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldUpdateContactDetailsWithInvalidData() throws Exception {
        contactDTOs.get(0).setPhoneNumber("");

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/contacts/series/6658/number/895623")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contactDTOs.get(0)));
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }
}