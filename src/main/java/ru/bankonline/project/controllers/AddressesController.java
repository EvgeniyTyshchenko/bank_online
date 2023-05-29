package ru.bankonline.project.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.bankonline.project.dto.AddressDTO;
import ru.bankonline.project.services.addressesservice.AddressesService;
import ru.bankonline.project.utils.validators.AddressValidator;

import java.util.List;

import static ru.bankonline.project.utils.exceptions.ErrorResponse.checkIfThereErrorInTheUpdate;

/***
 * Контроллер для работы с адресами клиентов
 */
@Slf4j
@RestController
@RequestMapping("/addresses")
@Tag(name = "Адреса", description = "CRUD-операции для работы с адресами")
public class AddressesController {

    private final AddressesService addressesService;
    private final AddressValidator addressValidator;
    private final ModelMapper modelMapper;

    @Autowired
    public AddressesController(AddressesService addressesService, AddressValidator addressValidator, ModelMapper modelMapper) {
        this.addressesService = addressesService;
        this.addressValidator = addressValidator;
        this.modelMapper = modelMapper;
    }

    /***
     * Получает все адреса клиентов банка
     * @return список адресов клиентов
     */
    @Operation(summary = "Получение всех адресов клиентов банка")
    @GetMapping(value = "/getAll")
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        return ResponseEntity.ok(AddressDTO.convertListAddressesToDTO(addressesService.getAllCustomerAddresses(),
                modelMapper));
    }

    /***
     * Обновляет адрес действующего клиента по серии и номеру паспорта
     * @param series серия паспорта
     * @param number номер паспорта
     * @param addressDTO новый адрес клиента
     * @param bindingResult результаты проверки валидации данных адреса
     * @return статус 200 в случае успешного обновления адреса
     */
    @Operation(summary = "Обновление адреса клиента",
            description = "Необходимо вводить серию и номер паспорта клиента, у которого необходимо обновить " +
                    "информацию, далее, заполнить поля в формате JSON")
    @PutMapping("/series/{series}/number/{number}")
    public ResponseEntity<HttpStatus> updateAddress(@PathVariable Integer series, @PathVariable Integer number,
                                                    @RequestBody AddressDTO addressDTO, BindingResult bindingResult) {
        log.info("Обновление адреса в базе по серии {} и номеру {} паспорта", series, number);
        checkIfThereErrorInTheUpdate(bindingResult, addressValidator, addressDTO);
        addressesService.updateAddress(series, number, AddressDTO.convertToAddress(addressDTO, modelMapper));
        return ResponseEntity.ok(HttpStatus.OK);
    }
}