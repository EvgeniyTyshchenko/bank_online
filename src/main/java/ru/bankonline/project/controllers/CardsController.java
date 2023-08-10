package ru.bankonline.project.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bankonline.project.dto.CardDTO;
import ru.bankonline.project.mappers.CardMapper;
import ru.bankonline.project.services.cardsservice.CardsService;

import javax.mail.MessagingException;

/***
 * Контроллер для работы с банковскими картами
 */
@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@Tag(name = "Карты", description = "CRUD-операции для работы с банковскими картами")
public class CardsController {

    private final CardsService cardsService;
    private final CardMapper cardMapper;

    /***
     * Открывает новую карту для клиента по указанным серии и номеру паспорта
     * @param series серия паспорта
     * @param number номер паспорта
     * @return статус 200 в случае успешного открытия карты
     * @throws MessagingException если возникла ошибка при отправке уведомления клиенту о новой карте
     */
    @Operation(summary = "Открытие карты",
            description = "Необходимо вводить серию и номер паспорта клиента")
    @PostMapping(path = "/series/{series}/number/{number}")
    public ResponseEntity<HttpStatus> addNewCardToTheCustomer(@PathVariable Integer series,
                                                              @PathVariable Integer number) throws MessagingException {
        cardsService.openCardToTheCustomer(series, number);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    /***
     * Закрывает карту клиента по указанным серии, номеру паспорта и номеру карты
     * @param series серия паспорта
     * @param number номер паспорта
     * @param cardNumber номер карты
     * @return сообщение об успешном закрытии карты
     * @throws MessagingException исключение, которое может быть вызвано при отправке уведомления на почту клиента
     */
    @Operation(summary = "Закрытие карты",
            description = "Необходимо вводить серию и номер паспорта клиента, " +
                    "а также номер карты, которую требуется закрыть")
    @PatchMapping("/series/{series}/number/{number}/close/{cardNumber}")
    /*
     * Много параметров в url
     * Для таких целей лучше подходит передача параметров в @RequestBody
     * Создать доп. dto, например CloseCardDto с полями series, number, cardNumber и принимать ее в качестве параметра
     */
    public ResponseEntity<String> deleteTheCardFromTheCustomer(@PathVariable Integer series, @PathVariable Integer number,
                                                               @PathVariable String cardNumber) throws MessagingException {
        cardsService.closeCard(series, number, cardNumber);
        return ResponseEntity.ok("Карта с номером " + cardNumber + " успешно закрыта!");
    }

    /***
     * Блокирует карту клиента по заданным параметрам
     * @param series серия паспорта
     * @param number номер паспорта
     * @param cardNumber номер карты
     * @return сообщение об успешном выполнении операции
     */
    @Operation(summary = "Блокировка карты",
            description = "Необходимо вводить серию и номер паспорта клиента, " +
                    "а также номер карты, которую требуется заблокировать")
    @PatchMapping("/series/{series}/number/{number}/block/{cardNumber}")
    public ResponseEntity<String> blockTheCustomerCard(@PathVariable Integer series, @PathVariable Integer number,
                                                       @PathVariable String cardNumber) {
        cardsService.blockCard(series, number, cardNumber);
        return ResponseEntity.ok("Блокировка карты " + cardNumber + " успешно выполнена!");
    }

    /***
     * Разблокирует карту клиента по указанным параметрам
     * @param series серия паспорта
     * @param number номер паспорта
     * @param cardNumber номер карты
     * @return ответ с сообщением об успешной разблокировке карты
     */
    @Operation(summary = "Разблокировка карты",
            description = "Необходимо вводить серию и номер паспорта клиента, " +
                    "а также номер карты, которую требуется разблокировать")
    @PatchMapping("/series/{series}/number/{number}/unlock/{cardNumber}")
    public ResponseEntity<String> unlockTheCustomerCard(@PathVariable Integer series, @PathVariable Integer number,
                                                        @PathVariable String cardNumber) {
        cardsService.unlockCard(series, number, cardNumber);
        return ResponseEntity.ok("Произведена разблокировка карты " + cardNumber);
    }

    /***
     * Проверяет баланс карты по указанным параметрам
     * @param series серия паспорта
     * @param number номер паспорта
     * @param cardNumber номер карты
     * @return ответ сервера с информацией о балансе карты
     */
    @Operation(summary = "Проверка баланса карты",
            description = "Необходимо вводить серию и номер паспорта клиента, " +
                    "а также номер карты, где нужно узнать баланс")
    @GetMapping("/series/{series}/number/{number}/checkBalance/{cardNumber}")
    public ResponseEntity<String> checkBalanceCard(@PathVariable Integer series, @PathVariable Integer number,
                                                   @PathVariable String cardNumber) {
        return ResponseEntity.ok(cardsService.checkBalance(series, number, cardNumber));
    }

    /***
     * Получает информацию о конкретной карте, идентифицируемой по серии, номеру паспорта клиента и номеру карты
     * @param series серия паспорта
     * @param number номер паспорта
     * @param cardNumber номер карты
     * @return ответ содержащий CardDTO с реквизитами запрошенной карты
     */
    @Operation(summary = "Получить информацию по карте",
            description = "Необходимо вводить серию, номер паспорта, а также номер карты, по которой необходима информация")
    @GetMapping("/details/{series}/{number}/{cardNumber}")
    public ResponseEntity<CardDTO> getCardDetails(@PathVariable Integer series, @PathVariable Integer number,
                                                  @PathVariable String cardNumber) {
        return ResponseEntity.ok(cardMapper.convertCardToDto(cardsService.getCardDetails(series, number, cardNumber)));
    }
}