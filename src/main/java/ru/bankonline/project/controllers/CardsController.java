package ru.bankonline.project.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bankonline.project.dto.CardDTO;
import ru.bankonline.project.services.cardsservice.CardsService;

import java.math.BigDecimal;


@RestController
@RequestMapping("/cards")
public class CardsController {

    private final CardsService cardsService;
    private final ModelMapper modelMapper;

    public CardsController(CardsService cardsService, ModelMapper modelMapper) {
        this.cardsService = cardsService;
        this.modelMapper = modelMapper;
    }

    @PostMapping(path = "/series/{series}/number/{number}")
    public ResponseEntity<HttpStatus> addNewCardToTheCustomer(@PathVariable Integer series,
                                                              @PathVariable Integer number) {
        cardsService.openCardToTheCustomer(series, number);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/series/{series}/number/{number}/close/{cardNumber}")
    public ResponseEntity<String> deleteTheCardFromTheCustomer(@PathVariable Integer series, @PathVariable Integer number,
                                                               @PathVariable String cardNumber) {
        cardsService.closeCard(series, number, cardNumber);
        return ResponseEntity.ok("Карта с номером " + cardNumber + " успешно закрыта!");
    }

    @PatchMapping("/series/{series}/number/{number}/block/{cardNumber}")
    public ResponseEntity<String> blockTheCustomerCard(@PathVariable Integer series, @PathVariable Integer number,
                                                       @PathVariable String cardNumber) {
        cardsService.blockCard(series, number, cardNumber);
        return ResponseEntity.ok("Карта с номером " + cardNumber + " успешно заблокирована!");
    }

    @PatchMapping("/series/{series}/number/{number}/unlock/{cardNumber}")
    public ResponseEntity<String> unlockTheCustomerCard(@PathVariable Integer series, @PathVariable Integer number,
                                                        @PathVariable String cardNumber) {
        cardsService.unlockCard(series, number, cardNumber);
        return ResponseEntity.ok("Карта с номером " + cardNumber + " успешно разблокирована!");
    }

    @GetMapping("/series/{series}/number/{number}/checkBalance/{cardNumber}")
    public ResponseEntity<String> checkBalanceCard(@PathVariable Integer series, @PathVariable Integer number,
                                                   @PathVariable String cardNumber) {
        return ResponseEntity.ok(cardsService.checkBalance(series, number, cardNumber));
    }

    @PatchMapping("/{series}/{number}/{senderCardNumber}/{recipientCardNumber}/{amount}")
    public ResponseEntity<String> transferBetweenCardsCustomers(@PathVariable Integer series, @PathVariable Integer number,
                                                                @PathVariable String senderCardNumber,
                                                                @PathVariable String recipientCardNumber, @PathVariable BigDecimal amount) {
        cardsService.transferBetweenCards(series, number, senderCardNumber, recipientCardNumber, amount);
        return ResponseEntity.ok("Перевод с карты: " + senderCardNumber + " на карту: " + recipientCardNumber + " - выполнен!" );
    }

    @GetMapping("/details/{series}/{number}/{cardNumber}")
    public ResponseEntity<CardDTO> getCardDetails(@PathVariable Integer series, @PathVariable Integer number,
                                                  @PathVariable String cardNumber) {
        return ResponseEntity.ok(CardDTO.convertCardsToDTO(cardsService.getCardDetails(series, number, cardNumber), modelMapper));
    }
}