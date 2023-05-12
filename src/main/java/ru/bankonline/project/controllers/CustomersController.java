package ru.bankonline.project.controllers;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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

@RestController
@RequestMapping("/customers")
@Slf4j
public class CustomersController {

    private final CustomersService customersService;
    private final FullCustomerValidator fullCustomerValidator;
    private final CustomerValidator customerValidator;
    private final ModelMapper modelMapper;

    public CustomersController(CustomersService customersService, FullCustomerValidator fullCustomerValidator,
                               CustomerValidator customerValidator, ModelMapper modelMapper) {
        this.customersService = customersService;
        this.fullCustomerValidator = fullCustomerValidator;
        this.customerValidator = customerValidator;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/series/{series}/number/{number}")
    public ResponseEntity<CustomerDTO> getCustomerByPassportSeriesAndNumber(@PathVariable Integer series,
                                                                            @PathVariable Integer number) {
        log.info("Поиск клиента в базе по серии {} и номеру {} паспорта", series, number);
        return ResponseEntity.ok(CustomerDTO.convertToDTOCustomerCardsAndAccounts(customersService.customerSearchByPassportSeriesAndNumber(series, number),
                modelMapper));
    }

    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> addNewCustomer(@RequestBody CustomerDTO customerDTO,
                                                     BindingResult bindingResult) {
        checkIfThereErrorInTheCreation(bindingResult, fullCustomerValidator, customerDTO);
        customersService.addNewCustomer(CustomerDTO.convertToCustomer(customerDTO, modelMapper));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/series/{series}/number/{number}")
    public ResponseEntity<HttpStatus> updateCustomer(@PathVariable Integer series, @PathVariable Integer number,
                                                     @RequestBody CustomerDTO customerDTO, BindingResult bindingResult) {
        log.info("Обновление клиента в базе по серии {} и номеру {} паспорта", series, number);
        checkIfThereErrorInTheUpdate(bindingResult, customerValidator, customerDTO);
        customersService.updateCustomer(series, number, CustomerDTO.convertToCustomer(customerDTO, modelMapper));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/series/{series}/number/{number}")
    public ResponseEntity<HttpStatus> deleteCustomer(@PathVariable Integer series,
                                                     @PathVariable Integer number) {
        log.info("Удаление клиента по серии {} и номеру {} паспорта", series, number);
        customersService.deleteCustomer(series, number);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/cardNumber/{cardNumber}")
    public ResponseEntity<CustomerDTO> getCustomerByCardNumber(@PathVariable String cardNumber) {
        log.info("Поиск клиента по номеру карты {}", cardNumber);
        return ResponseEntity.ok(CustomerDTO.convertToDTOCustomer(customersService.getCustomerByCardNumber(cardNumber),
                modelMapper));
    }

    @GetMapping("/accountNumber/{accountNumber}")
    public ResponseEntity<CustomerDTO> getCustomerBySavingAccountNumber(@PathVariable String accountNumber) {
        log.info("Поиск клиента по номеру сберегательного счета {}", accountNumber);
        return ResponseEntity.ok(CustomerDTO.convertToDTOCustomer(customersService.getCustomerBySavingAccountNumber(accountNumber),
                modelMapper));
    }
}