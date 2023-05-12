package ru.bankonline.project.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.entity.Card;
import ru.bankonline.project.entity.Contact;
import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.repositories.CardsRepository;
import ru.bankonline.project.repositories.CustomersRepository;
import ru.bankonline.project.services.cardsservice.CardsService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CardsControllerTest {

    @Mock
    private CardsService cardsService;
    @Mock
    private ModelMapper modelMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CardsRepository cardsRepository;
    @MockBean
    private CustomersRepository customersRepository;
    private static Customer customer;
    private static ObjectMapper objectMapper;


    @BeforeAll
    static void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        customer = new Customer();
        customer.setCustomerId(1);
        customer.setPassportSeries(8596);
        customer.setPassportNumber(120562);
        customer.setLastName("Мотько");
        customer.setFirstName("Владислав");
        customer.setPatronymic("Евгеньевич");
        customer.setBirthday("07.12.1976");
        customer.setContactDetails(new Contact("89884556598", "mottvladiskav@mail.ru"));

//        List<Card> cards = new ArrayList<>();
//        Card card = new Card(customer.getCustomerId(), "4444456584555698", "889",
//                "99965845255550002458", BigDecimal.valueOf(0), Currency.RUB);
//        cards.add(card);
//        customer.setCards(cards);
    }

    @Test
    public void shouldAddNewCardToTheCustomer() throws Exception {
        when(customersRepository.findByPassportSeriesAndPassportNumber(8596, 120562))
                .thenReturn(Optional.of(customer));
        when(customersRepository.save(customer)).thenReturn(customer);

        Card card = new Card(1, "4004456500055698", "127",
                "90065845000550002458", BigDecimal.valueOf(0), Currency.RUB);

        mockMvc.perform(post("/cards/series/8596/number/120562")
                .content(objectMapper.writeValueAsString(card))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}