package ru.bankonline.project.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bankonline.project.dto.CardDTO;
import ru.bankonline.project.services.cardsservice.CardsService;

import javax.mail.MessagingException;
import java.math.BigDecimal;

@RestController
@RequestMapping("/cards")
@Tag(name = "Карты", description = "CRUD-операции для работы с банковскими картами")
public class CardsController {

    private final CardsService cardsService;
    private final ModelMapper modelMapper;

    @Autowired
    public CardsController(CardsService cardsService, ModelMapper modelMapper) {
        this.cardsService = cardsService;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "Открытие карты",
            description = "Необходимо вводить серию и номер паспорта клиента")
    @PostMapping(path = "/series/{series}/number/{number}")
    public ResponseEntity<HttpStatus> addNewCardToTheCustomer(@PathVariable Integer series,
                                                              @PathVariable Integer number) throws MessagingException {
        cardsService.openCardToTheCustomer(series, number);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Operation(summary = "Закрытие карты",
            description = "Необходимо вводить серию и номер паспорта клиента, " +
                    "а также номер карты, которую требуется закрыть")
    @PatchMapping("/series/{series}/number/{number}/close/{cardNumber}")
    public ResponseEntity<String> deleteTheCardFromTheCustomer(@PathVariable Integer series, @PathVariable Integer number,
                                                               @PathVariable String cardNumber) throws MessagingException {
        cardsService.closeCard(series, number, cardNumber);
        return ResponseEntity.ok("Карта с номером " + cardNumber + " успешно закрыта!");
    }

    @Operation(summary = "Блокировка карты",
            description = "Необходимо вводить серию и номер паспорта клиента, " +
                    "а также номер карты, которую требуется заблокировать")
    @PatchMapping("/series/{series}/number/{number}/block/{cardNumber}")
    public ResponseEntity<String> blockTheCustomerCard(@PathVariable Integer series, @PathVariable Integer number,
                                                       @PathVariable String cardNumber) {
        cardsService.blockCard(series, number, cardNumber);
        return ResponseEntity.ok("Блокировка карты " + cardNumber + " успешно выполнена!");
    }

    @Operation(summary = "Разблокировка карты",
            description = "Необходимо вводить серию и номер паспорта клиента, " +
                    "а также номер карты, которую требуется разблокировать")
    @PatchMapping("/series/{series}/number/{number}/unlock/{cardNumber}")
    public ResponseEntity<String> unlockTheCustomerCard(@PathVariable Integer series, @PathVariable Integer number,
                                                        @PathVariable String cardNumber) {
        cardsService.unlockCard(series, number, cardNumber);
        return ResponseEntity.ok("Произведена разблокировка карты " + cardNumber);
    }

    @Operation(summary = "Проверка баланса карты",
            description = "Необходимо вводить серию и номер паспорта клиента, " +
                    "а также номер карты, где нужно узнать баланс")
    @GetMapping("/series/{series}/number/{number}/checkBalance/{cardNumber}")
    public ResponseEntity<String> checkBalanceCard(@PathVariable Integer series, @PathVariable Integer number,
                                                   @PathVariable String cardNumber) {
        return ResponseEntity.ok(cardsService.checkBalance(series, number, cardNumber));
    }

    @Operation(summary = "Получить информацию по карте",
            description = "Необходимо вводить серию, номер паспорта, а также номер карты, по которой необходима информация")
    @GetMapping("/details/{series}/{number}/{cardNumber}")
    public ResponseEntity<CardDTO> getCardDetails(@PathVariable Integer series, @PathVariable Integer number,
                                                  @PathVariable String cardNumber) {
        return ResponseEntity.ok(CardDTO.convertCardToDTO(cardsService.getCardDetails(series, number, cardNumber), modelMapper));
    }
}