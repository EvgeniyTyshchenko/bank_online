package ru.bankonline.project.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.bankonline.project.dto.CustomerDTO;
import ru.bankonline.project.services.customersservice.CustomersService;
import ru.bankonline.project.utils.validators.CustomerValidator;
import ru.bankonline.project.utils.validators.FullCustomerValidator;

import static ru.bankonline.project.utils.exceptions.ErrorResponse.checkIfThereErrorInTheCreation;
import static ru.bankonline.project.utils.exceptions.ErrorResponse.checkIfThereErrorInTheUpdate;

/***
 * Контроллер для работы с клиентами
 */
@RestController
@RequestMapping("/customers")
@Tag(name = "Клиенты", description = "CRUD-операции для работы с клиентами")
@Slf4j
public class CustomersController {

    private final CustomersService customersService;
    private final FullCustomerValidator fullCustomerValidator;
    private final CustomerValidator customerValidator;
    private final ModelMapper modelMapper;

    @Autowired
    public CustomersController(CustomersService customersService, FullCustomerValidator fullCustomerValidator,
                               CustomerValidator customerValidator, ModelMapper modelMapper) {
        this.customersService = customersService;
        this.fullCustomerValidator = fullCustomerValidator;
        this.customerValidator = customerValidator;
        this.modelMapper = modelMapper;
    }

    /***
     * Получает информацию о клиенте по серии и номеру паспорта
     * @param series серия паспорта
     * @param number номер паспорта
     * @return информацию о клиенте в формате DTO
     */
    @Operation(summary = "Поиск клиента в базе",
            description = "Необходимо вводить серию и номер паспорта")
    @GetMapping("/series/{series}/number/{number}")
    public ResponseEntity<CustomerDTO> getCustomerByPassportSeriesAndNumber(@PathVariable Integer series,
                                                                            @PathVariable Integer number) {
        log.info("Поиск клиента в базе по серии {} и номеру {} паспорта", series, number);
        return ResponseEntity.ok(CustomerDTO
                .convertToDTOTheEntireCustomerAndCardsAndAccounts(customersService
                                .customerSearchByPassportSeriesAndNumber(series, number), modelMapper));
    }

    /***
     * Добавляет нового клиента
     * @param customerDTO DTO клиента, содержащий информацию о клиенте, его адресе и контактных данных
     * @param bindingResult результат валидации DTO
     * @return статус 200 при успешном добавлении
     */
    @Operation(summary = "Добавить/зарегистрировать нового клиента",
            description = "Необходимо заполнить поля в формате JSON")
    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> addNewCustomer(@RequestBody CustomerDTO customerDTO,
                                                     BindingResult bindingResult) {
        checkIfThereErrorInTheCreation(bindingResult, fullCustomerValidator, customerDTO);
        customersService.addNewCustomer(CustomerDTO.convertToCustomerWithAddressAndContacts(customerDTO, modelMapper));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    /***
     * Обновляет информацию о клиенте в базе данных банка
     * @param series серия паспорта
     * @param number номер паспорта
     * @param customerDTO объект с обновленной информацией о клиенте
     * @param bindingResult результат валидации
     * @return статус 200 в случае успешного обновления информации о клиенте
     */
    @Operation(summary = "Обновление информации о клиенте",
            description = "Необходимо вводить серию и номер паспорта клиента, у которого необходимо обновить " +
                    "информацию, далее, заполнить поля в формате JSON")
    @PatchMapping("/update/series/{series}/number/{number}")
    public ResponseEntity<HttpStatus> updateCustomer(@PathVariable Integer series, @PathVariable Integer number,
                                                     @RequestBody CustomerDTO customerDTO, BindingResult bindingResult) {
        log.info("Обновление клиента в базе по серии {} и номеру {} паспорта", series, number);
        checkIfThereErrorInTheUpdate(bindingResult, customerValidator, customerDTO);
        customersService.updateCustomer(series, number, CustomerDTO.convertToCustomer(customerDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    /***
     * Закрывает учетную запись клиента по серии и номеру паспорта
     * @param series серия паспорта
     * @param number номер паспорта
     * @return статус ответа HTTP
     */
    @Operation(summary = "Закрытие учетной записи клиента",
            description = "Необходимо вводить серию и номер паспорта клиента, для закрытия учетной записи")
    @PatchMapping("/close/series/{series}/number/{number}")
    public ResponseEntity<HttpStatus> closingCustomer(@PathVariable Integer series,
                                                     @PathVariable Integer number) {
        log.info("Закрытие учетной записи клиента по серии {} и номеру {} паспорта", series, number);
        customersService.closingCustomer(series, number);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    /***
     * Получает информацию о клиенте по номеру банковской карты
     * @param cardNumber номер карты
     * @return объект с информацией о клиенте в формате CustomerDTO
     */
    @Operation(summary = "Поиск клиента по номеру карты",
            description = "Необходимо вводить номер карты, для получения информации о владельце")
    @GetMapping("/cardNumber/{cardNumber}")
    public ResponseEntity<CustomerDTO> getCustomerByCardNumber(@PathVariable String cardNumber) {
        log.info("Поиск клиента по номеру карты {}", cardNumber);
        return ResponseEntity.ok(CustomerDTO
                .convertToDTOCustomerWithAddressAndContacts(customersService
                                .getCustomerByCardNumber(cardNumber), modelMapper));
    }

    /***
     * Получает информацию о клиенте по номеру сберегательного счета
     * @param accountNumber номер сберегательного счета
     * @return информацию о клиенте в формате CustomerDTO
     */
    @Operation(summary = "Поиск клиента по номеру сберегательного счета",
            description = "Необходимо вводить номер сберегательного счета, для получения информации о владельце")
    @GetMapping("/accountNumber/{accountNumber}")
    public ResponseEntity<CustomerDTO> getCustomerBySavingAccountNumber(@PathVariable String accountNumber) {
        log.info("Поиск клиента по номеру счета {}", accountNumber);
        return ResponseEntity.ok(CustomerDTO
                .convertToDTOCustomerWithAddressAndContacts(customersService
                                .getCustomerBySavingAccountNumber(accountNumber), modelMapper));
    }
}