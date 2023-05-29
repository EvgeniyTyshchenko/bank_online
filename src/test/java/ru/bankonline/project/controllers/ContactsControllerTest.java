package ru.bankonline.project.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.bankonline.project.BankOnlineApplication;
import ru.bankonline.project.dto.ContactDTO;
import ru.bankonline.project.entity.Contact;
import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.services.contactsservice.ContactsService;
import ru.bankonline.project.services.customersservice.CustomersService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = BankOnlineApplication.class)
@AutoConfigureMockMvc
class ContactsControllerTest {

    @Autowired
    private CustomersService customersService;
    @Autowired
    private ContactsService contactsService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void shouldGetAllContactDetails() throws Exception {
        Contact contactOne = new Contact("89054778899", "stanislaavv@yandex.ru");
        Contact contactTwo = new Contact("89034551225", "test10@mail.ru");

        contactsService.saveContactsRepository(contactOne);
        contactsService.saveContactsRepository(contactTwo);

        mockMvc.perform(get("/contacts/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].phoneNumber").value("89054778899"))
                .andExpect(jsonPath("$[0].email").value("stanislaavv@yandex.ru"))
                .andExpect(jsonPath("$[1].phoneNumber").value("89034551225"))
                .andExpect(jsonPath("$[1].email").value("test10@mail.ru"));
    }

    @Test
    void shouldUpdateContactDetails() throws Exception {
        Customer customer = new Customer();
        customer.setPassportSeries(6658);
        customer.setPassportNumber(895623);
        Contact contact = new Contact("89054110023", "evgeniy1990@mail.ru");
        customer.setContactDetails(contact);

        customersService.saveCustomersRepository(customer);

        ObjectMapper objectMapper = new ObjectMapper();
        ContactDTO contactDTO = new ContactDTO("89054110022", "evgeniy1991@mail.ru");

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/contacts/series/{series}/number/{number}",
                        customer.getPassportSeries(), customer.getPassportNumber())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contactDTO));
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }
}